package com.springboot.sns_community.service;

import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.Post;
import com.springboot.sns_community.model.entity.LikeEntity;
import com.springboot.sns_community.model.entity.PostEntity;
import com.springboot.sns_community.model.entity.UserEntity;
import com.springboot.sns_community.repository.LikeEntityRepository;
import com.springboot.sns_community.repository.PostEntityRepository;
import com.springboot.sns_community.repository.UserEntityRepository;
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
    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
        postEntityRepository.save(PostEntity.of(title,body,userEntity));
    }
    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
        //post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND,String.format("%s not founded",postId)));
        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,String.format("%s has no permission with %s",userName,postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    public void delete(String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));

        //post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND,String.format("%s not founded",postId)));
        //post permission
        if(postEntity.getUser() != userEntity){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION,String.format("%s has no permission with %s",userName,postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName,Pageable pageable) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));
        return postEntityRepository.findAllByUser(userEntity,pageable).map(Post::fromEntity);
    }
    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(()->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND,String.format("%s not founded",userName)));

        //post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND,String.format("%s not founded",postId)));

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(
                it ->{
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
                }
        );

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }
    @Transactional
    public int likeCount(Integer postId) {

        //post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(()->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND,String.format("%s not founded",postId)));

        // count like
        return likeEntityRepository.countByPost(postEntity);
    }


}
