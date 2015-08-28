package com.xpg.gizwitsdeviceactivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.xpg.devicedata.FinishDevice;
import com.xpg.devicedata.Devicedata;
import com.xpg.gokit.bean.ControlDevice;
import com.xpg.gokit.utils.FileUtils;
import com.xpg.gokit.utils.txtParser;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ControlActivity extends BaseActivity {

	/** 实体字段名，代表对应的项目. */
	private static final String KEY_ACTION = "entity0";

	private static final String TestData = "Devicedata";

	/** The xpg wifi device. */
	XPGWifiDevice xpgWifiDevice;

	/** The control device. */
	ControlDevice controlDevice;

	/** The Constant UPDATE_UI. */
	protected static final int UPDATE_UI = 2;
	/** The Constant LOG. */
	protected static final int LOG = 6;

	/** The Constant RESP. */
	protected static final int RESP = 7;
	/** 发送命令 */
	protected static final int SEND = 1;
	/** 测试结束 */
	protected static final int END = 3;
	/** 命令长度 实际命令最后应该增加一条开关机命令 处理协议需要 */
	protected static final int CMD_ALL = Devicedata.CMD.length - 1;
	/** 测试成功失败判断位 */
	private boolean Success_Bit = false;
	/** 开始测试判断位 */
	private boolean isStart = false;
	/** 失败命令条目 */
	private int Fail_Cmd_Num = 0;
	/** 是否为多数据点命令 */
	private boolean isMoreCmd = false;

	/** The device statu. */
	private HashMap<String, Object> deviceStatu;

	private TextView tv_showdata;

	private Button btn_start;

	private Button btn_stop;

	private Button btn_clear;

	private Button btn_back;

	private Button btn_file_selet;
	/** 当前测试到该条命令 */
	private int CMD_count = 0;

	private Timer timer = null;

	private TimerTask task = null;

	private String[] Mac = new String[FinishDevice.getSize()];;

	private Object[] FinishStatus = new Object[FinishDevice.getSize()];

	private int FinishNum = 0;

	/** The handler. */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {

			case SEND:
				// 下次发送前判断上次发送的状况
				if (Success_Bit == false && CMD_count >= 1) {
					Fail_Cmd_Num++;
					Update_TV("失败");
				}// 打印发送命令
//				tv_showdata.append("CMD " + CMD_count + "     "
//						+ Devicedata.CMD[CMD_count] + " = "
//						+ Devicedata.DATA[CMD_count] + "\n");
				tv_showdata.append("CMD " + CMD_count +"      "+Devicedata.CMD_Name[CMD_count]+"\n");
				Refresh_TV();
				Success_Bit = false;
				try {
					sendJson(Devicedata.CMD[CMD_count],
							Devicedata.DATA[CMD_count]);
					CMD_count++;
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				break;
			// case DISCONNECT:
			// Toast.makeText(GokitControlActivity.this, "设备已断开",
			// Toast.LENGTH_SHORT).show();
			// finish();
			// break;
			case UPDATE_UI:

				UpdateUI();

				break;

			case END:
				Test_End();
				break;

			case RESP:
				String data = msg.obj.toString();

				try {
					CheckData(data);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			case LOG:
				StringBuilder sb = new StringBuilder();
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject((String) msg.obj);
					for (int i = 0; i < jsonObject.length(); i++) {
						sb.append(jsonObject.names().getString(i)
								+ " "
								+ jsonObject.getInt(jsonObject.names()
										.getString(i)) + "\r\n");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Toast.makeText(ControlActivity.this, sb.toString(),
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controlpage);

		tv_showdata = (TextView) findViewById(R.id.tv_showdata);
		tv_showdata.setMovementMethod(ScrollingMovementMethod.getInstance());
		btn_start = (Button) findViewById(R.id.btn_start);
		btn_stop = (Button) findViewById(R.id.btn_stop);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_file_selet = (Button) findViewById(R.id.btn_file_selet);
		btn_back = (Button) findViewById(R.id.btn_back);

		deviceStatu = new HashMap<String, Object>();
		controlDevice = (ControlDevice) getIntent().getSerializableExtra(
				"device");
		xpgWifiDevice = BaseActivity
				.findDeviceByOnlyMac(controlDevice.getMac());
		tv_showdata.append("MAC" + controlDevice.getMac() + "\n");
		tv_showdata.append("Productkey" + controlDevice.getProductKey() + "\n");
		if (xpgWifiDevice != null) {
			xpgWifiDevice.setListener(deviceListener);
		}
		actionBar.setTitle(controlDevice.getName());
		if (!xpgWifiDevice.isOnline()) {
			new AlertDialog.Builder(this).setTitle("警告")
					.setMessage("设备不在线，不可以做控制，但可以断开或解除绑定")
					.setPositiveButton("OK", null).show();
		}

		btn_start.setOnClickListener(btn_listener);
		btn_stop.setOnClickListener(btn_listener);
		btn_clear.setOnClickListener(btn_listener);
		btn_file_selet.setOnClickListener(btn_listener);
		btn_back.setOnClickListener(btn_listener);

		this.Mac = FinishDevice.getMac();
		this.FinishStatus = FinishDevice.getFinishStatus();
		this.FinishNum = FinishDevice.getNum();

	}

	/**
	 * 测试结束打印函数(针对最后一条失败情况)
	 * 
	 */
	protected void Test_End() {
		// 下次发送前判断上次发送的状况
		if (Success_Bit == false && CMD_count >= 1) {
			Fail_Cmd_Num++;
			Update_TV("失败");
		}
		// tv_showdata.append("测试完成" + "\n");
		int color;
		if (Fail_Cmd_Num != 0) {
			color = Color.RED;
		} else {
			color = Color.BLUE;
		}
		String status = "测试完成";
		Set_Color(color, status);
		tv_showdata.append("失败命令条数 : ");
		status = Integer.toString(Fail_Cmd_Num);
		RemenberStatus(Fail_Cmd_Num);
		Set_Color(color, status);
		// tv_showdata.append("Fail_Cmd_Num : " + Fail_Cmd_Num + "\n");
		Refresh_TV();
		btn_start.setEnabled(true);
		btn_back.setEnabled(true);

	}

	
	/**记录已经测试过的设备的成功失败情况
	 * @param fail_Cmd_Num  失败的条目
	 */
	private void RemenberStatus(int fail_Cmd_Num) {
		boolean hasDevice = false;
		for (int j = 0; j < FinishDevice.getMac().length; j++) {
			if (xpgWifiDevice.getMacAddress().equals(FinishDevice.getMac()[j])) {
				if (fail_Cmd_Num == 0) {
					FinishStatus[j] = true;
				} else {
					FinishStatus[j] = false;
				}
				Mac[j] = controlDevice.getMac();
				FinishDevice.SetStatus(FinishNum, Mac, FinishStatus);
				hasDevice = true;
				break;
			}
		}
		if (!hasDevice) {
			if (fail_Cmd_Num == 0) {
				FinishStatus[FinishNum] = true;
			} else {
				FinishStatus[FinishNum] = false;
			}
			Mac[FinishNum] = controlDevice.getMac();
			FinishNum++;
			FinishDevice.SetStatus(FinishNum, Mac, FinishStatus);
		}
		if(FinishNum == FinishDevice.getSize()-1){
			FinishNum = 0;
		}
		// TODO Auto-generated method stub

	}

	OnClickListener btn_listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.btn_start:
				Start_Test();
				break;
			case R.id.btn_stop:
				stop();
				btn_start.setEnabled(true);
				btn_back.setEnabled(true);
				break;
			case R.id.btn_clear:
				tv_showdata.setText("");
				break;
			case R.id.btn_file_selet:

				break;
			case R.id.btn_back:
				xpgWifiDevice.disconnect();
				finish();
				break;
			default:
				break;
			}

		}
	};

	@Override
	public boolean didReceiveData(XPGWifiDevice device,
			java.util.concurrent.ConcurrentHashMap<String, Object> dataMap,
			int result) {
		Log.i("aaaaaaaaaaaaaaaaaaaaa", "ff");
		if (dataMap.get("data") != null) {
			Log.i("info", (String) dataMap.get("data"));
			Message msg = new Message();
			msg.obj = dataMap.get("data");
			msg.what = RESP;
			handler.sendMessage(msg);
		}

		if (dataMap.get("alters") != null) {
			Log.i("info", (String) dataMap.get("alters"));
			Message msg = new Message();
			msg.obj = dataMap.get("alters");
			msg.what = LOG;
			handler.sendMessage(msg);
		}

		if (dataMap.get("faults") != null) {
			Log.i("info", (String) dataMap.get("faults"));
			Message msg = new Message();
			msg.obj = dataMap.get("faults");
			msg.what = LOG;
			handler.sendMessage(msg);
		}

		if (dataMap.get("binary") != null) {
			Log.i("info",
					"Binary data:" + bytesToHex((byte[]) dataMap.get("binary")));
		}

		return true;
	};

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 3];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 3] = hexArray[v >>> 4];
			hexChars[j * 3 + 1] = hexArray[v & 0x0F];
			hexChars[j * 3 + 2] = ' ';
		}
		return new String(hexChars);
	}

	/**
	 * 解析设备返回状态命令
	 * 
	 * @param data
	 * @throws JSONException
	 */
	private void CheckData(String data) throws JSONException {
		JSONObject receive = new JSONObject(data);
		@SuppressWarnings("rawtypes")
		Iterator actions = receive.keys();
		while (actions.hasNext()) {
			String action = actions.next().toString();
			// 忽略特殊部分
			if (action.equals("cmd") || action.equals("qos")
					|| action.equals("seq") || action.equals("version")) {
				continue;
			}
			JSONObject params = receive.getJSONObject(action);
			@SuppressWarnings("rawtypes")
			Iterator it_params = params.keys();
			while (it_params.hasNext()) {
				String param = it_params.next().toString();
				// Log.i("TAG", param);
				Object value = params.get(param);
				deviceStatu.put(param, value);
			}
		}
		Message msg = new Message();
		msg.obj = data;
		msg.what = UPDATE_UI;
		handler.sendMessage(msg);
	}

	/**
	 * 设备返回状态后，把状态更新到界面
	 * 
	 */
	private void UpdateUI() {
		//此处打印为下一条数据，是错误的，正确应该减一
		Log.i("deviceStatu.get(Devicedata.CMD[CMD_count])",
				deviceStatu.get(Devicedata.CMD[CMD_count]) + "");
		Log.i("Devicedata.DATA[CMD_count]", Devicedata.DATA[CMD_count] + "");
		if (isStart) {
			// 多数据点命令检测,命令中以1标示多数据点命令
			// 带扩展类型的没做
			if (Devicedata.CMD[CMD_count - 1].toString().contains("*")) {
				// 切割
				String[] CMD = null;
				CMD = Devicedata.CMD[CMD_count - 1].toString().split(",");
				String[] DATA = null;
				DATA = Devicedata.DATA[CMD_count - 1].toString().split(",");
				// 空气净化器特殊数据判断位，其他可删
				if(!FinishDevice.getProductkey().substring(1, FinishDevice.Productkeysize()).equals("ed1d52fc84be415caafbe7f7e8c14513")){
					if (CMD[1].equals("period_status")) {
						DATA[0] = Integer.toString(2);
					}
				}	
				
				// 成功状态位
				boolean CMD_MORE_CheckBit = true;
				// 逐个判断
				for (int i = 0; i < DATA.length; i++) {
					//多命令中扩展类型数据点检测
					if (DATA[i].contains("0x")) {
						String data = deviceStatu.get(Devicedata.CMD[CMD_count - 1])
								.toString();
						if (!bytesToHexString(Base64.decode(data, Base64.DEFAULT))
								.equals(DATA[i].substring(2, DATA[i].length()))){
							CMD_MORE_CheckBit = false;
						}
					} else {
						if (!deviceStatu.get(CMD[i + 1]).toString()
								.equals(DATA[i].toString())) {
							CMD_MORE_CheckBit = false;
						}
					}
				}// 成功
				if (CMD_MORE_CheckBit == true) {
					Update_TV_MoreData("成功!!!!");
					Success_Bit = true;
					stop();
					Restart_Test();
				}// 失败
				else {
					// Update_TV_MoreData("fail");
				}
				Refresh_TV();

			}// 扩展类型数据检测
			else if (Devicedata.DATA[CMD_count - 1].toString().contains("0x")) {
				String data = deviceStatu.get(Devicedata.CMD[CMD_count - 1])
						.toString();
				String DATA = Devicedata.DATA[CMD_count - 1].toString();
				if (bytesToHexString(Base64.decode(data, Base64.DEFAULT))
						.equals(DATA.substring(2, DATA.length()))) {
					Update_TV("成功!!!!");
					Success_Bit = true;
					stop();
					Restart_Test();
				}
			}// 单数据点命令检测
			else {
				if (deviceStatu.get(Devicedata.CMD[CMD_count - 1]).toString()
						.equals(Devicedata.DATA[CMD_count - 1].toString())) {
					Update_TV("成功!!!!");
					Success_Bit = true;
					stop();
					Restart_Test();
				}
				Refresh_TV();
			}
		}
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	// 切割多数据点命令，此处不需要使用
	private void CheckJson(String key, Object value) {
		if (key.contains("*")) {
			String[] CMD = null;
			CMD = key.split(",");
			for (int i = 0; i < CMD.length; i++) {
				Log.i("CMD", CMD[i]);
			}
			String[] DATA = null;
			DATA = value.toString().split(",");
			for (int i = 0; i < DATA.length; i++) {
				Log.i("DATA", DATA[i]);
			}

		}
	}

	/**
	 * 发送指令。格式为json。
	 * <p>
	 * 例如 {"entity0":{"attr2":74},"cmd":1}
	 * 其中entity0为gokit所代表的实体key，attr2代表led灯红色值，cmd为1时代表写入
	 * 。以上命令代表改变gokit的led灯红色值为74.
	 * 
	 * @param key
	 *            数据点对应的的json的key
	 * @param value
	 *            需要改变的值
	 * @throws JSONException
	 *             the JSON exception
	 */
	private void sendJson(String key, Object value) throws JSONException {
		final JSONObject jsonsend = new JSONObject();
		JSONObject jsonparam = new JSONObject();
		jsonsend.put("cmd", 1);
		if (key.contains("*")) {
			isMoreCmd = true;
			// 判断是否为多数据点命令
			// 带扩展类型的没做
			String[] CMD = null;
			// 切割
			CMD = key.split(",");
			Object[] DATA = null;
			DATA = value.toString().split(",");
			// 逐个判断
			for (int i = 0; i < DATA.length; i++) {
				// 多数据点中扩展类型
				if (DATA[i].toString().contains("0x")) {
					byte[] bytes = stringToBytes(DATA[i].toString().substring(2, value
							.toString().length()));
					String ext_data = Base64.encodeToString(bytes,
							Base64.DEFAULT); // 编码后的ext_data
					jsonparam.put(key, ext_data);
				} else {
					jsonparam.put(CMD[i + 1], DATA[i]);
				}
			}
		}
		// 扩展类型
		else if (value.toString().contains("0x")) {
			byte[] bytes = stringToBytes(value.toString().substring(2,
					value.toString().length()));
			String ext_data = Base64.encodeToString(bytes, Base64.DEFAULT); // 编码后的ext_data
			jsonparam.put(key, ext_data);
		} else {
			isMoreCmd = false;
			// 单个字节

			jsonparam.put(key, value);
		}
		jsonsend.put(KEY_ACTION, jsonparam);
		Log.i("sendjson", jsonsend.toString());
		mCenter.cWrite(xpgWifiDevice, jsonsend);
	}

	private byte[] stringToBytes(String extData) {
		int length = extData.length();
		byte[] bytes = new byte[2];
		if (length < 2)
			return null;
		for (int i = 0; i < 2; i++) {
			{
				bytes[i] = (byte) (Integer.parseInt(extData.substring(i * 2,
						(i * 2) + 2)) & 0xff);
			}
		}
		return bytes;
	}
	
	private void sendJsontest(String key, Object value) throws JSONException {
		final JSONObject jsonsend = new JSONObject();
		JSONObject jsonparam = new JSONObject();
		jsonsend.put("cmd", 1);
		jsonparam.put(key, value);
		jsonsend.put(KEY_ACTION, jsonparam);
		Log.i("sendjson", jsonsend.toString());
		mCenter.cWrite(xpgWifiDevice, jsonsend);
	}

	/**
	 * 开始测试，加入计时器
	 * 
	 */
	private void start() {
		if (timer == null) {
			timer = new Timer();
		}
		if (task == null) {
			task = new TimerTask() {

				@Override
				public void run() {
					if (CMD_count == CMD_ALL) {
						stop();
						Message msg1 = new Message();
						msg1.what = END;
						handler.sendMessage(msg1);
					} else {
						Message msg = new Message();
						msg.what = SEND;
						handler.sendMessage(msg);
					}

				}
			};
		}

		if (timer != null && task != null) {
			timer.schedule(task, 2000, 10000);
		}
	}

	/**
	 * 暂停测试，清除计时器
	 * 
	 */
	private void stop() {
		ClearTimer();
		isStart = false;
	}
	
	private void ClearTimer(){
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	/**
	 * 开始测试按钮
	 */
	private void Start_Test() {
		Fail_Cmd_Num = 0;
		isStart = true;
		Success_Bit = false;
		CMD_count = 0;
		btn_start.setEnabled(false);
		btn_back.setEnabled(false);
		ClearTimer();
		start();
	}

	/**
	 * 重新启动测试，用于接收上一条命令成功后，不等待直接发送下一条测试命令 测试结束 **针对最后一条成功情况
	 */
	private void Restart_Test() {
		isStart = true;
		if (CMD_count != CMD_ALL) {
			start();
		} else {
			// (针对最后一条成功情况)
			// tv_showdata.append("finish" + "\n");
			// tv_showdata.append("Fail_Cmd_Num : " + Fail_Cmd_Num + "\n");
			// Refresh_TV();
			// btn_start.setEnabled(true);
			// 测试结束
			int color;
			if (Fail_Cmd_Num != 0) {
				color = Color.RED;
			} else {
				color = Color.BLUE;
			}
			String status = "测试完成";
			Set_Color(color, status);
			// tv_showdata.append();
			status = "失败命令条数 : " + Integer.toString(Fail_Cmd_Num);
			RemenberStatus(Fail_Cmd_Num);
			Set_Color(color, status);
			// tv_showdata.append("Fail_Cmd_Num : " + Fail_Cmd_Num + "\n");
			Refresh_TV();
			btn_start.setEnabled(true);
			btn_back.setEnabled(true);
		}
	}

	/**
	 * 更新Textview的滑动，显示最底的数据
	 */
	private void Refresh_TV() {
		int offset = tv_showdata.getLineCount() * tv_showdata.getLineHeight();
		if (offset > tv_showdata.getHeight()) {
			tv_showdata.scrollTo(0, offset - tv_showdata.getHeight() + 10);
		}
	}

	/**
	 * 更新单个数据点命令回复的判断命令
	 * 
	 * @param status
	 *            success 测试成功 fail测试失败
	 */
	private void Update_TV(String status) {
		
		//打印Log命令
		//tv_showdata.append("CMD " + (CMD_count - 1) + " " + "Send  "
		//		+ Devicedata.DATA[CMD_count - 1] + "  ");
		if (isMoreCmd) {
			// 判断是否为多数据点命令
			String[] CMD = null;
			// 切割
			String key = Devicedata.CMD[CMD_count - 1];
			CMD = key.split(",");
			// 逐个判断
			//tv_showdata.append("Get  ");
			for (int i = 0; i < CMD.length - 1; i++) {
				tv_showdata.append(deviceStatu.get(CMD[i + 1]) + ",");
			}

		} else {
			// 单数据点命令
		//打印Log命令
		//	tv_showdata.append("Get  "
		//			+ deviceStatu.get(Devicedata.CMD[CMD_count - 1]) + "    ");
		}
		int color;
		if (status.equals("失败")) {
			color = Color.RED;
		} else {
			color = Color.BLUE;
		}
		Set_Color(color, status);
	}

	/**
	 * 更新多个数据点命令回复的判断命令
	 * 
	 * @param status
	 *            success 测试成功 fail测试失败
	 */
	private void Update_TV_MoreData(String status) {
		//tv_showdata.append("CMD " + (CMD_count - 1) + "  ");
		int color = Color.BLUE;
		Set_Color(color, status);
	}

	/**
	 * 设置字体颜色
	 * 
	 * @param color
	 * @param status
	 */
	private void Set_Color(int color, String status) {
		SpannableString style = new SpannableString(status);
		style.setSpan(new ForegroundColorSpan(color), 0, status.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		tv_showdata.append(style);
		tv_showdata.append("\n");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			xpgWifiDevice.disconnect();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
