package com.springboot.sns_community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.sns_community.controller.request.UserJoinRequest;
import com.springboot.sns_community.exception.ErrorCode;
import com.springboot.sns_community.exception.SnsApplicationException;
import com.springboot.sns_community.model.User;
import com.springboot.sns_community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void join_test() throws Exception{
        String name = "name";
        String password = "password";

        when(userService.join(name,password)).thenReturn(mock(User.class));

        mvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(name,password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void join_fail_test() throws Exception{
        String name = "name";
        String password = "password";

        when(userService.join(name,password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME,""));

        mvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(name,password))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void login() throws Exception{
        String name = "name";
        String password = "password";

        when(userService.login(name,password)).thenReturn("test_login");

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(name,password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void login_fail_userName() throws Exception{
        String name = "name";
        String password = "password";

        when(userService.login(name,password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(name,password))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void login_fail_password() throws Exception{
        String name = "name";
        String password = "password";

        when(userService.login(name,password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        mvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(name,password))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void alarm() throws Exception {
        when(userService.alarmList(any(),any())).thenReturn(Page.empty());
        mvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void alarm_if_not_login() throws Exception {
        when(userService.alarmList(any(),any())).thenReturn(Page.empty());
        mvc.perform(get("/api/v1/users/alarm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
