package com.haiphamcoder.dataprocessing.shared;

import java.util.zip.CRC32;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HashingUtils {

    public static String CRC32String(String text) {
        CRC32 crc32 = new CRC32();
        crc32.update(text.getBytes());
        return Long.toHexString(crc32.getValue());
    }

    public static String hashingText(String text) {
        return CRC32String(text);
    }
    
}
