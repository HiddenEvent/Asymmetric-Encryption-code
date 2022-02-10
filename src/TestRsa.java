import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.Cipher;

//import org.junit.Test;
//
//import com.lowagie.text.pdf.codec.Base64;

public class TestRsa {

//    @Test
    public void testKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");
        char[] password = "changeit".toCharArray();

        FileInputStream in = null;
        try {
            in = new FileInputStream("d:/jks/test.jks");
            keyStore.load(in, password);
        } finally {
            if(in != null) {
                in.close();
            }
        }

        byte[] data = "this is sample data".getBytes("utf8");

        Certificate certificate = keyStore.getCertificate("test");

        System.out.println("certificate: ");
        System.out.println(certificate);

        PublicKey publicKey = certificate.getPublicKey();

        Key key = keyStore.getKey("test", "changeit".toCharArray());

        System.out.println("key");
        System.out.println(key);

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherText = cipher.doFinal(data);
        System.out.println("cipher text length: " + cipherText.length);
//        System.out.println("cipher text: " + Base64.encodeBytes(cipherText));

        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(cipherText);

        System.out.println("result: " + new String(result));
    }
}

