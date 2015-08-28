package com.xpg.gokit.utils;

/** 
 * @Title: FileHelper.java 
 * @Package com.tes.textsd 
 * @Description: TODO(��һ�仰�������ļ���ʲô) 
 * @author Alex.Z 
 * @date 2013-2-26 ����5:45:40 
 * @version V1.0 
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

public class txtParser {
	private Context context;
	/** SD���Ƿ���� **/
	private boolean hasSD = false;
	/** SD����·�� **/
	private String SDPATH;
	/** ��ǰ������·�� **/
	private String FILESPATH;
	/** ÿһ������ **/
	private Map<String, Object> map = null;
	/** ���������б� **/
	private List<Map<String, Object>> list = null;
	/** �������� **/
	private StringBuffer ErrorData;

	private boolean MapErrorDataStatus;

	private String productkey;

	public String getProductkey() {
		return productkey;
	}

	public void setProductkey(String productkey) {
		this.productkey = productkey;
	}

	public txtParser(Context context, String SDPATH_get) {
		this.context = context;
		hasSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		SDPATH = SDPATH_get;
		// SDPATH = Environment.getExternalStorageDirectory().getPath();
		FILESPATH = this.context.getFilesDir().getPath();
		list = new ArrayList<Map<String, Object>>();
	}

	// /**
	// * ��SD���ϴ����ļ�
	// *
	// * @throws IOException
	// */
	// public File createSDFile(String fileName) throws IOException {
	// File file = new File(SDPATH + "//" + fileName);
	// if (!file.exists()) {
	// file.createNewFile();
	// }
	// return file;
	// }
	//
	// /**
	// * ɾ��SD���ϵ��ļ�
	// *
	// * @param fileName
	// */
	// public boolean deleteSDFile(String fileName) {
	// File file = new File(SDPATH + "//" + fileName);
	// if (file == null || !file.exists() || file.isDirectory())
	// return false;
	// return file.delete();
	// }

	/**
	 * д�����ݵ�SD���е�txt�ı��� strΪ����
	 */
	public void writeSDFile(String str) {
		try {

			// Toast.makeText(context, text, duration)
			// Toast.makeText(this, "����ɹ�", Toast.LENGTH_LONG).show();

			// FileWriter fw = new FileWriter(SDPATH + "//" + fileName);
			// File f = new File(SDPATH + "//" + fileName);
			Log.i("TAG", "SDPATH0");
			FileWriter fw = new FileWriter(SDPATH);
			File f = new File(SDPATH);
			Log.i("TAG", "SDPATH1");
			fw.write(str);
			Log.i("TAG", "SDPATH2");
			FileOutputStream os = new FileOutputStream(f, true);
			DataOutputStream out = new DataOutputStream(os);
			Log.i("TAG", "SDPATH3");
			out.writeShort(2);
			out.writeUTF("");
			Log.i("TAG", "SDPATH4");
			System.out.println(out);
			fw.flush();
			fw.close();
			System.out.println(fw);
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}
	}

	/**
	 * д�����ݵ�SD���е�txt�ı��� strΪ����
	 */
	public boolean writeSDcard_1(String str) {

		try {
			FileOutputStream outputStream = new FileOutputStream(SDPATH, true);
			outputStream.write(str.getBytes());
			outputStream.flush();
			outputStream.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ���ش������
	 */

	public String returnErrorData() {
		return ErrorData.toString();
	}

	/**
	 * ���ؽ������
	 */
	public List<Map<String, Object>> returnList() {
		return list;
	}

	/**
	 * ��ȡSD�����ı��ļ�
	 * 
	 * @param fileName
	 * @return
	 */
	public String readSDFile() {
		StringBuffer sb = new StringBuffer();
		// /File file = new File(SDPATH + "//" + fileName);
		File file = new File(SDPATH);
		file.mkdir();
		int time = 0;
		ErrorData = new StringBuffer();
		
		MapErrorDataStatus = false;
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileInputStream,"GB2312"));
			ExplainData(sb, time, br);
			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public void ExplainData(StringBuffer sb, int time, BufferedReader br)
			throws IOException {
		String readline = "";
		while ((readline = br.readLine()) != null) {			
			readline = EncodingUtils.getString(readline.getBytes("utf-8"),"utf-8");
			sb.append(readline + "\n");
			if (time == 0) {
				productkey = readline;
				time++;
			} else if (time == 1) {
				map = new HashMap<String, Object>();
				map.put("CName", readline);
				time++;
			} else if (time == 2) {
				map.put("EName", readline);
				time++;
			} else if (time == 3) {
				map = Check_Data(map, readline);
				// map.put("CMD", readline);
				list.add(map);
				time = 1;
			}
		}
	}

	/**
	 * 判断命令类型 做格式转换
	 * 
	 * @param readline
	 * @return 最大范围 7FFFFFFF
	 */
	private Map<String, Object> Check_Data(Map<String, Object> map,
			String readline) {
		//布尔值
		if (readline.equals("true")) {
			map.put("CMD", true);
		} else if (readline.equals("false")) {
			map.put("CMD", false);
		}
		//多命令
		else if (readline.contains(",")) {
			map.put("CMD", readline);
		} 
		//扩展类型，还未加密（待做）
		else if (readline.contains("0x")){
			readline = "0x0506";
			
			map.put("CMD", readline);
		}
		//数字类型
		else if (Integer.parseInt(readline) >= 0
				&& Integer.parseInt(readline) <= 2147483647) {
			map.put("CMD", Integer.parseInt(readline));
		} else {
			map.put("CMD", readline);
		}

		return map;
	}
	
	private byte[] stringToBytes(String extData) {
		int length = extData.length();
		byte[] bytes = new byte[2];
		if(length < 2)
			return null;
		for (int i = 0; i < 2; i++) {
			{
				bytes[i] = (byte) (Integer.parseInt(extData.substring(i * 2,
						(i * 2) + 2)) & 0xff);
			}
		}
		return bytes;
	}

	public void manageReadData(StringBuffer sb, int time, BufferedReader br)
			throws IOException {
		String readline = "";
		while ((readline = br.readLine()) != null) {
			// System.out.println("readline:" + readline);
			// manageReadData( sb, readline, time);
			sb.append(readline + "\n");
			if (time == 0) {
				map = new HashMap<String, Object>();
				map.put("Name", readline);
				time++;
			} else if (time == 1) {
				if (readline.length() != 36) {
					ErrorData.append(map.get("Name") + " Service is illegality"
							+ "\n");
					MapErrorDataStatus = true;
				}
				map.put("Service", readline);
				time++;

			} else if (time == 2) {
				if (readline.length() != 36) {
					Log.i("tag", "time =0");
					ErrorData.append(map.get("Name")
							+ " Characteristic is illegality" + "\n");
					MapErrorDataStatus = true;
				}
				Log.i("tag", "time =1");
				map.put("Characteristic", readline);
				time++;
				Log.i("tag", "time =2");
			} else if (time == 3) {
				map.put("Values", readline);
				time++;
			} else if (time == 4) {
				if (!readline.equals("READ") && !readline.equals("NOTIFY")
						&& !readline.equals("WRITE")) {
					ErrorData.append(map.get("Name") + " Status is illegality"
							+ "\n");
					MapErrorDataStatus = true;
				}
				map.put("Status", readline);
				if (MapErrorDataStatus == true) {
					map.put("Service_Characteristic", "    Illegality_CMD");
					MapErrorDataStatus = false;
				} else {
					map.put("Service_Characteristic",
							"    S: "
									+ ((String) map.get("Service")).substring(
											4, 8)
									+ "    C: "
									+ ((String) map.get("Characteristic"))
											.substring(4, 8));
				}
				list.add(map);
				time = 0;
			}
		}

	}

	public void DelectOneCmd(String str) {
		Log.i("txtParser", "DelectOneCmd");
		StringBuffer sb = new StringBuffer();
		// /File file = new File(SDPATH + "//" + fileName);
		File file = new File(SDPATH);
		int timecount = 0;
		ErrorData = new StringBuffer();
		;
		MapErrorDataStatus = false;

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					fileInputStream, "utf-8"));
			String readline = "";
			while ((readline = br.readLine()) != null) {

				if (readline.equals(str) || timecount != 0) {
					timecount++;
				} else if (timecount == 0) {
					sb.append(readline + "\n");
				}
				if (timecount == 5) { // 5Ϊһ�������е���Ŀ��
					timecount = 0;
				}
			}
			// System.out.println("readline:" + sb);

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("OK");
		writeSDFile(sb.toString());
	}

	public String getFILESPATH() {
		return FILESPATH;
	}

	public String getSDPATH() {
		return SDPATH;
	}

	public boolean hasSD() {
		return hasSD;
	}
}