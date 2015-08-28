package com.xpg.gizwitsdeviceactivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xpg.devicedata.Devicedata;
import com.xpg.devicedata.FinishDevice;
import com.xpg.gokit.utils.AssertsUtils;
import com.xpg.gokit.utils.FileUtils;
import com.xpg.gokit.utils.GetPathFromUri4kitkat;
import com.xpg.gokit.utils.txtParser;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiErrorCode;
import com.xtremeprog.xpgconnect.XPGWifiSDK;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

public class MainActivity extends BaseActivity {

	private Button btn_update;

	private Button btn_devicetest;

	private Button btn_add_file;

	protected static final int SUCCESS = 1;

	protected static final int FAIL = 2;

	private String productkey = "";

	private SharedPreferences mySharedPreferences;

	private static final int FILE_SELECT_CODE = 0;

	private Devicedata mDevicedata;

	/** 解析txt文件类 */
	private txtParser txtparser;

	private List<Map<String, Object>> list = null;

	/** The handler. */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case SUCCESS:
				Toast.makeText(MainActivity.this, "成功更新", Toast.LENGTH_SHORT)
						.show();
				btn_update.setText("已更新配置文件");
				break;
			case FAIL:
				Toast.makeText(MainActivity.this,
						"更新失败，请注意手机能否访问到外网" + msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_update = (Button) findViewById(R.id.btn_update);
		btn_devicetest = (Button) findViewById(R.id.btn_devicetest);
		btn_add_file = (Button) findViewById(R.id.btn_add_file);
		btn_update.setOnClickListener(listener);
		btn_devicetest.setOnClickListener(listener);
		btn_add_file.setOnClickListener(listener);
		// btn_update.setText("已更新配置文件" + Read_SH() + "个（点击可更新）");
		mySharedPreferences = getSharedPreferences("update",
				Activity.MODE_PRIVATE);
		if (!mySharedPreferences.getString("productkey", "").equals("")){
			productkey = mySharedPreferences.getString("productkey", "");
			FinishDevice.setProductkey(productkey);
			Log.i("productkey1", productkey);
		}
		if (!mySharedPreferences.getString("PATH", "").equals("")) {
			String path = mySharedPreferences.getString("PATH", "");
			ParserTXTData(path);
		}
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Intent i = new Intent();
			switch (view.getId()) {
			case R.id.btn_devicetest:

				i.setClass(MainActivity.this, DeviceListActivity.class);
				startActivity(i);
				break;

			case R.id.btn_update:
				//productkey="81217144155446f692933ae0332a37f3";
				if (productkey.equals("")) {
					Toast.makeText(MainActivity.this, "productkey为空,请导入产测文件", Toast.LENGTH_SHORT)
					.show();
				} else {
					Toast.makeText(MainActivity.this, "正在下载"+productkey+"文件", Toast.LENGTH_SHORT)
					.show();
					/**文本格式问题，需要截取字符串*/
					//productkey = productkey.substring(1, productkey.length());
					
					XPGWifiSDK.sharedInstance().updateDeviceFromServer(
							productkey);
				}
				break;
			case R.id.btn_add_file:
				showFileChooser();
				break;

			default:
				break;
			}

		}
	};

	@Override
	public void didUpdateProduct(int result, String productKey) {
		Message msg = new Message();
		if (result == XPGWifiErrorCode.XPGWifiError_NONE) {
			// 更新配置文件成功，返回设备列表
			msg.obj = result;
			msg.what = SUCCESS;
			handler.sendMessage(msg);
		} else {
			// result - 下载的结果：-25=网络故障，-1=服务器返回的数据错误，0=成功。自动下载的情况下，下载的结果：1=不更新
			// 更新配置文件失败，弹出错误信息

			msg.obj = result;
			msg.what = FAIL;
			handler.sendMessage(msg);

		}
	}

	/**
	 * 打开存储器文件选择界面
	 */
	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
       
		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 存储器文件选择界面回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_CODE:
			
			if (resultCode == RESULT_OK) {
				// Get the Uri of the selected file
				// 导入开始
				// mSharedPreferences存储上次导入位置
				// mSharedPreferences =
				// getSharedPreferences("TestSharedPreferences", 0);
				Log.i("版本号", android.os.Build.VERSION.RELEASE);
				Uri uri = data.getData();
				String VERSION = android.os.Build.VERSION.RELEASE;
				VERSION = VERSION.substring(0, 3);
				Log.i("VERSION", VERSION);
				String path = null;
				if(Double.parseDouble(VERSION) > 4.4){
					path = GetPathFromUri4kitkat.getPath(this, uri);
				}else{
					path = FileUtils.getPath(this, uri);
				}			
				SharedPreferences.Editor mEditor = mySharedPreferences.edit();
				mEditor.putString("PATH", path);
				mEditor.commit();
				String Data = ParserTXTData(path);
				
				if(!Data.equals("")){
					Toast.makeText(MainActivity.this, "导入成功，请点击更新配置文件", Toast.LENGTH_SHORT)
					.show();
				}
				// mDevicedata.ParseData();

			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	

	/**
	 * 解析txt
	 */
	private String ParserTXTData(String path) {
		txtparser = new txtParser(this, path);
		String data1 = txtparser.readSDFile();
		list = new ArrayList<Map<String, Object>>();
		list = txtparser.returnList();
		productkey = txtparser.getProductkey();
		FinishDevice.setProductkey(productkey);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("productkey", productkey);
		editor.commit();
		mDevicedata = new Devicedata(list);
		mDevicedata.ParseData();
		return data1;
	}

}
