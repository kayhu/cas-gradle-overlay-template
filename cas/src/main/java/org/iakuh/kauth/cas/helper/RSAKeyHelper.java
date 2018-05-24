package org.iakuh.kauth.cas.helper;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class RSAKeyHelper {

  private static final String PUBLIC_KEY = "PUBLIC";
  private static final String PRIVATE_KEY = "PRIVATE";
  private static final String DEFAULT_ALGORITHM = "RSA";
  private static final int DEFAULT_KEY_SIZE = 1024;

  public static Map<String, Key> getKeyPair() throws Exception {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM);
    keyPairGenerator.initialize(DEFAULT_KEY_SIZE);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    PublicKey publicKey = keyPair.getPublic();
    PrivateKey privateKey = keyPair.getPrivate();
    Map<String, Key> keyPairMap = new HashMap<>(2);
    keyPairMap.put(PUBLIC_KEY, publicKey);
    keyPairMap.put(PRIVATE_KEY, privateKey);
    return keyPairMap;
  }

  public static String getPublicKey(Map<String, Key> keyPair) throws Exception {
    Key key = keyPair.get(PUBLIC_KEY);
    return encryptBASE64(key.getEncoded());
  }

  public static String getPrivateKey(Map<String, Key> keyPair) throws Exception {
    Key key = keyPair.get(PRIVATE_KEY);
    return encryptBASE64(key.getEncoded());
  }

  public static RSAPublicKey getPublicKey(String publicKey) throws Exception {
    byte[] bytes = decryptBASE64(publicKey);
    return (RSAPublicKey) KeyFactory.getInstance(DEFAULT_ALGORITHM)
        .generatePublic(new X509EncodedKeySpec(bytes));
  }

  public static RSAPrivateKey getPrivateKey(String privateKey) throws Exception {
    byte[] bytes = decryptBASE64(privateKey);
    return (RSAPrivateKey) KeyFactory.getInstance(DEFAULT_ALGORITHM)
        .generatePrivate(new PKCS8EncodedKeySpec(bytes));
  }

  private static byte[] decryptBASE64(String key) throws Exception {
    return Base64.getDecoder().decode(key);
  }

  private static String encryptBASE64(byte[] key) throws Exception {
    return Base64.getEncoder().encodeToString(key);
  }

  public static void main(String[] args) throws Exception {
    Map<String, Key> keyPair = getKeyPair();
    String publicKey = getPublicKey(keyPair);
    System.out.println(publicKey);
    String privateKey = getPrivateKey(keyPair);
    System.out.println(privateKey);
  }
}
