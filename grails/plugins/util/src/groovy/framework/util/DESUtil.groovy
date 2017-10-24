package framework.util

import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec

/**
 * Created by Administrator on 2014/11/14.
 * @Auth :lch
 * @version :2014/11/14
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder

import java.security.spec.AlgorithmParameterSpec;

public class DESUtil{

//    public static void main(String[] agrs)throws Exception{
//        String str = DESUtil.encode("12345678", "qazwsxed");
//        System.out.println(str);
//        System.out.println(DESUtil.decode("1234567", "557308C0947F783E4506B385662223B1"));
//    }
    public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

    /**
     * DES算法，加密
     *
     * @param data 待加密字符串
     * @param key  加密私钥，长度不能够小于8位
     * @return 加密后的字节数组，一般结合Base64编码使用
     * @throws InvalidAlgorithmParameterException
     * @throws Exception
     */
    public static String encode(String key,String data) throws Exception {
        if(data == null)
            throw new Exception("加密数据不能为空");

        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        //key的长度不能够小于8位字节
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes());
        AlgorithmParameterSpec paramSpec = iv;
        cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);
        byte[] bytes = cipher.doFinal(data.getBytes());
        return byte2hex(bytes);

    }

    /**
     * DES算法，解密
     *
     * @param data 待解密字符串
     * @param key  解密私钥，长度不能够小于8位
     * @return 解密后的字节数组
     * @throws Exception
     * @throws Exception 异常
     */
    public static String decode(String key,String data) throws Exception {
        if(data == null)
            throw new Exception("加密数据不能为空");

        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        //key的长度不能够小于8位字节
        Key secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes());
        AlgorithmParameterSpec paramSpec = iv;
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        return new String(cipher.doFinal(hex2byte(data.getBytes())));


    }

/**
 * 二行制转字符串
 * @param b
 * @return
 */
    private static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    private static byte[] hex2byte(byte[] b) {
        if((b.length%2)!=0)
            throw new IllegalArgumentException();
        byte[] b2 = new byte[b.length/2];
        for (int n = 0; n < b.length; n+=2) {
            String item = new String(b,n,2);
            b2[n/2] = (byte)Integer.parseInt(item,16);
        }
        return b2;
    }

}