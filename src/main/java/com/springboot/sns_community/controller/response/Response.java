package com.springboot.sns_community.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

    private String resultCode;
    private T result;

    public static <T> Response <T> success(){ return new Response<T>("SUCCESS",null);}
    public static Response<Void> error(String errorCode) {
        return new Response<>(errorCode, null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }


    public String toStream() {
        if (result == null) {
            return "{"+"\"resultCode\":"+"\""+resultCode+"\"," + "\"result\":"+ null + "}";
        }
        return "{"+"\"resultCode\":"+"\""+resultCode+"\"," + "\"result\":"+"\""+result+"\"" + "}";

    }
}
