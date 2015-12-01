/**
 * MainActivity.java
 * 
 * Version 1
 *
 * Date 20.11.2015
 * 
 * Copyright 2015 Mikhail Chepkin, mtch0604@gmail.com
 */

package com.mtch0604.test_sms;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import com.mtch0604.test_sms_greet.ActivityGreet;




/**
 *The MainActivity class provides a main application screen.
 * @author Mikhail Chepkin
 */
public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor>
{
	private static final String LOG_TAG = "TMS";
	
	private CustomAdapter scAdapter;
	
	private final String[] from = new String[] { Telephony.TextBasedSmsColumns.ADDRESS, 
												Telephony.TextBasedSmsColumns.TYPE, 
												Telephony.TextBasedSmsColumns.BODY };
    private final int[] to = new int[] { R.id.tvLVItem_Address, R.id.tvLVItem_Type, R.id.tvLVItem_Body };
        
    @Override
    protected void onCreate(Bundle savedInstanceState)  
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView lvSmsList = (ListView)findViewById(R.id.lvSmsList);
         
        scAdapter = new CustomAdapter(this, //context
        								R.layout.listviewitem,
        								null, //cursor
        								from, 
        								to, 
        								0	//flags
        								);
        lvSmsList.setAdapter(scAdapter);
        registerForContextMenu(lvSmsList);
        
        getSupportLoaderManager().initLoader(0, //id
        							null, //Bundle
        							this	//object that implements LoaderCallbacks<Cursor>
        							);
        
        //show splash screen
        Intent intent = new Intent(this, ActivityGreet.class);
        startActivity(intent);  
    }

    /**
     * Method onCreateLoader is an implementation for interface LoaderCallbacks<>
     */
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle arg1) 
	{
		Log.d(LOG_TAG, "Create loader");
        return new CursorLoader(
                    this,   // Parent activity context
                    Uri.parse(/*"content://sms/"*/ getString(R.string.MainActivity_SmsContentUri)),        // Table to query
                    new String[] { /*"_id"*/getString(R.string.MainActivity_FieldName_id), 
                    	Telephony.TextBasedSmsColumns.ADDRESS, 
                    	Telephony.TextBasedSmsColumns.TYPE, 
                    	Telephony.TextBasedSmsColumns.BODY 
                    	},     // Projection to return
                    null,            // No selection clause
                    null,            // No selection arguments
                    null             // Default sort order
        		);
        
	}

	/**
     * Method onLoadFinished is an implementation for interface LoaderCallbacks<>
     */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) 
	{
		Log.d(LOG_TAG, "Loading data finished");
		scAdapter.swapCursor(cursor);
	}

	/**
     * Method onLoaderReset is an implementation for interface LoaderCallbacks<>
     */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
	}
	
	private static final int CM_SENDEM_ID = 1;
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, //group id
				CM_SENDEM_ID, //item id
				0, //order
				getString(R.string.MainActivity_MenuCmd_SendEmail) //title
				);
	}
	
	private static final int PICK_CONTACT_REQUEST = 1;
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		if (item.getItemId() == CM_SENDEM_ID) 
		{
		      AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
		      final int nPos = acmi.position;
		      Cursor c = scAdapter.getCursor();
		      if(c.moveToPosition(nPos))
		      {
		    	  //form the message
		    	  final int nBody = c.getColumnIndex(Telephony.TextBasedSmsColumns.BODY);
		    	  final int nAddr = c.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS);
		    	  final String stBody = c.getString(nBody);
		    	  final String stAddr = c.getString(nAddr);
		    	  final String stMsg = stAddr + " " + stBody;
		    	  
		    	  //invoke an email application
		    	  Intent email = new Intent(Intent.ACTION_SEND);
		    	  email.putExtra(Intent.EXTRA_EMAIL, getString(R.string.MainActivity_SendEmail_DefaultAddr));		  
		    	  email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.MainActivity_SendEmail_DefaultText));
		    	  email.putExtra(Intent.EXTRA_TEXT, stMsg);
		    	  email.setType(getString(R.string.MainActivity_SendEmail_Type));
		    	  startActivity(Intent.createChooser(email, getString(R.string.MainActivity_SendEmail_ChooserText)));
		      }
		 }
		return super.onContextItemSelected(item);
	}

	
	/**
	 *The CustomAdapter class is a special adapter intended to load text data to ListView.
	 *It replaces the sms type numbers to words.
	 * @author Mikhail Chepkin
	 */
	private class CustomAdapter extends SimpleCursorAdapter
	{
		/**
		 * sms types translation table
		 */
		Map<Integer, String> mapTrans = new HashMap<Integer, String>() 
				{
	        		{
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_ALL,  
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_Any));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT, 
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_outgoing));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_FAILED,  
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_Any));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX, 
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_incoming));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_OUTBOX,  
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_outgoing));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_QUEUED, 
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_outgoing));
		        		put(Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT,  
		        				getString(R.string.MainActivity_CustomAdapter_MsgDirection_outgoing));
	        		}
	        	};
		
	    /**
	     * Method CustomAdapter is the class constructor.
	     */
		public CustomAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to, int flags) 
		{
			super(context, layout, c, from, to, flags);
		}
		
		/**
		 * Method bindView binds all the field names to their corresponding cursor columns
		 */
		@Override
		public void bindView(View view, Context context, Cursor cursor) 
		{
			final int count = to.length;
			for (int i = 0; i < count; i++) 
			{
				final TextView v = (TextView)view.findViewById(to[i]);
				if (v != null) 
				{
					final int nameCol = cursor.getColumnIndex(from[i]);
					String name = cursor.getString(nameCol);
					//special case
					if((Telephony.TextBasedSmsColumns.TYPE).equals(from[i]))
					{
			        	int nType = Integer.parseInt(name);
			        	name = mapTrans.get(nType);
					}
					v.setText(name);
				}
			}
		}
	}
}
