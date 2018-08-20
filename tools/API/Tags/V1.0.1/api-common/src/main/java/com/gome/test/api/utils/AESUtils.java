package com.gome.test.api.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.apache.commons.codec.binary.Base64;

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
            return Base64.encodeBase64String(encryptedContent);

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
            return Base64.encodeBase64String(encryptedContent);

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
            byte[] decoded = Base64.decodeBase64(text);
            return decrypt(decoded, ENCRYPT_CIPHER, ENCRYPT_IV);
        } catch (Exception e) {
        }
        return null;
    }

    public final static String decryptByKey(String text, String key) {
        try {
            byte[] decoded = Base64.decodeBase64(text);
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

    public static void writeSource2DstFileByLines(String sourcePath,
                                                  String toPath, String reqMethod, String key) {
        if (StringUtils.isBlank(key)) {
            key = "1234567890123456";
        }
        File sourceFile = new File(sourcePath);
        FileWriter fileWriter = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        String tempString = null;
        try {
            reader = new BufferedReader(new FileReader(sourceFile));
            fileWriter = new FileWriter(toPath);
            writer = new BufferedWriter(fileWriter);

            while ((tempString = reader.readLine()) != null) {
                writer.write(convert2AesEncryText(tempString, reqMethod, key));
                writer.newLine();
            }
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    writer.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static String convert2AesEncryText(String sourceStr,
                                               String reqMethod, String key) {
        String encryptKeyText = encryptByKey(sourceStr, key);
        if ("GET".equalsIgnoreCase(reqMethod)) {
            try {
                return URLEncoder.encode(encryptKeyText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return StringUtils.EMPTY;
            }
        }
        return encryptKeyText;
    }
}
