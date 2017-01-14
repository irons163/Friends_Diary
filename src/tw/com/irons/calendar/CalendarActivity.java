/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tw.com.irons.calendar;

import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;

import tw.com.irons.try_case2.MainMenu;
import tw.com.irons.try_case2.R;
import tw.com.irons.try_case2.SetMenu;
import tw.com.irons.try_case2.R.drawable;
import tw.com.irons.try_case2.R.id;
import tw.com.irons.try_case2.R.layout;
import tw.com.irons.try_case2.utils.DateUtil2;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity  implements CalendarView.OnCellTouchListener{
	public static final String MIME_TYPE = "vnd.android.cursor.dir/vnd.exina.android.calendar.date";
	CalendarView mView = null;
	TextView mHit;
	Handler mHandler = new Handler();
	
	Button setBtn;
	ImageButton beforeBtn, nextBtn;
	TextView dateText;
	int year;
	int month;
	myResetReceiver receiver;
	public static int mcDay,mcYear,mcMonth;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender_main);
        
        SharedPreferences preferences = getSharedPreferences("clickDate", 0);    
        int bg =preferences.getInt("background", 0);
        if(bg==0){
            View layout = (View)findViewById(R.id.layout1);
     		layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.dot_blue));
			SharedPreferences preferences3 = getSharedPreferences("clickDate", 0);
			SharedPreferences.Editor editor = preferences3.edit();
			editor.putInt("background", R.drawable.dot_blue);
			editor.commit();
            }
        if(bg!=0){
        View layout = (View)findViewById(R.id.layout1);
 		layout.setBackgroundDrawable(getResources().getDrawable(bg));
        }
        
        SharedPreferences preferences2 = getSharedPreferences("clickDate", 0);    
        mcDay =preferences2.getInt("mcDay", 0);
        mcMonth =preferences2.getInt("mcMonth", 0);
        mcYear =preferences2.getInt("mcYear", 0);
        
        mView = (CalendarView)findViewById(R.id.calendar);
        mView.setOnCellTouchListener(this);
        
        //if(getIntent().getAction().equals(Intent.ACTION_PICK))
        	//findViewById(R.id.hint).setVisibility(View.INVISIBLE);
        
        setBtn =(Button)findViewById(R.id.setbutton1);
        setBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CalendarActivity.this, SetMenu.class);
				startActivity(intent);
			}
		});
        
        dateText=(TextView)findViewById(R.id.dateText);
		year  = mView.getYear();
		month = mView.getMonth()+1; 
        dateText.setText(year+"�~"+month+"��");
        
        beforeBtn=(ImageButton)findViewById(R.id.calenderImageButton01);
        nextBtn=(ImageButton)findViewById(R.id.calenderImageButton02);
        
        beforeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mView.previousMonth();
				year  = mView.getYear();
				month = mView.getMonth()+1;
				dateText.setText(year+"�~"+month+"��");
			}
		});
        
        nextBtn.setOnClickListener(new OnClickListener() {
			
  			@Override
  			public void onClick(View v) {
  				mView.nextMonth();
  				year  = mView.getYear();
  				month = mView.getMonth()+1;
  				dateText.setText(year+"�~"+month+"��");
  			}
  		});
        
		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2.BG");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);
    }
    
	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
	        SharedPreferences preferences = getSharedPreferences("clickDate", 0);    
	        int bg =preferences.getInt("background", 0);
			View layout = (View)findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}		
	}
	
	public void onTouch(Cell cell) {
		Intent intent = getIntent();
		String action = intent.getAction();

			year  = mView.getYear();
			month = mView.getMonth();
			int day   = cell.getDayOfMonth();
			
			// FIX issue 6: make some correction on month and year
			if(cell instanceof CalendarView.GrayCell) {
				// oops, not pick current month...				
				if (day < 15) {
					// pick one beginning day? then a next month day
					if(month==11)
					{
						month = 0;
						year++;
					} else {
						month++;
					}
					
				} else {
					// otherwise, previous month
					if(month==0) {
						month = 11;
						year--;
					} else {
						month--;
					}
				}
			}
			
			
			month=month+1;
			//MyApplication application = (MyApplication)this.getApplicationContext();
			//MyApplication.clickDate = DateUtil2.getdate(year+"", month+"", day+"", 100);
			String clickDate = DateUtil2.getdate(year+"", month+"", day+"", 100);
			SharedPreferences preferences = getSharedPreferences("clickDate", 0);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("clickDate", clickDate);
			editor.commit();
			
			Toast.makeText(CalendarActivity.this, clickDate , Toast.LENGTH_SHORT).show();
			
			//Intent msgIntent = getIntent();
			TranObject msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);
			
			Intent intent2 = new Intent(CalendarActivity.this, MainMenu.class);
			intent2.putExtra(Constants.MSGKEY, msg);
			startActivity(intent2);
			
			


		mHandler.post(new Runnable() {
			public void run() {
				Toast.makeText(CalendarActivity.this, DateUtils.getMonthString(mView.getMonth(), DateUtils.LENGTH_LONG) + " "+mView.getYear(), Toast.LENGTH_SHORT).show();
			}
		});
		
		//finish();
		return;
	}

    
}