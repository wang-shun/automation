package com.gome.test.mock.utils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by chaizhongbao on 2015/9/22.
 */
public class KeyStoreUtil {

    private static final Logger log = new Logger(KeyStoreUtil.class);

    public static String SECURITY_FILE_PATH = "keystore/server.keystore";
    // TODO: which protocols will be adopted?
    public static String PROTOCOL = "TLS";
    public static String KEY_STORE_PASSWORD = "aerohive";
    public static String KEY_STORE_ALIAS = "alias";
    public static File file = null;

    public static KeyStore ks = null;

    static {
        file = new File(SECURITY_FILE_PATH);
    }

    //
    public static KeyStore getKeyStore(File file) {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            ks = KeyStore.getInstance("JKS");
            ks.load(in, KEY_STORE_PASSWORD.toCharArray());
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ks;
    }

    public static KeyManagerFactory getKeyManagerFactory(KeyStore keyStore) {
        // Set up key manager factory to use our key store
        KeyManagerFactory kmf = null;
        try {
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, KEY_STORE_PASSWORD.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return kmf;
    }

    public static TrustManagerFactory getTrustManagerFactory(KeyStore keyStore) {
        // set up trust manager factory to use our trust store
        TrustManagerFactory tmf = null;
        try {
            // set up trust manager factory to use our trust store
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return tmf;
    }

    public static PublicKey getPublicKey() {

        // 公钥类所对应的类
        PublicKey pubkey = null;
        try {
            java.security.cert.Certificate cert = getKeyStore(file).getCertificate(KEY_STORE_ALIAS);
            pubkey = cert.getPublicKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return pubkey;
    }


    public static PrivateKey getPrivateKey(String keyAlias, String keyAliasPass) {

        PrivateKey prikey = null;
        try {
            prikey = (PrivateKey) getKeyStore(file).getKey(keyAlias, keyAliasPass.toCharArray());
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return prikey;
    }
}
