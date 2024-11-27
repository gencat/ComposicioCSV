package cat.gencat.ctti.std.composicio.config;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class DumbX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    	//do nothing
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    	//do nothing
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
    }
}