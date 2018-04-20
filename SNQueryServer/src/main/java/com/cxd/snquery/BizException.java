package com.cxd.snquery;

public class BizException extends RuntimeException{


    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
