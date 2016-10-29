package com.le.safe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.le.safe.service.ProtectedService;
import com.le.safe.utils.Contants;
import com.le.safe.utils.SharedPreferencesUtil;


public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("手机重启了.........");
		
		//根据防盗保护是否开启，来判断是否检测SIM卡的变化以及发送报警短信
		boolean sp_portected = SharedPreferencesUtil.getBoolean(context, Contants.PROTECTED, false);
		if (sp_portected) {
			//检测SIM卡是否发生变化，如果发生变化，发送报警短信
			//1.检测SIM卡是否发生变化，判断当前的SIM卡序列号和保存的SIM卡序列号是否一致
			//1.1获取保存的SIM卡序列号
			String sp_sim = SharedPreferencesUtil.getString(context, Contants.SIM, "");
			//1.2获取当前的SIM卡序列号
			TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String sim = tel.getSimSerialNumber();
			//1.3判断SIM卡序列号是否为空
			if (!TextUtils.isEmpty(sp_sim) && !TextUtils.isEmpty(sim)) {
				//1.4判断SIM卡序列号是否一致
				if (!sp_sim.equals(sim)) {
					//1.5发送报警短信
					SmsManager smsManager = SmsManager.getDefault();
					//destinationAddress : 收件人
					//scAddress : 短信服务中心的地址，一般null
					//text : 短信内容
					//sentIntent : 发送是否成功，PendingIntent ：延迟的意图
					//deliveryIntent : 短信的协议，一般null
					//将短信发送给设置的安全号码
					smsManager.sendTextMessage(SharedPreferencesUtil.getString(context, Contants.SAFENUMBER, "5556"), null, "da ge bei tou le,help me", null, null);
				}
			}
		}
		//手机重启的时候，开启守护进程
		context.startService(new Intent(context,ProtectedService.class));
	}

}
