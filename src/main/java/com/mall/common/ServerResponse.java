package com.mall.common;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
    @Getter
    private int status;
    //返回状态码,0为success,1为error
    @Getter
    private String msg;
    //返回消息
    @Getter
    private T data;
    //返回具体泛型数据

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
        int a[]=new int[5];
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
        //{status:number}
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
        //{status:number,msg:string}
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
        //{status:number,data:T}
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
        //{status:number,msg:string,data:T}
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
        //{status:1,msg:"ERROR"}
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMsg) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMsg);
        //{status:number,msg:string}
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMsg) {
        return new ServerResponse<T>(errorCode, errorMsg);
        //{status:number,msg:string,data:T}
    }
}
