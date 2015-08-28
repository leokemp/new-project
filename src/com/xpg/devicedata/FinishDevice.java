package com.xpg.devicedata;

import android.util.Log;

public class FinishDevice {
	
	private static int Size = 200;
	
	private static String[] Mac = new String[Size];
	
	private static Object[] FinishStatus= new Object[Size];
	
	public static String productkey;
	
	public static int Productkeysize(){
		return productkey.length();
	}
		
	public static String getProductkey() {
		return productkey;
	}

	public static void setProductkey(String productkey) {
		FinishDevice.productkey = productkey;
	}

	public static int getSize() {
		return Size;
	}

	public static void setSize(int size) {
		Size = size;
	}

	private static int Num = 0;
	//private static int Num = 6;
	
	public static void Start(){
		//Mac[0] = "ACCF23660036";
		//Mac[5] = "ACCF2359DEB6";
		//FinishStatus[0]=true;
		//FinishStatus[5]=true;
	}
	
	public static void SetStatus(int Num ,String[] Mac,Object[] FinishStatus ){
		FinishDevice.Num = Num;
		FinishDevice.FinishStatus = FinishStatus;
		FinishDevice.Mac = Mac;
		for(int i = 0;i< Mac.length;i++){
			Log.i("FFFFFFFFFFFFFFFFFFFFFFFFFFFFF", Mac[i]+"  "+FinishStatus[i]+"  "+Num);
		}
		
	}
	
	public static void setMac(String[] mac) {
		Mac = mac;
	}

	public static void setFinishStatus(Object[] finishStatus) {
		FinishStatus = finishStatus;
	}

	public static void setNum(int num) {
		Num = num;
	}

	public static String[] getMac(){
		return Mac;
	}

	public static Object[] getFinishStatus() {
		return FinishStatus;
	}

	public static int getNum(){
		return Num;
	}
	
	
	
	

}
