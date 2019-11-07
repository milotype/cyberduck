package ch.cyberduck.core;

/*
 * Copyright (c) 2002-2014 David Kocher. All rights reserved.
 * http://cyberduck.io/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to:
 * feedback@cyberduck.io
 */

import ch.cyberduck.core.ssl.CertificateStoreX509KeyManager;

import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class KeychainStoreTest {

    private static KeyStore keychain;

    @BeforeClass
    public static void initKeychain() throws Exception {
        try {
            keychain = KeyStore.getInstance("KeychainStore", "Apple");
            keychain.load(null, null);
            KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
            kspkcs12.load(KeychainStoreTest.class.getResourceAsStream("/test.p12"), "test".toCharArray());
            Key key = kspkcs12.getKey("test", "test".toCharArray());
            Certificate[] chain = kspkcs12.getCertificateChain("test");
            keychain.setKeyEntry("myClient", key, "null".toCharArray(), chain);
            keychain.store(null, null);
        }
        catch(NoSuchProviderException e) {
            // assuming we are not on macOS - ignore
        }
    }

    @AfterClass
    public static void removeCertificate() throws Exception {
        keychain.deleteEntry("myClient");
        keychain.store(null, null);
    }

    @Test
    public void testGetAliasesForIssuerDN() {
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("mac"));
        final CertificateStoreX509KeyManager m = new CertificateStoreX509KeyManager(new Host(new TestProtocol()), new DisabledCertificateStore(),
            keychain).init();
        final String[] aliases = m.getClientAliases("RSA", new Principal[]{
            new X500Principal("C=US, O=Apple Inc., OU=Apple Certification Authority, CN=Developer ID Certification Authority")
        });
        assertNotNull(aliases);
        assertFalse(Arrays.asList(aliases).isEmpty());
    }

    @Test
    public void testLoadPrivateKeyFromKeychain() {
        Assume.assumeTrue(System.getProperty("os.name").toLowerCase().startsWith("mac"));
        final CertificateStoreX509KeyManager m = new CertificateStoreX509KeyManager(new Host(new TestProtocol()), new DisabledCertificateStore(),
            keychain).init();
        assertNotNull(m.getPrivateKey("myClient"));
    }
}
