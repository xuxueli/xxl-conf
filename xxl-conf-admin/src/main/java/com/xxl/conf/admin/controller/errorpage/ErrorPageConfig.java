package com.xxl.conf.admin.controller.errorpage;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @author xuxueli 2018-06-11 20:50:02
 */
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {

        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/static/html/500.html");
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/static/html/500.html");

        registry.addErrorPages(error404Page, error500Page);
    }

}