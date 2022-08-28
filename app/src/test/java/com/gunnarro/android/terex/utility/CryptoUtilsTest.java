package com.gunnarro.android.terex.utility;

import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class CryptoUtilsTest {

    // The AES algorithm requires that the key size must be 16 bytes
   // private String key = "this is crypt key for AES";

    /*
    @Test
    public void encryptDecrypt() {
        try {
            String encryptedValue = CryptoUtils.encrypt( "password", "encrypt this text");
            System.out.println(encryptedValue);
            org.junit.Assert.assertNotNull(encryptedValue);
            org.junit.Assert.assertEquals("encrypt this text", CryptoUtils.decrypt("password", encryptedValue));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
*/

    String plainText = "gunnarro";
    String password = "password-as-key";
    String salt = "12345678";
    String cipherText;

    //ROiOAAAnkuYV9ZohZbOVTQ==
    @Test
    public void encrypt()
            throws InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException,
            InvalidAlgorithmParameterException, NoSuchPaddingException {
        IvParameterSpec ivParameterSpec = CryptoUtils.generateIv();
        SecretKey key = CryptoUtils.generateKeyFromPassword(password,salt);
        cipherText = CryptoUtils.encryptPasswordBased(plainText, key, ivParameterSpec);
        System.out.println("encrypted: " + plainText + " -> " + cipherText);
    }

    @Test
    public void xdecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        System.out.println("encrypted: " + plainText + " -> " + cipherText);
        IvParameterSpec ivParameterSpec = CryptoUtils.generateIv();
        SecretKey key = CryptoUtils.generateKeyFromPassword(password,salt);
        String decryptedCipherText = CryptoUtils.decryptPasswordBased(cipherText, key, ivParameterSpec);
        System.out.println("decrypted: " + plainText + " -> " + cipherText + " -> " + decryptedCipherText);
    }
}
