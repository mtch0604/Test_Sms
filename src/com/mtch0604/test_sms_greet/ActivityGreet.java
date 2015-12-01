/**
 * ActivityGreet.java
 * 
 * Version 1
 *
 * Date 20.11.2015
 * 
 * Copyright 2015 Mikhail Chepkin, mtch0604@gmail.com
 */
package com.mtch0604.test_sms_greet;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.mtch0604.test_sms.R;
import com.mtch0604.test_sms.R.layout;


/**
 *The ActivityGreet class provides a splash screen.
 * @author Mikhail Chepkin
 */
public class ActivityGreet extends Activity 
{
	private AsyncTask<Void, Void, Void> waitTask;
	private static final String LOG_TAG = "TMS";
	private static final int SECONDS_TO_WAIT = 3;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_greet);
		
		waitTask = new AsyncTask<Void, Void, Void> ()
		{
			@Override
			protected Void doInBackground(Void... arg0) 
			{
				Log.d(LOG_TAG, "Wait thread start");
				try 
				{
					int nLoops = SECONDS_TO_WAIT*10;
					for(int n=0; n<nLoops; n++)
					{
						//TimeUnit.SECONDS.sleep(3);
						TimeUnit.MILLISECONDS.sleep(100);
						if(isCancelled())
						{
							break;
						}
					}
			    } catch (InterruptedException e) 
			    {
			        e.printStackTrace();
			    }
				Log.d(LOG_TAG, "Wait thread end");
				return null;
			}
			
			protected void onPostExecute(Void result) 
			{
				finish();
			};
		}.execute();
		
	}
	
	@Override
	protected void onDestroy() 
	{
		waitTask.cancel(false);
		super.onDestroy();
	}
}
