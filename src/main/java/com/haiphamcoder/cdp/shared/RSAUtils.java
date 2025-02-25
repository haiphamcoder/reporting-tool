package com.haiphamcoder.cdp.shared;

import javax.crypto.Cipher;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
@Slf4j
public class RSAUtils {

    /**
     * Generate RSA key pair with given key size
     * 
     * @param keySize key size
     * @return RSA key pair with public and private key
     */
    public static RSAKeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            return RSAKeyPair.builder()
                    .publicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                    .privateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()))
                    .build();
        } catch (Exception e) {
            log.error("Failed to generate RSA key pair", e);
        }
        return null;
    }

    /**
     * Get public key from base64 string
     * 
     * @param base64PublicKey base64 public key
     * @return public key
     */
    public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    /**
     * Get private key from base64 string
     * 
     * @param base64PrivateKey base64 private key
     * @return private key
     */
    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * Encrypt data with public key
     * 
     * @param data      data to encrypt
     * @param publicKey public key
     * @return encrypted data
     * @throws Exception if encryption fails
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    /**
     * Decrypt data with private key
     * 
     * @param data       encrypted data
     * @param privateKey private key
     * @return decrypted data
     * @throws Exception if decryption fails
     */
    public static String decrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    /**
     * Decrypt data with private key
     * 
     * @param data             encrypted data
     * @param base64PrivateKey base64 private key
     * @return decrypted data
     * @throws Exception if decryption fails
     */
    public static String decrypt(String data, String base64PrivateKey) throws Exception {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

}
