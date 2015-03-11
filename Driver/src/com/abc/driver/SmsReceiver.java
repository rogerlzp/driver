package com.abc.driver;



import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver{
	
	public static final String TAG = "SmsReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage msg = null;
		
		if (null != bundle) {
			Object[] smsObj = (Object[]) bundle.get("pdus");
			for (Object object:smsObj) {
				msg = SmsMessage.createFromPdu((byte[]) object);
				Date current_date = new Date(msg.getTimestampMillis());
				SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String receiveTime = sFormat.format(current_date);
				
				 System.out.println("number:" + msg.getOriginatingAddress()  
			                + "   body:" + msg.getDisplayMessageBody() + "  time:"  
			                        + msg.getTimestampMillis());  

			}
		}
		
	}

}
