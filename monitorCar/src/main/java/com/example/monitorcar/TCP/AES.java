package com.example.monitorcar.TCP;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private byte[] IV = new byte[]{0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0x0};

    private byte[] getMD5(byte[] source) {
        byte tmp[] = null;
        String result = "";
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            tmp = md.digest();

            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < tmp.length; offset++) {
                i = tmp[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
//			System.out.println("MD5(" + source + ") = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }

    public static void main(String[] args) {
        AES aes = new AES();
        String content = "test";
        String password = "helloworld!";
        System.out.println("加密前：" + content);
        byte[] encryptResult = aes.encrypt(content, password);
        System.out.println("加密后：" + new String(encryptResult));
        byte[] decryptResult = aes.decrypt(encryptResult, password);
        System.out.println("解密后：" + new String(decryptResult));
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public byte[] decrypt(byte[] content, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(getMD5(key.getBytes()), "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NOPadding");

            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] result = cipher.doFinal(content);
            return result;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encrypt(String content, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(getMD5(key.getBytes()), "AES");
            Cipher cipher = Cipher.getInstance("AES/CFB/NOPadding");

            IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] result = cipher.doFinal(content.getBytes());
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

}
