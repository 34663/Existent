package zyx.existent.gui.altmanager.althening.api.utilities;

import java.security.cert.*;
import java.security.*;
import javax.net.ssl.*;

public class SSLVerification
{
    private boolean verified;
    
    public SSLVerification() {
        this.verified = false;
    }
    
    public void verify() {
        if (!this.verified) {
            this.bypassSSL();
            this.whitelistTheAltening();
            this.verified = true;
        }
    }
    
    private void bypassSSL() {
        final TrustManager[] v2 = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] a2, final String v1) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] a2, final String v1) {
                }
            } };
        try {
            final SSLContext v3 = SSLContext.getInstance("SSL");
            v3.init(null, v2, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(v3.getSocketFactory());
        }
        catch (Exception ex) {}
    }
    
    private void whitelistTheAltening() {
        HttpsURLConnection.setDefaultHostnameVerifier((a1, a2) -> a1.equals("authserver.thealtening.com") || a1.equals("sessionserver.thealtening.com"));
    }
}
