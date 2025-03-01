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
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies).filter(cookie -> cookie
                .getName()
                .equals(name))
                .findAny();
    }

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findAny()
                    .ifPresent(cookie -> {
                        cookie.setValue(null);
                        response.addCookie(cookie);
                    });
        }
    }

}
