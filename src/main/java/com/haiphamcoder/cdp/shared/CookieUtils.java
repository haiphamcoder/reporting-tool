package com.haiphamcoder.cdp.shared;

import lombok.experimental.UtilityClass;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class CookieUtils {

    public Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies).filter(cookie -> cookie
                .getName()
                .equals(name))
                .findAny();
    }

    public void addCookie(HttpServletResponse response, CookieProperties cookieProperties) {
        Cookie cookie = new Cookie(cookieProperties.getName(), cookieProperties.getValue());
        cookie.setPath(cookieProperties.getPath());
        cookie.setMaxAge(cookieProperties.getMaxAge());
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setHttpOnly(cookieProperties.isHttpOnly());
        cookie.setDomain(cookieProperties.getDomain());
        response.addCookie(cookie);
    }

    public void addCookie(HttpServletResponse response, String name, String value) {
        CookieProperties cookieProperties = CookieProperties.builder()
                .name(name)
                .value(value)
                .build();
        addCookie(response, cookieProperties);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findAny()
                    .ifPresent(cookie -> {
                        cookie.setValue(null);
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
        }
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
