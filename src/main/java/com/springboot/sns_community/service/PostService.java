package com.springboot.sns_community.service;

import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.AlarmArgs;
import com.springboot.sns_community.model.AlarmType;
import com.springboot.sns_community.model.Comment;
import com.springboot.sns_community.model.Post;
import com.springboot.sns_community.model.entity.*;
import com.springboot.sns_community.model.event.AlarmEvent;
import com.springboot.sns_community.producer.AlarmProducer;
import com.springboot.sns_community.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;
    private final AlarmProducer alarmProducer;

    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
        postEntityRepository.save(PostEntity.of(title,body,userEntity));
    }
    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);
        //post exist
        PostEntity postEntity = getPostOrException(postId);
        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,String.format("%s has no permission with %s",userName,postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);

        //post exist
        PostEntity postEntity = getPostOrException(postId);
        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,String.format("%s has no permission with %s",userName,postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);

    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName,Pageable pageable) {
        UserEntity userEntity = getUserOrException(userName);
        return postEntityRepository.findAllByUser(userEntity,pageable).map(Post::fromEntity);
    }
    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = getUserOrException(userName);
        //post exist
        PostEntity postEntity = getPostOrException(postId);

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(
                it ->{
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
                }
        );

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(),postEntity.getId())));
    }
    @Transactional
    public long likeCount(Integer postId) {

        //post exist
        PostEntity postEntity = getPostOrException(postId);

        // count like
        return likeEntityRepository.countByPost(postEntity);
    }
    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        PostEntity postEntity = getPostOrException(postId);
        UserEntity userEntity = getUserOrException(userName);

        //comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(),AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(),postEntity.getId())));
    }

    public Page<Comment> getComment(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity,pageable).map(Comment::fromEntity);
    }
    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND,String.format("%s not founded",postId)));
    }

    private UserEntity getUserOrException(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
    }

}
