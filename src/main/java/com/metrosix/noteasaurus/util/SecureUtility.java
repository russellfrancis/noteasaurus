package com.metrosix.noteasaurus.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecureUtility {

    public byte[] generateAESKey(int keysize) throws NoSuchAlgorithmException, InvalidKeyException {
        return generateKey("AES", keysize);
    }

    public byte[] encryptAES(byte[] key, byte[] data) throws InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        return encrypt("AES", key, data);
    }
    
    public byte[] decryptAES(byte[] key, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        return decrypt("AES", key, data);
    }

    public byte[] generateKey(String algorithm, int keysize) throws InvalidKeyException, NoSuchAlgorithmException {
       KeyGenerator kgen = KeyGenerator.getInstance(algorithm);
       kgen.init(keysize);
       SecretKey skey = kgen.generateKey();
       return skey.getEncoded();
    }

    public byte[] scramble(byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("The parameter input must be non-null.");
        }
        byte[] scramble = new byte[input.length];
        int j = input.length / 3;
        for (int i = 0; i < input.length; ++i, ++j) {

            scramble[j % input.length] =input[i];
            if (j % 2 == 0) {
                scramble[j % input.length] ^= 0xff;
            }
            if (j % 3 == 0) {
                scramble[j % input.length] ^= 0x9e;
            }
            if (j % 5 == 0) {
                scramble[j % input.length] ^= 0xab;
            }
        }
        return scramble;
    }

    private byte[] encrypt(String algorithm, byte[] key, byte[] data)
    throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException
    {
        SecretKeySpec skeySpec = newSecretKeySpec(key, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(data);
    }

    private byte[] decrypt(String algorithm, byte[] key, byte[] data)
    throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException
    {
        SecretKeySpec skeySpec = newSecretKeySpec(key, algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(data);
    }

    private SecretKeySpec newSecretKeySpec(byte[] key, String algorithm) {
        return new SecretKeySpec(key, algorithm);
    }
}
