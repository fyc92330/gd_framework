package org.chun.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.chun.framework.exception.FrameworkBaseException;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Slf4j
public class StringCryptoUtil {

  private static final String ALG = "AES";
  private static final String CRYPTO_PREFIX_STR = "_GD_FRAME_";
  private static final String CRYPTO_KEY = "7923123063821136";
  private static final byte[] DEFAULT_KEY_BYTE_ARRAY = CRYPTO_KEY.getBytes();

  /** =================================================== encrypt ================================================== */

  /**
   * 字串加密
   *
   * @param paramString
   * @return
   */
  public static String getEncrypt(String paramString) {
    return getEncrypt(paramString, null);
  }

  /**
   * 字串加密
   *
   * @param paramString
   * @param specifiedSecretKey
   * @return
   */
  public static String getEncrypt(String paramString, String specifiedSecretKey) {
    if (Strings.isBlank(paramString) || StringUtils.startsWithIgnoreCase(paramString, CRYPTO_PREFIX_STR)) {
      throw new FrameworkBaseException(String.format("Can't be blank or start with '_GD_FRAME_' into getEncrypt method. param:%s", paramString));
    }

    String encryptString = parseByte2HexStr(encrypt(paramString, specifiedSecretKey));
    return Strings.concat(CRYPTO_PREFIX_STR, encryptString);
  }

  /**
   * 字串加密byte[]
   *
   * @param paramString
   * @param specifiedSecretKey
   * @return
   */
  private static byte[] encrypt(String paramString, String specifiedSecretKey) {
    byte[] arrayOfByte2 = paramString.getBytes(StandardCharsets.UTF_8);
    try {
      SecretKeySpec localSecretKeySpec = getSecretKeySpec(specifiedSecretKey);
      Cipher localCipher = Cipher.getInstance(ALG);
      localCipher.init(1, localSecretKeySpec);
      return localCipher.doFinal(arrayOfByte2);
    } catch (Exception e) {
      log.error("paramString:{}, specifiedSecretKey:{}", paramString, specifiedSecretKey, e);
    }
    return arrayOfByte2;
  }

  /**
   * 加密字串組裝
   *
   * @param paramArrayOfByte
   * @return
   */
  private static String parseByte2HexStr(byte[] paramArrayOfByte) {
    StringBuilder sb = new StringBuilder();
    for (byte b : paramArrayOfByte) {
      String hexStr = Integer.toHexString(b & 0xFF).toUpperCase();
      sb.append(hexStr.length() == 1 ? Strings.concat("0", hexStr) : hexStr);
    }
    return sb.toString();
  }

  /** =================================================== decrypt ================================================== */

  /**
   * 字串解密
   *
   * @param paramString
   * @return
   */
  public static String getDecrypt(String paramString) {
    return getDecrypt(paramString, null);
  }

  /**
   * 字串解密
   *
   * @param paramString
   * @param specifiedSecretKey
   * @return
   */
  public static String getDecrypt(String paramString, String specifiedSecretKey) {
    if (StringUtils.startsWithIgnoreCase(paramString, CRYPTO_PREFIX_STR)) {
      log.warn("Parameter do not need to decrypt. param:{}", paramString);
      return paramString;
    }

    String cryptoString = paramString.substring(CRYPTO_PREFIX_STR.length());
    byte[] decryptString = decrypt(parseHexStr2Byte(cryptoString), specifiedSecretKey);
    return decryptString == null
        ? Strings.EMPTY
        : new String(decryptString, StandardCharsets.UTF_8);
  }

  /**
   * 字串解密轉換
   *
   * @param paramString
   * @return
   */
  public static byte[] parseHexStr2Byte(String paramString) {
    byte[] arrayOfByte = new byte[paramString.length() / 2];
    for (int i = 0; i < paramString.length() / 2; i++) {
      int j = Integer.parseInt(paramString.substring(i * 2, i * 2 + 1), 16);
      int k = Integer.parseInt(paramString.substring(i * 2 + 1, i * 2 + 2), 16);
      arrayOfByte[i] = (byte) (j * 16 + k);
    }
    return arrayOfByte;
  }

  /**
   * 字串解密byte[]
   *
   * @param paramArrayOfByte
   * @return
   */
  public static byte[] decrypt(byte[] paramArrayOfByte) {
    return decrypt(paramArrayOfByte, null);
  }

  /**
   * 字串解密byte[]
   *
   * @param paramArrayOfByte
   * @param specifiedSecretKey
   * @return
   */
  public static byte[] decrypt(byte[] paramArrayOfByte, String specifiedSecretKey) {
    try {
      SecretKeySpec localSecretKeySpec = getSecretKeySpec(specifiedSecretKey);
      Cipher localCipher = Cipher.getInstance(ALG);
      localCipher.init(2, localSecretKeySpec);
      return localCipher.doFinal(paramArrayOfByte);
    } catch (Exception e) {
      log.error("paramString:{}, specifiedSecretKey:{}", paramArrayOfByte, specifiedSecretKey, e);
    }
    return paramArrayOfByte;
  }

  /** =================================================== common ================================================== */

  /**
   * 加密密鑰
   *
   * @param specifiedSecretKey
   * @return
   * @throws Exception
   */
  private static SecretKeySpec getSecretKeySpec(String specifiedSecretKey) throws Exception {
    return getSecretKeySpec(specifiedSecretKey, false);
  }

  /**
   * 加密密鑰
   *
   * @param specifiedSecretKey
   * @param isRandomKey
   * @return
   * @throws Exception
   */
  private static SecretKeySpec getSecretKeySpec(String specifiedSecretKey, boolean isRandomKey) throws Exception {
    byte[] keyByteAry = Strings.isNotBlank(specifiedSecretKey)
        ? specifiedSecretKey.getBytes()
        : isRandomKey
        ? randomKeyBytes()
        : DEFAULT_KEY_BYTE_ARRAY;
    return new SecretKeySpec(keyByteAry, ALG);
  }

  /**
   * 隨機密鑰
   *
   * @return
   * @throws NoSuchAlgorithmException
   */
  private static byte[] randomKeyBytes() throws NoSuchAlgorithmException {
    KeyGenerator localKeyGenerator = KeyGenerator.getInstance(ALG);
    localKeyGenerator.init(128, new SecureRandom(DEFAULT_KEY_BYTE_ARRAY));
    return localKeyGenerator.generateKey().getEncoded();
  }
}
