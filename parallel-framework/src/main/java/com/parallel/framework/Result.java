package com.parallel.framework;


import com.ctrip.payment.provider.common.constant.ResultCodeEnum;

/**
 * Created by b_lin on 2018/4/18.
 */
public class Result<T> implements Cloneable {

    public Result(String code) {
        Code = code;
    }
    public Result(String code, String msg, T obj) {
        Code =code;
        Msg =msg;
        this.obj =obj;
    }
    public Result(String code, String msg) {
        Code = code;
        Msg = msg;
    }

    private Enum en;

    public Result(Enum en, T obj) {
        this.en = en;
        this.obj = obj;
    }

    private String Code;
    private String Msg;
    private T obj;

    public static final Result success =new Result(ResultCodeEnum.success, "success");
    public static final Result fail =new Result(ResultCodeEnum.fail, "fail");

    public Result(Enum en) {
        this.en = en;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

    public Enum getEnum() {
        return en;
    }

    public void setEnum(Enum en) {
        this.en = en;
    }
}
