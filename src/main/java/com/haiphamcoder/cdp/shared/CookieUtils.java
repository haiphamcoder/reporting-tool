package com.haiphamcoder.cdp.shared;

import lombok.experimental.UtilityClass;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Optional;

@UtilityClass
public class CookieUtils {

    public static final int DEFAULT_MAX_AGE = 7 * 24 * 60 * 60; // 7 days
    public static final String DEFAULT_PATH = "/";
    public static final String DEFAULT_DOMAIN = null;
    public static final boolean DEFAULT_SECURE = false;
    public static final boolean DEFAULT_HTTP_ONLY = true;

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

    public void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean secure, boolean httpOnly, String domain, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    public void addCookie(HttpServletResponse response, String name, String value) {
        addCookie(response, name, value, DEFAULT_MAX_AGE, DEFAULT_SECURE, DEFAULT_HTTP_ONLY, DEFAULT_DOMAIN, DEFAULT_PATH);
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

}
