package com.fsc.common.utils;

public class APIException extends Exception {
    private static final long serialVersionUID = 6399141738182629421L;
    private int retCode;
    private int errCode;
    private String msg;

    public APIException(int retCode, int errCode, String msg) {
        super(msg);
        this.retCode = retCode;
        this.errCode = errCode;
        this.msg = msg;
    }

    public int getRetCode() {
        return this.retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public int getErrCode() {
        return this.errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

