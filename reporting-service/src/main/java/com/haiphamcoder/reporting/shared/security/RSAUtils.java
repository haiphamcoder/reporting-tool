package com.haiphamcoder.reporting.shared.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.haiphamcoder.reporting.shared.security.exception.DecryptException;
import com.haiphamcoder.reporting.shared.security.exception.EncryptException;
import com.haiphamcoder.reporting.shared.security.exception.GenerateKeyException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
@Slf4j
public class RSAUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String NO_SUCH_ALGORITHM = "No such algorithm";
    private static final String NO_SUCH_PADDING = "No such padding";
    private static final String INVALID_KEY = "Invalid key";
    private static final String ILLEGAL_BLOCK_SIZE = "Illegal block size";
    private static final String BAD_PADDING = "Bad padding";

    /**
     * Generate RSA key pair with given key size
     * 
     * @param keySize key size
     * @return RSA key pair with public and private key
     */
    public static RSAKeyPair generateKeyPair(int keySize) throws GenerateKeyException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            return RSAKeyPair.builder()
                    .publicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()))
                    .privateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new GenerateKeyException(NO_SUCH_ALGORITHM, e);
        }
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
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
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
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
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
    public static String encrypt(String data, String publicKey) throws EncryptException {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptException(NO_SUCH_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptException(NO_SUCH_PADDING, e);
        } catch (InvalidKeyException e) {
            throw new EncryptException(INVALID_KEY, e);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptException(ILLEGAL_BLOCK_SIZE, e);
        } catch (BadPaddingException e) {
            throw new EncryptException(BAD_PADDING, e);
        }
    }

    /**
     * Decrypt data with private key
     * 
     * @param data       encrypted data
     * @param privateKey private key
     * @return decrypted data
     * @throws Exception if decryption fails
     */
    public static String decrypt(byte[] data, PrivateKey privateKey) throws DecryptException {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException e) {
            throw new DecryptException(NO_SUCH_ALGORITHM, e);
        } catch (NoSuchPaddingException e) {
            throw new DecryptException(NO_SUCH_PADDING, e);
        } catch (InvalidKeyException e) {
            throw new DecryptException(INVALID_KEY, e);
        } catch (IllegalBlockSizeException e) {
            throw new DecryptException(ILLEGAL_BLOCK_SIZE, e);
        } catch (BadPaddingException e) {
            throw new DecryptException(BAD_PADDING, e);
        }
    }

    /**
     * Decrypt data with private key
     * 
     * @param data             encrypted data
     * @param base64PrivateKey base64 private key
     * @return decrypted data
     * @throws Exception if decryption fails
     */
    public static String decrypt(String data, String base64PrivateKey) throws DecryptException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

}
