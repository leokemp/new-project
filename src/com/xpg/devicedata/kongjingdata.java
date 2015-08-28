package com.xpg.devicedata;

/**
 * @author QAer
 * 
 * 1，修改下载配置文件处的productkey,更改kongjingdata.java或里面的数据点
 * 2，增加测试命令
 * 3，主要规范
 * 4，对比测试
 *
 */
public class kongjingdata {
	
	

	// 电源
	public static final String KEY_POWER = "power";
	// 熄屏
	public static final String KEY_XIPIN = "xipin";
	// 童锁
	private static final String KEY_CHILD_LOCK = "child_lock";
	// 等离子
	private static final String KEY_PLASMA = "plasma";
	// 模式
	private static final String KEY_MODE = "mode";
	// 预约开机状态
	private static final String KEY_DATE_STATUS = "date_status";
	// 预约开机时间
	private static final String KEY_DATA_MINUTES = "date_minutes";
	// 预约开机时间
	private static final String KEY_PERIOD_STATUS = "period_status";
	// 预约开机时间
	private static final String KEY_PERIOD_START_TIME = "period_start_time";
	// 预约开机时间
	private static final String KEY_PERIOD_END_TIME = "period_end_time";
	// 预约开关机
	private static final String KEY_MORE_DATE_STATUS = "1,date_status,date_minutes";
	// 预约开机
	private static final String KEY_MORE_DATE_STATUS_DATA = "1,2";
	// 预约关机
	private static final String KEY_MORE_DATE_STATUS_C_DATA = "2,2";
	// 工作时段
	private static final String KEY_MORE_DATE_PERIOD_START = "1,period_status,period_start_time,period_end_time";
	// 工作时段数据
	private static final String KEY_MORE_DATE_PERIOD_START_DATA = "1,2,2";

	/** led红灯开关 0=关 1=开. */
	public static final String KEY_RED_SWITCH = "LED_R";

	public static final String TRUE = "true";

	 public static String CMD[] =
	 {KEY_POWER,KEY_POWER,KEY_POWER,KEY_XIPIN,KEY_XIPIN,KEY_CHILD_LOCK,KEY_CHILD_LOCK
	 ,KEY_PLASMA,KEY_PLASMA
	 ,KEY_MODE,KEY_MODE,KEY_MODE,KEY_MODE,KEY_MODE
	 ,KEY_POWER,KEY_MORE_DATE_STATUS,KEY_POWER,KEY_MORE_DATE_STATUS
	 ,KEY_POWER,KEY_MORE_DATE_PERIOD_START,KEY_POWER,KEY_POWER};
	
	 public static Object DATA[] = {true,false,true,true,false,true,false
	 ,false,true
	 ,3,2,1,4,5
	 ,false,KEY_MORE_DATE_STATUS_DATA,true,KEY_MORE_DATE_STATUS_C_DATA
	 ,false,KEY_MORE_DATE_PERIOD_START_DATA,false,false};

//	 public static String CMD[] =
//	 {KEY_MORE_DATE_STATUS,KEY_DATE_STATUS ,KEY_MODE,KEY_MODE,KEY_MODE};
//	 public static Object DATA[] = {KEY_MORE_DATE_STATUS_DATA,0,2,3,1};

}
