package com.springboot.sns_community.controller;

import com.springboot.sns_community.controller.request.PostCreateRequest;
import com.springboot.sns_community.controller.response.Response;
import com.springboot.sns_community.repository.UserEntityRepository;
import com.springboot.sns_community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserEntityRepository userEntityRepository;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        //user find
        postService.create(request.getTitle(),request.getBody(),authentication.getName());
        //post save
        return Response.success(null);
        //return
    }
}
