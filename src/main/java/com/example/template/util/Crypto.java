package com.example.template.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Crypto {
    static int RSAlengthLine = 64;
    static int RSAbits = 2048;

    public static byte[] base64Encode(byte[] bytesDecoded) {
        return Base64.getEncoder().encode(bytesDecoded);
    }

    public static byte[] base64Decode(byte[] bytesEncoded) {
        return Base64.getDecoder().decode(bytesEncoded);
    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(RSAbits);
        return generator.generateKeyPair();
    }

    public static RSAPrivateKey getPrivateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    public static RSAPublicKey getPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    public static RSAPrivateKey getPrivateKey(File file) throws Exception {
        String pem = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        return getPrivateKey(pem);
    }

    public static RSAPublicKey getPublicKey(File file) throws Exception {
        String pem  = new String(Files.readAllBytes(file.toPath()), Charset.defaultCharset());
        return getPublicKey(pem);
    }

    public static RSAPrivateKey getPrivateKey(String pem) throws Exception {
        String privateKeyPEM = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encodedKey = base64Decode(privateKeyPEM.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    public static RSAPublicKey getPublicKey(String pem) throws Exception {
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .replaceAll("\r", "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encodedKey = base64Decode(publicKeyPEM.getBytes());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(encodedKey);
        return (RSAPublicKey) keyFactory.generatePublic(x509EncodedKeySpec);
    }

    public static RSAPublicKey getPublicKey(RSAPrivateKey rsaPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey) rsaPrivateKey;
        RSAPublicKeySpec rsaPublicKeySpec = new java.security.spec.RSAPublicKeySpec(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent());

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(rsaPublicKeySpec);
    }

    public static String getPemStringPrivateKey(RSAPrivateKey rsaPrivateKey) {
        String content = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
        int lengthContent = content.length();
        int countRows = (int) Math.ceil((double)lengthContent / RSAlengthLine);

        String stringPem = "-----BEGIN PRIVATE KEY-----" + "\n";
        for (int ii = 0; ii < countRows; ++ii) {
            if (ii == countRows - 1) {
                stringPem += content.substring(ii * RSAlengthLine) + "\n";
            }
            else {
                stringPem += content.substring(ii * RSAlengthLine, (ii + 1) * RSAlengthLine) + "\n";
            }
        }
        stringPem += "-----END PRIVATE KEY-----";

        return stringPem;
    }

    public static String getPemStringPublicKey(RSAPublicKey rsaPublicKey) {
        String content = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        int lengthContent = content.length();
        int countRows = (int) Math.ceil((double)lengthContent / RSAlengthLine);

        String stringPem = "-----BEGIN PUBLIC KEY-----" + "\n";
        for (int ii = 0; ii < countRows; ++ii) {
            if (ii == countRows - 1) {
                stringPem += content.substring(ii * RSAlengthLine) + "\n";
            }
            else {
                stringPem += content.substring(ii * RSAlengthLine, (ii + 1) * RSAlengthLine) + "\n";
            }
        }
        stringPem += "-----END PUBLIC KEY-----";

        return stringPem;
    }

    public static String encryptPublic(String plainText, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] byteEncrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(byteEncrypted);
    }

    public static String decryptPrivate(String encryptedText, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] byteEncrypted = Base64.getDecoder().decode(encryptedText.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] byteDecrypted = cipher.doFinal(byteEncrypted);
        return new String(byteDecrypted, "utf-8");
    }

    public static String encryptPrivate(String plainText, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] byteEncrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(byteEncrypted);
    }

    public static String decryptPublic(String encryptedText, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance("RSA");
        byte[] byteEncrypted = Base64.getDecoder().decode(encryptedText.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] byteDecrypted = cipher.doFinal(byteEncrypted);
        return new String(byteDecrypted, "utf-8");
    }

    public static String algorithmAES = "AES/CBC/PKCS5Padding";

    // key: 32 bytes, iv: 16 bytes
    public static String encryptAES(String plainText, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithmAES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParamSpec);

            byte[] byteEncrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(byteEncrypted);
        } catch (Exception e) {
            return null;
        }
    }

    // key: 32 bytes, iv: 16 bytes
    public static String decryptAES(String encryptedText, String key, String iv) {
        try {
            Cipher cipher = Cipher.getInstance(algorithmAES);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParamSpec);

            byte[] byteEncrypted = Base64.getDecoder().decode(encryptedText);
            byte[] byteDecrypted = cipher.doFinal(byteEncrypted);
            return new String(byteDecrypted, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    public static String hashSHA256(String input) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.toString());
            return null;
        }
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hashBytes.length; i++) {
            builder.append(String.format("%02x", hashBytes[i]));
        }
        return builder.toString();
    }
}
