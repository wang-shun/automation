package com.ofo.test.api.utils;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by lizonglin on 2016/3/8/0008.
 */
public class DubboClient {
    private TelnetClient telnet;
    private InputStream in;
    private PrintStream out;
    private String cmd;
    private String ip;
    private int port;

    public DubboClient() {
        telnet = new TelnetClient();
    }

//    public DubboClient(String ip, int port) throws Exception {
//        try {
//            telnet = new TelnetClient();
//            telnet.connect(ip, port);
//            in = telnet.getInputStream();
//            out = new PrintStream(telnet.getOutputStream());
//            this.ip = ip;
//            this.port = port;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//
//    }
//
//    public DubboClient(String ip, int port, String command) throws Exception {
//        try {
//            telnet = new TelnetClient();
//            telnet.connect(ip, port);
//            in = telnet.getInputStream();
//            out = new PrintStream(telnet.getOutputStream());
//            this.ip = ip;
//            this.port = port;
//            this.cmd = command;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//
//    }

//    public DubboClient(String command) throws Exception {
//        try {
//            telnet = new TelnetClient();
//            telnet.connect(ip, port);
//            in = telnet.getInputStream();
//            out = new PrintStream(telnet.getOutputStream());
//            this.cmd = command;
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }

    public void assemble(String ip, int port, String cmd) throws Exception {
        try {
            telnet.connect(ip, port);
            this.in = telnet.getInputStream();
            this.out = new PrintStream(telnet.getOutputStream());
            this.cmd = cmd;
            this.ip = ip;
            this.port = port;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public String send() throws Exception {
        return sendCommand(cmd);
    }
    //向目标发送命令字符串
    public String sendCommand(String command) throws Exception {
        try {
            write(command);
            return readUntil("dubbo>");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    //关闭连接
    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //读取结果(从网络读入内存)
    private String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);//标记位的最后一个字符
            StringBuffer sb = new StringBuffer();
            char ch = (char) in.read();//先读第一个字符
            while (true) {
                sb.append(ch);//拼接读入的字符
                if (ch == lastChar) {//如果该字符等于标记位的最后一个字符
                    if (sb.toString().endsWith(pattern)) {//如果拼接的字符串串以标记字符串结尾
                        String response = new String(sb.toString().getBytes("iso8859-1"), "GBK");
                        return response;
                    }
                }
                ch = (char) in.read();//读入下一个字符
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //写操作(从内存写入网络)
    private void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TelnetClient getTelnet() {
        return telnet;
    }

    public void setTelnet(TelnetClient telnet) {
        this.telnet = telnet;
    }

    public InputStream getIn() {
        return in;
    }

    public void setIn(InputStream in) {
        this.in = in;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static void main(String[] args) {
        try {
            System.out.println("启动Telnet...");
            String ip = "10.126.53.233";
            int port = 20885;
            DubboClient telnet = new DubboClient();
            telnet.assemble(ip, port, "ls");
//            String r1 = telnet.sendCommand(" ");
//            String r2 = telnet.sendCommand("ls");
            String r2 = telnet.send();
            String r3 = telnet.sendCommand("invoke com.ofo.memberCenter.facade.appraise.IAppraiseFacade.findAppraisedList(\"206291022\",\"2165450054\",\"Hao\",1,3)");
            System.out.println("显示结果");
//            System.out.println("r1: \n" + r1);
            System.out.println("r2: \n" + r2);
            System.out.println("r3: \n" + r3);

//            String[] r2Array = r2.split("\n");
//
//            System.out.println(r2Array.length);

            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
