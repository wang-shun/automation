package com.gome.test.gui.helper;

import com.gome.test.context.IContext;
import com.gome.test.utils.Logger;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientUtils {

    private static String[] statuses = null;

    static {
        String[] status = IContext.compositeConfiguration.getStringArray("link.status");
        if (status == null || status.length == 0)
            statuses = new String[]{"200"};
        else
            statuses = status;
    }

    public static boolean checkSuccess(String url) throws Exception {
        HttpURLConnection uRLConnection = null;

        try {
            uRLConnection = (HttpURLConnection) new URL(url).openConnection();
            uRLConnection.setConnectTimeout(30000);
            uRLConnection.setReadTimeout(30000);
            uRLConnection.setUseCaches(false);
            int code = uRLConnection.getResponseCode();

            for (String status : statuses)
                if (status.equals(String.valueOf(code)))
                    return true;

        } catch (Exception ex) {
            Logger.error("checkFail : %s", ex.getMessage());
        } finally {
            try {
                if (uRLConnection != null)
                    uRLConnection.disconnect();

            } catch (Exception e) {
            }
        }

        return false;
    }
}
