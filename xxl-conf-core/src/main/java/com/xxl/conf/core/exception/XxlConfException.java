package com.xxl.conf.core.exception;

/**
 * xxl conf exception
 *
 * @author xuxueli 2018-02-01 19:04:52
 */
public class XxlConfException extends RuntimeException {

    private static final long serialVersionUID = 42L;

    public XxlConfException(String msg) {
        super(msg);
    }

    public XxlConfException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XxlConfException(Throwable cause) {
        super(cause);
    }

}
