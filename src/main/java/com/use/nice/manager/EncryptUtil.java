package com.use.nice.manager;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

public class EncryptUtil {
	public final static int BITPERCHANNEL = 4;
	private final static int BIT4LENGTH = 32;
	
	public static byte[] reveal(InputStream input) {
		//long time = System.currentTimeMillis();
		 
		Bitmap revealImage = BitmapFactory.decodeStream(input);
// 		revealImage.setHasAlpha(true);
		
//		Log.d("HEHE", "Decode BitMap Use " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
//		System.out.println("Reveal operation started.");
				
		int x,y; 
		x = y = 0;
		
		int lengthLEN = BIT4LENGTH/BITPERCHANNEL;
		byte[] lengthData = new byte[lengthLEN];
		
		for(int i = 0; i < lengthData.length ; i += 3){
			int colorData =  revealImage.getPixel(x, y);
			
			lengthData[i] = (byte) (colorData >> 16 & ((1 << BITPERCHANNEL) - 1));
			if(i+1<lengthLEN) {
				lengthData[i + 1] = (byte) (colorData >> 8 & ((1 << BITPERCHANNEL) - 1));
			}
			if(i+2<lengthLEN) {
				lengthData[i + 2] = (byte) (colorData & ((1 << BITPERCHANNEL) - 1));
			}
			
			x++;
			if(x == revealImage.getWidth())
			{
				y++;
				x = 0;
			}
		}
		
		int length = bytes2int(toNormalData(lengthData));
		byte[] channelData = new byte[length * Byte.SIZE / BITPERCHANNEL];
		
//		Log.d("HEH", "Get Length use: " + (System.currentTimeMillis() - time));
//		time = System.currentTimeMillis();
		
		x = y = 0;
		for(int i = 0; i < channelData.length; i+=3){
			int colorData =  revealImage.getPixel(x, y);
			
			channelData[i] = (byte) (colorData >> 16 & ((1 << BITPERCHANNEL) - 1));
			if(i+1<channelData.length){
				channelData[i + 1] = (byte) (colorData >> 8 & ((1 << BITPERCHANNEL) - 1));
			}
			if(i+2<channelData.length){
				channelData[i + 2] = (byte) (colorData & ((1 << BITPERCHANNEL) - 1));
			}
			
			x++;
			if(x == revealImage.getWidth())
			{
				y++;
				x = 0;
			}
		}
		
		
		//Log.d("HEHE", "Get Data Use: " + (System.currentTimeMillis() - time));
		byte[] realData = toNormalData(channelData);
		
		revealImage.recycle();
		byte [] bs =Arrays.copyOfRange(realData, BIT4LENGTH/Byte.SIZE, realData.length);
		return  bs;
	}

	public static boolean hide(byte[] data, File outputFile) {
		Bitmap decodeBitmap = BitmapFactory.decodeFile(outputFile.getAbsolutePath());
		Bitmap outImage = decodeBitmap.copy(Bitmap.Config.ARGB_8888, true);
		decodeBitmap.recycle();
		
		long requiredBits = calcRequiredBits(data);
		int requiredBytes = (int)requiredBits/Byte.SIZE;
		
		byte[] fileBytes = new byte[requiredBytes];
		System.arraycopy(data, 0, fileBytes, BIT4LENGTH/Byte.SIZE, data.length);
		byte[] lengthByte = int2bytes(requiredBytes);
		for(int i = 0; i < lengthByte.length; i++)
		{
			fileBytes[i] = lengthByte[i];
		}
		
		byte[] channelData = createUnitData(fileBytes);
		fileBytes = null;
		
		int x = 0, y = 0;

		for(int i = 0; i < channelData.length; i += 3){
			int colorData = outImage.getPixel(x,y);
			
			int redMask = ~(((1 << BITPERCHANNEL) - 1) << 16);
			
			colorData = colorData & redMask | (channelData[i] << 16);
			
			int greenMask =  ~(((1 << BITPERCHANNEL) - 1) << 8);
			
			if(i+1< channelData.length)  colorData = colorData & greenMask | (channelData[i + 1] << 8);
			
			int blueMask = ~((1 << BITPERCHANNEL) - 1);
			
			if(i+2 < channelData.length) colorData = colorData & blueMask | (channelData[i + 2]);
			
			outImage.setPixel(x,y,colorData);
			
			x++;
			if(x == outImage.getWidth())
			{
				if(y == outImage.getHeight())
				{
					return false;
				}
				else
				{
					y++;
					x = 0;
				}
			}
		}
		
		try {
			outImage.compress(CompressFormat.PNG, 100, new FileOutputStream(outputFile, false));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		outImage.recycle();
		return true;
	}
	
	
	private static long calcRequiredBits(byte[] data)
	{
		long bits = 0;
		bits += data.length * Byte.SIZE; 
		bits += BIT4LENGTH;
		return bits;
	}

	
	static byte[] createUnitData(byte[] byteArray)
	{
		byte[] ret = new byte[Byte.SIZE/BITPERCHANNEL*byteArray.length];
		
		int mask = (1 << BITPERCHANNEL) -1;
		
		for(int i = 0; i < byteArray.length; i++){
			for(int j = 0; j < Byte.SIZE/BITPERCHANNEL; j++){
				ret[(i+1) * Byte.SIZE/BITPERCHANNEL - j - 1] = (byte) (byteArray[i] & mask);
				byteArray[i] = (byte) (byteArray[i] >> BITPERCHANNEL);
			}
		}
		
		return ret;
	}
	
	
	static byte[] toNormalData(byte[] data){
		byte[] ret = new byte[data.length/(Byte.SIZE / BITPERCHANNEL)];
		
		for(int i = 0; i < ret.length; i++){
			for(int j = 0; j < Byte.SIZE / BITPERCHANNEL; j++){
				ret[i] = (byte) (ret[i] << BITPERCHANNEL | data[i * Byte.SIZE / BITPERCHANNEL  + j]);
			}
		}
		
		return ret;
	}
	
	
	  private static byte[] int2bytes(int num) {
	        byte[] b = new byte[4];
	        for (int i = 0; i < 4; i++) {
	            b[i] = (byte) (num >>> (24 - i * 8));
	        }
	        return b;
	    }
	    
	    
	    private static int bytes2int(byte[] data){
	    	int ret = 0;
	    	for(int i = 0; i< 4; i++){
	    		ret = ret << 8 | (data[i] & 0xff);
	    	}
	    	return ret;
	    }

}
