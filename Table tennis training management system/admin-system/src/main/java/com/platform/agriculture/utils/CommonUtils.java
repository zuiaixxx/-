package com.platform.agriculture.utils;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtils {
    public static Long getUserId(HttpServletRequest request){
        return (Long) request.getAttribute("uid");
    }
}
