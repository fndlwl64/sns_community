package com.springboot.sns_community.controller;

import com.springboot.sns_community.controller.request.UserJoinRequest;
import com.springboot.sns_community.controller.request.UserLoginRequest;
import com.springboot.sns_community.controller.response.AlarmResponse;
import com.springboot.sns_community.controller.response.Response;
import com.springboot.sns_community.controller.response.UserJoinResponse;
import com.springboot.sns_community.controller.response.UserLoginResponse;
import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.User;
import com.springboot.sns_community.service.UserService;
import com.springboot.sns_community.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // TODO : implement
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        //join
        User user = userService.join(request.getName(), request.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(),User.class).orElseThrow(
                () -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,
                "Casting to User class failed"));
        return Response.success( userService.alarmList(user.getId(),pageable).map(AlarmResponse::fromEntity));
    }
}
