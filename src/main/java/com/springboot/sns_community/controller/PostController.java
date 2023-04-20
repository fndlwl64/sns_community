package com.springboot.sns_community.controller;

import com.springboot.sns_community.controller.request.PostCreateRequest;
import com.springboot.sns_community.controller.request.PostModifyRequest;
import com.springboot.sns_community.controller.response.PostResponse;
import com.springboot.sns_community.controller.response.Response;
import com.springboot.sns_community.model.Post;
import com.springboot.sns_community.repository.UserEntityRepository;
import com.springboot.sns_community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserEntityRepository userEntityRepository;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(),request.getBody(),authentication.getName());
        return Response.success();
    }
    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId , @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(),request.getBody(),authentication.getName(),postId);
        return Response.success(PostResponse.fromPost(post));
    }
}
