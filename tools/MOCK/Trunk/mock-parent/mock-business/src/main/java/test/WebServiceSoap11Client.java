package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceSoap11Client {

    private static String actionBySOAP() {
        String soapRequestData = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:axis=\"http://ws.apache.org/axis2\">";
        soapRequestData += "<soapenv:Header/>";
        soapRequestData += " <soapenv:Body>";
        soapRequestData += "<axis:queryUserList/>";
        soapRequestData += "</soapenv:Body>";
        soapRequestData += "</soapenv:Envelope>";

        return soapRequestData;
    }

    private static String callWebService(String urlPath) throws Exception {
        System.setProperty("sun.net.client.defaultConnectTimeout", "20000");
        System.setProperty("sun.net.client.defaultReadTimeout", "20000");

        // URL连接
        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // conn.setRequestMethod("GET");
        conn.setRequestMethod("POST");
        conn.setRequestProperty("SOAPAction: ", "");
        conn.setRequestProperty("Content-Length", String.valueOf(actionBySOAP().getBytes("UTF-8").length));
        conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(20000);
        // 请求输入内容
        OutputStream output = conn.getOutputStream();
        output.write(actionBySOAP().getBytes("UTF-8"));
        output.flush();
        output.close();
        // 请求返回内容
        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String str = null;
        while ((str = br.readLine()) != null) {
            sb.append(str + "\n");
        }
        br.close();
        isr.close();
        return sb.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /*        String WSDL = "http://test.gome.com:9999/axis2/services/MyListService/queryUserList";
                String result = callWebService(WSDL);
        */
    }

}
