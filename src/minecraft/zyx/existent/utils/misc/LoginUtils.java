package zyx.existent.utils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LoginUtils {
    public static String name;
    public static String code;
    private String hwid;

    public boolean auth(String code) {
        this.hwid = getHwid();
        LoginUtils.code = code;
        System.out.println("Welcome");
        return true;
    }

    public String getHwid() {
        try {
            String s = "";
            final String main = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
            final byte[] bytes = main.getBytes("UTF-8");
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] md5 = messageDigest.digest(bytes);
            int i = 0;
            byte[] array;
            for (int length = (array = md5).length, j = 0; j < length; ++j) {
                final byte b = array[j];
                s = String.valueOf(s) + Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3);
                if (i != md5.length - 1) {
                    s = String.valueOf(s) + "-";
                }
                ++i;
            }
            return s;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "Error :(";
        }
    }
}
