package com.xpg.application;

import java.io.IOException;
import android.util.Log;
import com.xpg.gokit.utils.AssertsUtils;
import com.xtremeprog.xpgconnect.XPGWifiSDK;


public class Application extends android.app.Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		try {
//			// 复制assert文件夹中的json文件到设备安装目录。json文件是解析数据点必备的文件，sdk根据该文件，把二进制数据转换为json字段并返回。
//			AssertsUtils.copyAllAssertToCacheFolder(this
//					.getApplicationContext());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// 启动SDK cce3a776d9c443bc9d77f668bee61f7d   e5481eeb10f84bbe945932c1e08e8f5a
		XPGWifiSDK.sharedInstance().startWithAppID(getApplicationContext(),
				"null");
		
		// 设定日志打印级别
		XPGWifiSDK.sharedInstance().setLogLevel(
				XPGWifiSDK.XPGWifiLogLevel.XPGWifiLogLevelAll, "GoKitDemo.log",
				true);
	}

}
