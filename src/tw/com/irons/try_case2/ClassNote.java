package tw.com.irons.try_case2;

import tw.com.irons.try_case2.db.sql_schedule;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ClassNote extends Activity {
	Button[] btns = new Button[35];
	int[] btnIds = { R.id.classBtn01, R.id.classBtn02, R.id.classBtn03,
			R.id.classBtn04, R.id.classBtn05, R.id.classBtn06, R.id.classBtn07,
			R.id.classBtn08, R.id.classBtn09, R.id.classBtn10, R.id.classBtn11,
			R.id.classBtn12, R.id.classBtn13, R.id.classBtn14, R.id.classBtn15,
			R.id.classBtn16, R.id.classBtn17, R.id.classBtn18, R.id.classBtn19,
			R.id.classBtn20, R.id.classBtn21, R.id.classBtn22, R.id.classBtn23,
			R.id.classBtn24, R.id.classBtn25, R.id.classBtn26, R.id.classBtn27,
			R.id.classBtn28, R.id.classBtn29, R.id.classBtn30, R.id.classBtn31,
			R.id.classBtn32, R.id.classBtn33, R.id.classBtn34, R.id.classBtn35 };
	private sql_schedule sql_schedule;
	private SQLiteDatabase db;
	myResetReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class);

		sql_schedule = new sql_schedule(ClassNote.this);
		db = sql_schedule.getWritableDatabase();

		for (int i = 0; i < 35; i++) {
			btns[i] = (Button) findViewById(btnIds[i]);
			final int id = i;
			btns[i].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AlertDialog alert = null;

					LayoutInflater dialog = LayoutInflater.from(ClassNote.this);
					View dview = dialog.inflate(R.layout.class_dialog, null);

					final EditText et = (EditText) dview
							.findViewById(R.id.editText1);

					AlertDialog.Builder builder = new AlertDialog.Builder(
							ClassNote.this);
					builder.setTitle("�Ҫ��T");
					builder.setView(dview);
					builder.setCancelable(true);

					builder.setPositiveButton("�T�w",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									String str = et.getText().toString();
									sql_schedule.insert(str, id);
									sql_schedule.update(str, id);

									Intent intent = new Intent(
											"tw.com.irons.try_case2.Class");
									sendBroadcast(intent);
								}
							});

					builder.setNegativeButton("���",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					alert = builder.create();
					alert.show();
				}
			});
		}

		Cursor cursor = sql_schedule.getAll();
		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			btns[id].setText(cursor.getString(1));
		}

		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2.Class");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);
	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			Cursor cursor = sql_schedule.getAll();
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				btns[id].setText(cursor.getString(1));
			}
		}
	}


}
