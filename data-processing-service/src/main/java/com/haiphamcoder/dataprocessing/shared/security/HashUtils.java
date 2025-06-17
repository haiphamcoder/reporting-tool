package com.haiphamcoder.dataprocessing.shared.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.zip.CRC32;

import com.haiphamcoder.dataprocessing.shared.security.exception.HashingException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HashUtils {

    /**
     * Hash data with CRC32 algorithm
     * 
     * @param input data to hash
     * @return hashed data
     */
    public static long hashCRC32(String input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input.getBytes(StandardCharsets.UTF_8));
        return crc32.getValue();
    }

    /**
     * Hash data with SHA-256 algorithm
     * 
     * @param input data to hash
     * @return hashed data
     */
    public static String hashSHA256(String input) throws HashingException {
        return hash(input, "SHA-256");
    }

    /**
     * Hash data with SHA-512 algorithm
     * 
     * @param input data to hash
     * @return hashed data
     */
    public static String hashSHA512(String input) throws HashingException {
        return hash(input, "SHA-512");
    }

    /**
     * Hash data with given algorithm
     * 
     * @param input     data to hash
     * @param algorithm algorithm to use
     * @return hashed data
     */
    private static String hash(String input, String algorithm) throws HashingException {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new HashingException("No such algorithm", e);
        }
    }

    /**
     * Verify hash with given algorithm
     * 
     * @param input     data to verify
     * @param hashed    hashed data
     * @param algorithm algorithm to use
     * @return true if the hash is valid, false otherwise
     */
    public static boolean verifyHash(String input, String hashed, String algorithm) throws HashingException {
        return hash(input, algorithm).equals(hashed);
    }

    /**
     * Verify CRC32 hash
     * 
     * @param input  data to verify
     * @param hashed hashed data
     * @return true if the hash is valid, false otherwise
     */
    public static boolean verifyCRC32(String input, long hashed) {
        return hashCRC32(input) == hashed;
    }

    /**
     * Verify SHA-256 hash
     * 
     * @param input  data to verify
     * @param hashed hashed data
     * @return true if the hash is valid, false otherwise
     */
    public static boolean verifySHA256(String input, String hashed) throws HashingException{
        return verifyHash(input, hashed, "SHA-256");
    }

    /**
     * Verify SHA-512 hash
     * 
     * @param input  data to verify
     * @param hashed hashed data
     * @return true if the hash is valid, false otherwise
     */
    public static boolean verifySHA512(String input, String hashed) throws HashingException {
        return verifyHash(input, hashed, "SHA-512");
    }

}
