package com.example.threadproject.service;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    // Secret Key
    private static final String AES_SECRET_KEY = "SixteenByteKey00"; // 16-character key
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public String encrypt(String valueToEncrypt) throws Exception {
        if (valueToEncrypt == null || valueToEncrypt.isEmpty()) {
            throw new IllegalArgumentException("Value to encrypt cannot be null or empty");
        }

        // Create a new secret key from the AES key bytes
        SecretKey key = new SecretKeySpec(AES_SECRET_KEY.getBytes(), ALGORITHM);

        // Get a cipher instance for the specified transformation
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // Initialize cipher for encryption mode
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Encrypt the value and encode it to Base64 to ensure proper encoding
        byte[] encryptedBytes = cipher.doFinal(valueToEncrypt.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String valueToDecrypt) throws Exception {
        if (valueToDecrypt == null || valueToDecrypt.isEmpty()) {
            throw new IllegalArgumentException("Value to decrypt cannot be null or empty");
        }

        // Create a new secret key from the AES key bytes
        SecretKey key = new SecretKeySpec(AES_SECRET_KEY.getBytes(), ALGORITHM);

        // Get a cipher instance for the specified transformation
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // Initialize cipher for decryption mode
        cipher.init(Cipher.DECRYPT_MODE, key);

        // Decode the Base64 encoded value and decrypt it
        byte[] decodedBytes = Base64.getDecoder().decode(valueToDecrypt);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);

        // Return the decrypted string
        return new String(decryptedBytes);
    }
}
