package com.haiphamcoder.cdp.shared;

import lombok.experimental.UtilityClass;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static String serialize(Object object) {
        try {
            return Base64.getUrlEncoder().encodeToString(new ObjectMapper().writeValueAsBytes(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
        try {
            return new ObjectMapper().readValue(bytes, cls);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
