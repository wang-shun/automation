package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HttpPostTest {
    void testXmlPost1(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = this.getXmlInfo();
            System.out.println("urlStr=" + urlStr);
            System.out.println("xmlInfo=" + xmlInfo);
            out.write(new String(("xmlInfo=" + xmlInfo).getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void testXmlPost2(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String xmlInfo = this.getXmlInfo();
            System.out.println("urlStr=" + urlStr);
            System.out.println("xmlInfo=" + xmlInfo);
            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void testJsonPost1(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String jsonInfo = this.getJsonInfo();
            System.out.println("urlStr=" + urlStr);
            System.out.println("jsonInfo=" + jsonInfo);
            out.write(new String(("jsonInfo=" + jsonInfo).getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void testJsonPost2(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setRequestProperty("Pragma:", "no-cache");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Content-Type", "text/xml");

            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            String jsonInfo = this.getJsonInfo();
            System.out.println("urlStr=" + urlStr);
            System.out.println("jsonInfo=" + jsonInfo);
            out.write(new String(jsonInfo.getBytes("ISO-8859-1")));
            out.flush();
            out.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = "";
            for (line = br.readLine(); line != null; line = br.readLine()) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getXmlInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("<videoSend>");
        sb.append("    <header>");
        sb.append("        <sid>1</sid>");
        sb.append("        <type>service</type>");
        sb.append("    </header>");
        sb.append("    <service name=\"videoSend\">");
        sb.append("        <fromNum>0000021000011001</fromNum>");
        sb.append("           <toNum>33647405</toNum>");
        sb.append("        <videoPath>mnt/5.0.217.50/resources/80009.mov</videoPath>");
        sb.append("        <chargeNumber>0000021000011001</chargeNumber>");
        sb.append("    </service>");
        sb.append("</videoSend>");
        return sb.toString();
    }

    private String getJsonInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"store\": {\"book\": [{ \"category\": \"reference\",\"author\": \"Nigel Rees\",\"title\": \"Sayings of the Century\",");
        sb.append("\"price\": 8.95},{ \"category\": \"fiction\",\"author\": \"Evelyn Waugh\",\"title\": \"Sword of Honour\",\"price\": 12.99},");
        sb.append("{ \"category\": \"fiction\",\"author\": \"Herman Melville\",\"title\": \"Moby Dick\",\"isbn\": \"0-553-21311-3\",\"price\": 8.99}],");
        sb.append("\"bicycle\": {\"color\": \"red\",\"price\": 19.95}}}");
        return sb.toString();
    }

    public static void main(String[] args) {
        String xmlUrl1 = "http://http.gome.com:7777/honeybee/postXml1.do";
        String xmlUrl2 = "http://http.gome.com:7777/honeybee/postXml2.do";
        String jsonUrl1 = "http://http.gome.com:7777/honeybee/postJson1.do";
        String jsonUrl2 = "http://http.gome.com:7777/honeybee/postJson2.do";
        String templateUrl = "http://http.gome.com:7777/honeybee/templateUrl.do";
        new HttpPostTest().testXmlPost1(xmlUrl1);
        new HttpPostTest().testXmlPost2(xmlUrl2);
        new HttpPostTest().testJsonPost1(jsonUrl1);
        new HttpPostTest().testJsonPost2(jsonUrl2);
        new HttpPostTest().testXmlPost1(templateUrl);
        System.exit(1);
    }
}
