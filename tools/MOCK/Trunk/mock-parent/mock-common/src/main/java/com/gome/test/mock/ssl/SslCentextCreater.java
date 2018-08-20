package com.gome.test.mock.ssl;

import com.gome.test.mock.cnst.Ak47Constants;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by chaizhongbao on 2015/9/23.
 */
public class SslCentextCreater implements SslCtxCreateInter {

    public SSLContext sslCtx = null;

    @Override
    public SSLContext createSSLContextInstance(String password, String keystorePath, String trutststorePath) {
        try {
            sslCtx = SSLContext.getInstance(Ak47Constants.PROTOCOL);
            KeyManagerFactory kmf = this.getKeyManagerFactory(password,keystorePath);
            TrustManagerFactory tmf = this.getTrustManagerFactory(password,trutststorePath);
            sslCtx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
        return sslCtx;
    }

    private KeyManagerFactory getKeyManagerFactory(String password, String keystorePath){
        KeyManagerFactory kmf = null;
        try {
            File file = new File(keystorePath);
            // keystore
            KeyStore ks = KeyStore.getInstance(Ak47Constants.CERT_FORMAT);
            InputStream in = new FileInputStream(file);
            ks.load(in, password.toCharArray());

            // Set up key manager factory to use our key store
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password.toCharArray());
            in.close();
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
        return kmf;
    }

    private TrustManagerFactory getTrustManagerFactory(String password, String trutststorePath){
        TrustManagerFactory tmf = null;
        try {
            File file = new File(trutststorePath);
            // keystore
            KeyStore ts = KeyStore.getInstance(Ak47Constants.CERT_FORMAT);
            InputStream in = new FileInputStream(file);
            ts.load(in, password.toCharArray());
            // set up trust manager factory to use our trust store
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ts);
            in.close();
        } catch (Exception e) {
            throw new Error("Failed to initialize the server-side SSLContext", e);
        }
        return tmf;
    }

    //test
    public static void mian (String[] args){
        SSLContext sslContext = SslContextFactory.getSslCtxCreater().createSSLContextInstance("","","");
    }
}
