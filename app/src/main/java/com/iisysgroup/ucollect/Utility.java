package com.iisysgroup.ucollect;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public final class Utility {

    /**
     * URL encode a String
     *
     * @param string
     * @return
     */
    public static String urlEncode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
