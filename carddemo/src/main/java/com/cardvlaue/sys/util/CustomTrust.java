/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cardvlaue.sys.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.CertificatePinner;
import okio.Buffer;

public final class CustomTrust {

    public final X509TrustManager trustManager;
    public final SSLSocketFactory sslSocketFactory;

    public CustomTrust() {
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation
     * just embeds the PEM files in Java strings; most applications will instead read this from a
     * resource file that gets bundled with the application.
     */
    private InputStream trustedCertificatesInputStream() {
        String myCertificationAuthority = "" +
            "-----BEGIN CERTIFICATE-----\n" +
            "MIICuDCCAiGgAwIBAgIJAIs736cLbpTUMA0GCSqGSIb3DQEBBQUAMHUxCzAJBgNV\n" +
            "BAYTAkNOMREwDwYDVQQIDAhTaGFuZ2hhaTERMA8GA1UEBwwIU2hhbmdoYWkxEjAQ\n" +
            "BgNVBAoMCWNhcmR2YWx1ZTESMBAGA1UECwwJY2FyZHZhbHVlMRgwFgYDVQQDDA93\n" +
            "d3cuY3ZiYW9saS5jb20wHhcNMTYxMTI5MDIyMzA2WhcNMTgxMTI5MDIyMzA2WjB1\n" +
            "MQswCQYDVQQGEwJDTjERMA8GA1UECAwIU2hhbmdoYWkxETAPBgNVBAcMCFNoYW5n\n" +
            "aGFpMRIwEAYDVQQKDAljYXJkdmFsdWUxEjAQBgNVBAsMCWNhcmR2YWx1ZTEYMBYG\n" +
            "A1UEAwwPd3d3LmN2YmFvbGkuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKB\n" +
            "gQDN3qqFHz4cBw47OJ2EuY+I+eG+FtPg+obapM9YunHzIslP/ySOyb1Zec6LvxUQ\n" +
            "tJSbrM8FM1UEvWstDI6ZBZ6C62545oF0IS78PyvOBtJYRJY/x26LRUnlSDAoadJl\n" +
            "xVzThRc6VPhHugNVQQSt9WTzdzisA6uU+6dc3RAZkGSbQQIDAQABo1AwTjAdBgNV\n" +
            "HQ4EFgQUdESB48DPV3NnR4eQqt3gb008tzMwHwYDVR0jBBgwFoAUdESB48DPV3Nn\n" +
            "R4eQqt3gb008tzMwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQCZdkn3\n" +
            "xxn0lE2SKHE29zaMvSwOuzaVophRRGBiPH+h1BTL+Cf0ujzHyx3NVngHHXPxNWX8\n" +
            "ZgJRssytSVpdOY05Quw7G4CCyEg+6/RK/RBKluy6RcBkqedAqBbV2qw1wvRPDDGn\n" +
            "1mIVq+RRJDHXNTTI3nRfI6o+BpOojaTEKxG4gQ==\n" +
            "-----END CERTIFICATE-----";
        return new Buffer()
            .writeUtf8(myCertificationAuthority)
            .inputStream();
    }

    /**
     * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a {@code
     * SSLHandshakeException}. <p> <p>This can be used to replace the host platform's built-in
     * trusted certificates with a custom set. This is useful in development where certificate
     * authority-trusted certificates aren't available. Or in production, to avoid reliance on
     * third-party certificate authorities. <p> <p>See also {@link CertificatePinner}, which can
     * limit trusted certificates while still using the host platform's built-in trust store. <p>
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3> <p> <p>Relying on your own
     * trusted certificates limits your server team's ability to update their TLS certificates. By
     * installing a specific set of trusted certificates, you take on additional operational
     * complexity and limit your ability to migrate between certificate authorities. Do not use
     * custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    private X509TrustManager trustManagerForCertificates(InputStream in)
        throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory
            .generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
            KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}