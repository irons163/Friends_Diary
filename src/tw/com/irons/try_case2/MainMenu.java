package tw.com.irons.try_case2;

import com.way.chat.activity.FriendListActivity;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.util.Constants;

import tw.com.irons.calendar.CalendarActivity;
import tw.com.irons.try_case2.db.MyDBHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TabHost;

public class MainMenu extends Activity {
	Button button, button2, button3, button4, button5, button6, button7,
			button8, button9;
	ImageButton imageButton;
	private MyDBHelper dbHelper;
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("backgroundMain", 0);
		if (bg == 0) {
			View layout = (View) findViewById(R.id.layout2);
			layout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.onef1));
		}
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout2);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}
		
		TranObject msg = (TranObject) getIntent().getSerializableExtra(Constants.MSGKEY);
		
		final Intent intent2 = new Intent(MainMenu.this, MyNote.class);
		intent2.putExtra(Constants.MSGKEY, msg);
		final Intent intent3 = new Intent(MainMenu.this, FriendListActivity.class);
		intent3.putExtra(Constants.MSGKEY, msg);

		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);
		button7 = (Button) findViewById(R.id.button7);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainMenu.this, MySchedule.class);
				startActivity(intent);
			}
		});

		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(MainMenu.this, MyNote.class);
				startActivity(intent2);
			}
		});

		button4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, MC.class);
				startActivity(intent);
			}
		});

		button5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, ClassNote.class);
				startActivity(intent);
			}
		});

		button6.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainMenu.this, Mrt.class);
				startActivity(intent);
			}
		});

		button7.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(MainMenu.this, FriendListActivity.class);
				startActivity(intent3);
			}
		});

	}
}
