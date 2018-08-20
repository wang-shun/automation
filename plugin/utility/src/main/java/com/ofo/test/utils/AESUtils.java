package com.ofo.test.utils;


import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.testng.annotations.Test;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AESUtils {

    public final static byte[] ENCRYPT_CIPHER = {0x31, 0x32, 0x33, 0x34, 0x35,
            0x36, 0x37, 0x38, 0x39, 0x31, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36};

    public final static byte[] ENCRYPT_IV = {0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
            0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};

    public final static String encryptAndEncoding(String text, byte[] cipher,
                                                  byte[] IV) {
        try {
            BufferedBlockCipher engine = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESFastEngine()));
            engine.init(true,
                    new ParametersWithIV(new KeyParameter(cipher), IV));
            byte[] content = text.getBytes("utf-8");
            byte[] enc = new byte[engine.getOutputSize(content.length)];
            int size1 = engine.processBytes(content, 0, content.length, enc, 0);
            int size2 = engine.doFinal(enc, size1);
            byte[] encryptedContent = new byte[size1 + size2];
            System.arraycopy(enc, 0, encryptedContent, 0,
                    encryptedContent.length);
            return Base64Utils.encodeMessage(encryptedContent) ;//Base64.encodeBase64String(encryptedContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static String encryptByKey(String text, String key) {
        try {
            key = convertTo16Key(key);
            byte[] cipher = key.getBytes();
            BufferedBlockCipher engine = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESFastEngine()));
            engine.init(true, new ParametersWithIV(new KeyParameter(cipher),
                    ENCRYPT_IV));
            byte[] content = text.getBytes("utf-8");
            byte[] enc = new byte[engine.getOutputSize(content.length)];
            int size1 = engine.processBytes(content, 0, content.length, enc, 0);
            int size2 = engine.doFinal(enc, size1);
            byte[] encryptedContent = new byte[size1 + size2];
            System.arraycopy(enc, 0, encryptedContent, 0,
                    encryptedContent.length);

                return Base64Utils.encodeMessage( encryptedContent);
           // Base64.encodeBase64String(encryptedContent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public final static String decrypt(String content, byte[] encCipher,
                                       byte[] encIV) {
        try {
            BufferedBlockCipher engine = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESFastEngine()));
            engine.init(false, new ParametersWithIV(
                    new KeyParameter(encCipher), encIV));
            byte[] encryptedContent = Hex.decode(content.getBytes());
            byte[] dec = new byte[engine.getOutputSize(encryptedContent.length)];
            int size1 = engine.processBytes(encryptedContent, 0,
                    encryptedContent.length, dec, 0);
            int size2 = engine.doFinal(dec, size1);
            byte[] decryptedContent = new byte[size1 + size2];
            System.arraycopy(dec, 0, decryptedContent, 0,
                    decryptedContent.length);
            return new String(decryptedContent, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public final static String decrypt(byte[] content, byte[] encCipher,
                                       byte[] encIV) {
        try {
            BufferedBlockCipher engine = new PaddedBufferedBlockCipher(
                    new CBCBlockCipher(new AESFastEngine()));
            engine.init(false, new ParametersWithIV(
                    new KeyParameter(encCipher), encIV));
            byte[] encryptedContent = content;
            byte[] dec = new byte[engine.getOutputSize(encryptedContent.length)];
            int size1 = engine.processBytes(encryptedContent, 0,
                    encryptedContent.length, dec, 0);
            int size2 = engine.doFinal(dec, size1);
            byte[] decryptedContent = new byte[size1 + size2];
            System.arraycopy(dec, 0, decryptedContent, 0,
                    decryptedContent.length);
            return new String(decryptedContent, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public final static String decodingAndDecrypt(String text) {
        try {
            byte[] decoded = Base64Utils.decodeMessage(text);// Base64.decodeBase64(text);
            return decrypt(decoded, ENCRYPT_CIPHER, ENCRYPT_IV);
        } catch (Exception e) {
        }
        return null;
    }

    public final static String decryptByKey(String text, String key) {
        try {
            byte[] decoded =Base64Utils.decodeMessage(text);// Base64.decodeBase64(text);
            key = convertTo16Key(key);
            return decrypt(decoded, key.getBytes(), ENCRYPT_IV);
        } catch (Exception e) {
        }
        return null;
    }

    public static String getHexString(byte[] b) throws Exception {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] getByteArray(String hexString) {
        return new BigInteger(hexString, 16).toByteArray();
    }

    private static String convertTo16Key(String entryKey) {
        if (StringUtils.isNotBlank(entryKey)) {
            if (entryKey.length() == 16) {
                return entryKey;
            }
            if (entryKey.length() > 16) {
                return entryKey.subSequence(0, 16).toString();
            }
            if (entryKey.length() < 16) {
                return convet2Substi16Byte(entryKey);
            }
        }
        return null;
    }

    private static String convet2Substi16Byte(String key) {
        StringBuffer keyBuffer = new StringBuffer(key);
        for (int i = 0; i < 16 - key.length(); i++) {
            keyBuffer.append("0");
        }
        return keyBuffer.toString();
    }






    /*
    * AES加密(结合base64转码使用)
    * */
    public static byte[] encryptMessageForBASE64(String input, String key) throws Exception{
        byte[] crypted = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes("utf-8"));
        }catch(Exception e){
            System.out.println(e.toString());
        }
        // return new String(Base64Utils.encodeMessage(crypted));
        //return new String(crypted,"UTF-8");
        return crypted;
    }

    /*
    * AES解密（结合base64转码使用）
    * */
    public static String decryptMessageForBASE64(byte[] input, String key){
        byte[] output = null;
        try{
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(input);//Base64Utils.decodeMessage(input)
        }catch(Exception e){
            System.out.println(e.toString());
        }
        //return new String(output);
        try {
            return new String(output, "UTF-8");
        }catch (Exception ex)
        {
            return null;
        }
    }


    public static void main(String[] args) throws Exception{
        String key = "AESKEY0003123456";
      String data="请输入要加密的内容";
       String info=Base64Utils.encodeMessage(encryptMessageForBASE64(data, key));
        String value="Jls1dyRZWwV3fvLeW8enUBib0E6LyojS4GkYQHcjfR7If91ucWtCGxAKZTNjpMyJlaNUYWSqEGYyqiPWgziPAR5kVOOiM9DjcXIOVBd/n6Yr0NbUgld16irciZhAOEkQtV9mlegg14DTJtHllhTQNA==";
        String resultValue="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";
        resultValue=info;
        System.out.println("解密原"+decryptMessageForBASE64(Base64Utils.decodeMessage(resultValue), key));
        //单纯AES加密
        String aesencode=AESUtils.encryptAESOnly(data, key);
       //单纯AES解密
        String aesdecry=AESUtils.decryptAESOlny(aesencode, key);

        System.out.println("单纯AES加密："+aesencode);
        System.out.println("单纯AES解密："+aesdecry);
    }


    @Test
    public void test() throws Exception
    {
        String key = "AESKEY0003123456";
        String data="{\"systemNo\":\"0003\",\"startIndex\":\"1\",\"userNo\":\"201470000\",\"pageSize\":\"10\",\"reqType\":\"P100000010\"}";
//        String data="请输入要加密的内容";
        String aa=AESUtils.encryptAESOnly(data,key);
//         aa="95474E5DD89701145686539BC71ED57975A62F8BE3DFA29056936045021446A6";
        key="AESKEY0003123456";
        String bb= AESUtils.decryptAESOlny(aa,key);


        System.out.println(aa);
        System.out.println(bb);
        System.out.println(aa.equals(bb));
    }





    /**
     * 加密
     *
     * @param content
     *            待加密内容
     * @param key
     *            加密的密钥
     *
     */
    public static String encryptAESOnly(String content, String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(key.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] byteRresult = cipher.doFinal(byteContent);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteRresult.length; i++) {
                String hex = Integer.toHexString(byteRresult[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     *
     * @param content
     *            待解密内容
     * @param key
     *            解密的密钥
     *
     */
    public static String decryptAESOlny(String content, String key) throws Exception{

        if (content.length() < 1)
            return null;
        byte[] byteRresult = new byte[content.length() / 2];
        for (int i = 0; i < content.length() / 2; i++) {
            int high = Integer.parseInt(content.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(content.substring(i * 2 + 1, i * 2 + 2), 16);
            byteRresult[i] = (byte) (high * 16 + low);
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(key.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] result = cipher.doFinal(byteRresult);

            return new String(result,"utf-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }




//
//    public static void writeSource2DstFileByLines(String sourcePath,
//                                                  String toPath, String reqMethod, String key) {
//        if (StringUtils.isBlank(key)) {
//            key = "1234567890123456";
//        }
//        File sourceFile = new File(sourcePath);
//        FileWriter fileWriter = null;
//        BufferedReader reader = null;
//        BufferedWriter writer = null;
//        String tempString = null;
//        try {
//            reader = new BufferedReader(new FileReader(sourceFile));
//            fileWriter = new FileWriter(toPath);
//            writer = new BufferedWriter(fileWriter);
//
//            while ((tempString = reader.readLine()) != null) {
//                writer.write(convert2AesEncryText(tempString, reqMethod, key));
//                writer.newLine();
//            }
//            writer.close();
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                    writer.close();
//                } catch (Exception e) {
//                }
//            }
//        }
//    }
//
//
//
//
//
//    private static String convert2AesEncryText(String sourceStr,
//                                               String reqMethod, String key) {
//        String encryptKeyText = encryptByKey(sourceStr, key);
//        if ("GET".equalsIgnoreCase(reqMethod)) {
//            try {
//                return URLEncoder.encode(encryptKeyText, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//                return StringUtils.EMPTY;
//            }
//        }
//        return encryptKeyText;
//    }

}
