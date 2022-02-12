import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class KeyTest {
    private static final String keyFilePath = System.getProperty("user.dir") + "/src/keys/";
    // 1 * 1000L (1초)
    // 1 * 60 * 1000L (1분)
    // 1 * 60 * 60 * 1000L (1시간)
    // 1 * 24 * 60 * 60 * 1000L (하루)
    private static Long validTime = 5 * 1000L; // 5초
    private static Cipher cipher = null;

    static {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // 고객사에서 보낼 데이터
        String text = "아이디";
        long publicServerMillis = System.currentTimeMillis() - 4 * 1000L;
        String payload = publicServerMillis+"//"+text;
        System.out.println(payload);
        String encryptHexString = encryption(payload);

        // 우리쪽 서버에서 복호화 로그인 처리
        String decryption = decryption(encryptHexString);
        System.out.println(decryption);
        String[] payloads = decryption.split("//");
        String userId = payloads[1];
        long sendTimeMillis = Long.parseLong(payloads[0]);
        boolean isNotExpire = expireCk(sendTimeMillis);
        System.out.println(isNotExpire);

    }

    private static boolean expireCk(long sendTimeMillis) {
        // 유효시간 계산 (5초)
        long term = System.currentTimeMillis() - sendTimeMillis;
        System.out.println(term);
        return term >= 0 && validTime >= term;
    }

    private static String decryption(String encryptHexString) throws Exception {
        Key rsaPrivateKeyFromStore = getPrivateKey();
        cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKeyFromStore);

        byte[] encryptBytes = hexStringToBytes(encryptHexString);
        return new String(cipher.doFinal(encryptBytes));
    }

    private static String encryption(String text) throws Exception {
        Key rsaPrivateKeyFromStore = getPublicKey();
        cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKeyFromStore);

        byte[] bytes = text.getBytes();
        byte[] encryptByte = cipher.doFinal(bytes);
        return toHexString(encryptByte);
    }

    private static Key getPrivateKey() throws Exception {
        String keystorePath = keyFilePath + "apiPrivate.jks";
        String keystorePwd = "test1234";
        String alias = "privateServer";
        String keyPwd = "test1234";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Key key = keystore.getKey(alias, keyPwd.toCharArray());
        return key;
    }

    private static Key getPublicKey() throws Exception {
        String keystorePath = keyFilePath + "apiPublic.jks";
        String keystorePwd = "test1111";
        String alias = "publicServer";
        FileInputStream is = new FileInputStream(keystorePath);
//        키 저장소 유형: PKCS12
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(is, keystorePwd.toCharArray());
        Certificate certificate = keystore.getCertificate(alias);
        PublicKey publicKey = certificate.getPublicKey();

        return publicKey;
    }

    /**
     * unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
     *
     * ByteUtils.toHexString(null) = null
     * ByteUtils.toHexString([(byte)1, (byte)255]) = "01ff"
     *
     * @param bytes unsigned byte's array
     * @return
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) { return null; }

        StringBuffer result = new StringBuffer();
        for (byte b : bytes) {
            result.append(Integer.toString((b & 0xF0) >> 4, 16));
            result.append(Integer.toString(b & 0x0F, 16));
        }
        return result.toString();
    }

        /**
     * 16진수 문자열을 바이트 배열로 변환한다.
     * 문자열의 2자리가 하나의 byte로 바뀐다.
     *
     * ByteUtils.hexStringToBytes(null) = null
     * ByteUtils.hexStringToBytes("0E1F4E") = [0x0e, 0xf4, 0x4e]
     * ByteUtils.hexStringToBytes("48414e") = [0x48, 0x41, 0x4e]
     *
     * @param digits 16진수 문자열
     * @return
     * @throws NumberFormatException
     */
    public static byte[] hexStringToBytes(String digits) throws IllegalArgumentException, NumberFormatException {
        if (digits == null) { return null; }
        int length = digits.length();
        if (length % 2 == 1) { throw new IllegalArgumentException("For input string: \"" + digits + "\""); }
        length = length / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            int index = i * 2;
            bytes[i] = (byte) (Short.parseShort(digits.substring(index, index + 2), 16));
        }
        return bytes;
    }
}