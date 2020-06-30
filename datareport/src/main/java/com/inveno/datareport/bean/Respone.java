package com.inveno.datareport.bean;

public class Respone {

    public int code;

    public String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Respone{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
