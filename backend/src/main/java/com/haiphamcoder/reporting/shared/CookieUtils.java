package com.haiphamcoder.reporting.shared;

import lombok.experimental.UtilityClass;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import org.springframework.util.SerializationUtils;

@UtilityClass
public class CookieUtils {

    /**
     * Get a cookie from the request
     * 
     * @param request the request
     * @param name    the name of the cookie
     * @return the cookie
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies).filter(cookie -> cookie
                .getName()
                .equals(name))
                .findAny();
    }

    /**
     * Add a cookie to the response
     * 
     * @param response         the response
     * @param cookieProperties the cookie properties
     */
    public static void addCookie(HttpServletResponse response, CookieProperties cookieProperties) {
        Cookie cookie = new Cookie(cookieProperties.getName(), cookieProperties.getValue());
        cookie.setPath(cookieProperties.getPath());
        cookie.setMaxAge(cookieProperties.getMaxAge());
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setHttpOnly(cookieProperties.isHttpOnly());
        cookie.setDomain(cookieProperties.getDomain());
        response.addCookie(cookie);
    }

    /**
     * Add a cookie to the response
     * 
     * @param response the response
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        CookieProperties cookieProperties = CookieProperties.builder()
                .name(name)
                .value(value)
                .build();
        addCookie(response, cookieProperties);
    }

    /**
     * Add a cookie to the response
     * 
     * @param response the response
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param maxAge   the max age of the cookie
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * Delete a cookie from the request
     * 
     * @param request the request
     * @param name    the name of the cookie
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findAny()
                    .ifPresent(cookie -> {
                        cookie.setValue("");
                        cookie.setPath("/");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
        }
    }

    /**
     * Delete a cookie from the response
     * 
     * @param response the response
     * @param name     the name of the cookie
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * Serialize an object to a string
     * 
     * @param object the object to serialize
     * @return the serialized object
     */
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Deserialize a string to an object
     * 
     * @param string the string to deserialize
     * @return the deserialized object
     */
    @SuppressWarnings("deprecation")
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
