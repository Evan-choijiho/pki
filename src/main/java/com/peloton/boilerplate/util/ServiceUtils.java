package com.peloton.boilerplate.util;

import com.peloton.boilerplate.exception.ServerSystemException;
import jakarta.xml.bind.DatatypeConverter;

import java.security.MessageDigest;

public class ServiceUtils {
    public static final long adminUserSid = 198361L;

    public static String md5(String s) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            return DatatypeConverter.printHexBinary(md.digest())
                    .toUpperCase();
        } catch (Exception e) {
            throw new ServerSystemException(e);
        }
    }
}
