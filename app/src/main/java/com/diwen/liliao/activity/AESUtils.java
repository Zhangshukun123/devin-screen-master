package com.diwen.liliao.activity;

import android.util.Base64;


import com.diwen.liliao.netty.Constans;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    /**
     * AES加密
     *
     * @param datas 将要加密的内容
     * @return 已经加密的内容
     */
    public static byte[] encrypt(String datas) {
        //不足16字节，补齐内容为差值  
        try {
            byte[] data = datas.getBytes("UTF-8");
            int len = 16 - data.length % 16;
            for (int i = 0; i < len; i++) {
                byte[] bytes = {(byte) 0};
                data =ArrayUtils.concat(data, bytes);
            }
            SecretKeySpec skeySpec = new SecretKeySpec(Constans.aeskey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
       //    String name = parseByte2HexStr(cipher.doFinal(data));
       //    Log.i("tian",name);
            byte[] encrypted = cipher.doFinal(data);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密
     *
     * @return 已经解密的内容
     */
    public static String decrypt(byte[] data) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(Constans.aeskey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decryptData = cipher.doFinal(data);
            int len = 2 + com.diwen.liliao.activity.ByteUtils.byteToInt(decryptData[4]) + 3;
            return new String(com.diwen.liliao.activity.ArrayUtils.noPadding(decryptData, len), "UTF-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] bytedecrypt(byte[] data) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(Constans.aeskey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] hexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 测试加密
     */
    public static String encrypt(String encData, String secretKey, String vector)  {
        try {
            if (secretKey == null) {
                return null;
            }
            if (secretKey.length() != 16) {
                return null;
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] raw = secretKey.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            IvParameterSpec iv = new IvParameterSpec(vector.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(encData.getBytes("utf-8"));
            return Base64.encodeToString(encrypted, Base64.DEFAULT);// 此处使用BASE64做转码。（处于android.util包）
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ";";
    }
}