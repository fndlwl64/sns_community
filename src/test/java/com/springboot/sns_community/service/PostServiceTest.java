package com.springboot.sns_community.service;

import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.fixture.PostEntityFixture;
import com.springboot.sns_community.fixture.UserEntityFixture;
import com.springboot.sns_community.model.entity.PostEntity;
import com.springboot.sns_community.model.entity.UserEntity;
import com.springboot.sns_community.repository.PostEntityRepository;
import com.springboot.sns_community.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;
    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void create_post(){
        String title = "title";
        String body = "body";
        String userName = "userName";

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(()->postService.create(title,body,userName));

    }

    @Test
    void create_post_if_not_user(){
        String title = "title";
        String body = "body";
        String userName = "userName";

        //mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND,e.getErrorCode());
    }

    @Test
    void modify_post(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId,1);
        UserEntity userEntity = postEntity.getUser();
        //mocking

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(()->postService.modify(title,body,userName,postId));
    }
    @Test
    void modify_post_if_post_not_exist(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId,1);
        UserEntity userEntity = postEntity.getUser();
        //mocking

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,()->postService.modify(title,body,userName,postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND,e.getErrorCode());

    }

    @Test
    void modify_post_if_not_authorized(){
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId,1);
        UserEntity writer = UserEntityFixture.get("userName1","password",postId);
        //mocking

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class,()->postService.modify(title,body,userName,postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION,e.getErrorCode());

    }



}
