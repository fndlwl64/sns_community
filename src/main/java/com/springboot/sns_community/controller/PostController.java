package com.springboot.sns_community.controller;

import com.springboot.sns_community.controller.request.PostCommentRequest;
import com.springboot.sns_community.controller.request.PostCreateRequest;
import com.springboot.sns_community.controller.request.PostModifyRequest;
import com.springboot.sns_community.controller.response.CommentResponse;
import com.springboot.sns_community.controller.response.PostResponse;
import com.springboot.sns_community.controller.response.Response;
import com.springboot.sns_community.model.Post;
import com.springboot.sns_community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    //private final UserEntityRepository userEntityRepository;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId,Authentication authentication) {
        postService.delete(authentication.getName(),postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(authentication.getName(),pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }
    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId, Authentication authentication) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest request, Authentication authentication) {
        postService.comment(postId, authentication.getName(),request.getComment());
        return Response.success();
    }
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> commentList(@PathVariable Integer postId, Pageable pageable, Authentication authentication) {
        //postService.getComment(postId,pageable);
        return Response.success(postService.getComment(postId,pageable).map(CommentResponse::fromComment));
    }


}
