package com.use.nice.util;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesUtil {
    private final static String DES = "DES";
    /**
     * Description 根据键值进行加密
     * @param data 
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt( String key,String data) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = Base64.encodeToString(bt, Base64.DEFAULT);//new BASE64Encoder().encode(bt);
        return strs;
    }
 
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String key,String data) throws IOException,
            Exception {
        if (data == null)
            return null;
//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] buf = decoder.decodeBuffer(data);
        byte[] buf=Base64.decode(data.getBytes(), Base64.DEFAULT);
        byte[] bt = decrypt(buf,key.getBytes());
        return new String(bt);
    }
 
    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
     
     
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
 
        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
    
    public static String getUnzip(String zipCode){
	byte[] unzipData = unzip(Base64.decode(zipCode, 0));
	String content = new String(unzipData);
	return content;
    }
    
    public static byte[] unzip(byte[] zipData){
	try {
		ByteArrayOutputStream os=new ByteArrayOutputStream();
	    
		ByteArrayInputStream inputstream = new ByteArrayInputStream(zipData);
        GZIPInputStream v3_1 = new GZIPInputStream(inputstream);
		int count;  
		byte dsata[] = new byte[1024];  
		while ((count = v3_1.read(dsata, 0, 1024)) != -1) {  
	        os.write(dsata, 0, count);  
		}  
		v3_1.close();
		return os.toByteArray();
	} catch (Exception e) {
		return null;
	}
	
}
    
    
    
    public static boolean isBase64(final byte[] arrayOctet) {
	    for (int i = 0; i < arrayOctet.length; i++) {
	        if (!(arrayOctet[i] == '=' || (arrayOctet[i] >= 0 && arrayOctet[i] < DECODE_TABLE.length && DECODE_TABLE[arrayOctet[i]] != -1)) 
	        		&& !(arrayOctet[i]==' '||arrayOctet[i]=='\n'||arrayOctet[i]=='\r'||arrayOctet[i]=='\t')) {
	            return false;
	        }
	    }
	    return true;
	}
	private static final byte[] DECODE_TABLE = {
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	    -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54,
	    55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
	    5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
	    24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
	    35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51
	};

	 
}