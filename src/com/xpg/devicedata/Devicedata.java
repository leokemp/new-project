package com.xpg.devicedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.xpg.gokit.utils.txtParser;

public class Devicedata {
	
	private List<Map<String, Object>> list = null;
	
	public static String[] CMD_Name;
	
	public static String[] CMD;
	
	public static Object[] DATA;
	
	public static String productkey;

	public Devicedata(List<Map<String, Object>> list2) {
		super();
		this.list = new ArrayList<Map<String, Object>>();
		this.list = list2;
		Log.i("list.size()", list2.size()+"");
		CMD = new String[list2.size()];
		DATA = new Object[list2.size()];
		CMD_Name = new String[list2.size()];
		}

	public void ParseData(){
		for(int i =0 ; i<list.size();i++){
			CMD_Name[i] = (String) list.get(i).get("CName");
			CMD[i] = (String) list.get(i).get("EName");
			DATA[i] = list.get(i).get("CMD");
			
			Log.i("CName", CMD_Name[i]);
			Log.i("CMD", CMD[i]);
			Log.i("DATA", DATA[i]+"");
		}
	}
	
	

}
