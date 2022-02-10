import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class KeyTest {
    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() {
        try {
            String text = "아이디 날아갑니다~~~~1313123213213123123121231313dfgfgtreg싷라ㅣ후구ㅑㅐ루하ㅣ구ㅐ호ㅜ릭사ㅜㅎ라ㅜㅑㄱ힐ㅇ후3";
            Cipher cipher = Cipher.getInstance("RSA");

            Key rsaPublicKeyFromStore = getPublicKey2();
            Key rsaPrivateKeyFromStore = getPrivateKey2();


            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKeyFromStore);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            System.out.println("암호화된 텍스트 -> " + new String(encrypted));

            // decrypt the text
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKeyFromStore);
            String decrypted = new String(cipher.doFinal(encrypted));
            System.out.println("복호화된 텍스트 -> " + decrypted);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static Key getPrivateKey() throws Exception {
        String keystorePath = "c:/work/apiEncryptionKey.jks";
        String keystorePwd = "test1234";
        String alias = "apiEncryptionKey";
        String keyPwd = "test1234";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Key key = keystore.getKey(alias, keyPwd.toCharArray());
        return key;
    }
    private static Key getPublicKey() throws Exception {
        String keystorePath = "c:/work/publicKey.jks";
        String keystorePwd = "test1111";
        String alias = "trustServer";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Certificate certificate = keystore.getCertificate(alias);
        PublicKey publicKey = certificate.getPublicKey();


        return publicKey;
    }



    private static Key getPrivateKey2() throws Exception {
        String keystorePath = "c:/work/ttt.jks";
        String keystorePwd = "test1234";
        String alias = "ttt";
        String keyPwd = "test1234";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Key key = keystore.getKey(alias, keyPwd.toCharArray());
        return key;
    }
    private static Key getPublicKey2() throws Exception {
        String keystorePath = "c:/work/ppp.jks";
        String keystorePwd = "test1111";
        String alias = "trustppp";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Certificate certificate = keystore.getCertificate(alias);
        PublicKey publicKey = certificate.getPublicKey();


        return publicKey;
    }
}