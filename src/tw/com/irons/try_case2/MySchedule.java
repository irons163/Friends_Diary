package tw.com.irons.try_case2;

import static tw.com.irons.try_case2.Constant.DIALOG_ABOUT;
import static tw.com.irons.try_case2.Constant.DIALOG_ALL_DEL_CONFIRM;
import static tw.com.irons.try_case2.Constant.DIALOG_CHECK;
import static tw.com.irons.try_case2.Constant.DIALOG_SCH_DEL_CONFIRM;
import static tw.com.irons.try_case2.Constant.DIALOG_SET_DATETIME;
import static tw.com.irons.try_case2.Constant.DIALOG_SET_SEARCH_RANGE;
import static tw.com.irons.try_case2.Constant.getNowDateString;
import static tw.com.irons.try_case2.db.DBUtil2.deleteSchedule;
import static tw.com.irons.try_case2.db.DBUtil2.deleteType;
import static tw.com.irons.try_case2.db.DBUtil2.getAllType;
import static tw.com.irons.try_case2.db.DBUtil2.insertSchedule;
import static tw.com.irons.try_case2.db.DBUtil2.insertType;
import static tw.com.irons.try_case2.db.DBUtil2.loadSchedule;
import static tw.com.irons.try_case2.db.DBUtil2.loadType;
import static tw.com.irons.try_case2.db.DBUtil2.searchSchedule;
import static tw.com.irons.try_case2.db.DBUtil2.updateSchedule;

import java.util.ArrayList;
import java.util.Calendar;

import tw.com.irons.try_case2.Constant.Layout;
import tw.com.irons.try_case2.Constant.WhoCall;
import tw.com.irons.try_case2.utils.ImageAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint({ "ResourceAsColor", "ResourceAsColor", "ResourceAsColor" })
public class MySchedule extends Activity {
	public String[] defultType = new String[] { "���h", "����", "���k" };// ܛ�w���������܄h����Ĭ�J���
	Dialog dialogSetRange;// �г̲��ҕr�O��������ʼ�����Č�Ԓ���K
	Dialog dialogSetDatetime;// �½����޸��г̕r�O�����ں͕r�g�Č�Ԓ���K
	Dialog dialogSchDelConfirm;// �h���г̕r�Ĵ_�J��Ԓ���K
	Dialog dialogCheck;// �������в鿴�г�Ԕ�����ݵČ�Ԓ���K
	Dialog dialogAllDelConfirm;// �h��ȫ���^���г̕r�Ĵ_�J��Ԓ���K
	Dialog dialogAbout;// �P춌�Ԓ���K
	public static ArrayList<String> alType = new ArrayList<String>();// �惦�����г���͵�arraylist
	public static ArrayList<Schedule> alSch = new ArrayList<Schedule>();// �惦����schedule�����ArrayList
	public Schedule schTemp;// �R�r��schedule
	public ArrayList<Boolean> alSelectedType = new ArrayList<Boolean>();// ӛ䛲��ҽ��������ǰ��checkbox��B��
	public String rangeFrom = getNowDateString();// �����г̕r�O�õ���ʼ���ڣ�Ĭ�J��ǰ����
	public String rangeTo = rangeFrom;// �����г̕r�O�õĽKֹ���ڣ�Ĭ�J��ǰ����
	Layout curr = null;// ӛ䛮�ǰ�����ö�e���
	WhoCall wcSetTimeOrAlarm;// �Á��Д��{�Õr�g���ڌ�Ԓ���K�İ��o���O�Õr�g߀���O���[�,�Ա���Č�Ԓ���K�е�һЩ�����ԓ�O�Þ�visible߀��gone
	WhoCall wcNewOrEdit;// �Á��Д��{���г̾�݋��������½��г̰��o߀�����޸��г̰��o���Ա��O�Ì����Ľ�����}
	int sel = 0;

	Schedule todaySch;
	String[] clickDate;
	myResetReceiver receiver;
	int littleImage;
	ImageView littleImageView;
	ListView listView;
	Cursor cursor2;
	TextView hasListTextView;
	ImageButton bEdit;
	ImageButton bCheck;
	ImageButton bDel;
	View chooseList;
	TextView tvTime;
	String hour;
	String min;
	
	int hightlight;
	View v;
	/*
	 * �R�rӛ��½��г̽����e�����spinner��position������O�Õr�g�Č�Ԓ���Kcancel��
	 * �ص��½��г̽���r��ˢ�����п���헣�spinner�����x�е��ĿҲ���ص�Ĭ�J
	 */
	Handler hd = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				gotoMain();
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// �o���}
		gotoMain();

		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2_2");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);

	}

	// ===================================������start===========================================
	public void gotoMain()// ��ʼ��������
	{
		setContentView(R.layout.schedule);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.MAIN;
		sel = 0;

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// ӛ�ListView������x���˵Ę��IλԪ

		bEdit = (ImageButton) findViewById(R.id.ibmainEdit);// �޸��г̰��o
		bDel = (ImageButton) findViewById(R.id.ibmainDel);// �h����ǰ�x���г̵İ��o
		ImageButton bNew = (ImageButton) findViewById(R.id.ibmainNew);// �½��г̰��o
		ImageButton bSearch = (ImageButton) findViewById(R.id.ibmainSearch);// �����г̰��o
		// final ListView lv=(ListView)findViewById(R.id.lvmainSchedule);//�г��б�

		bEdit.setEnabled(false);// �A�O�O�鲻���à�B
		bDel.setEnabled(false);

		alSch.clear();// ���Y�ώ��xȡ֮ǰ��մ惦�г̵�arraylist
		loadSchedule(this);// ���Y�ώ����xȡ�г�
		loadType(this);// ���Y�ώ����xȡ���

		alIsSelected.clear();

		for (int i = 0; i < alSch.size(); i++)// ȫ���O�Þ�false�����]��һ��x��
		{
			alIsSelected.add(false);
		}

		final ArrayList<String> type = getAllType(MySchedule.this);// �@ȡ�Ѵ��г��е�������ͺ��Ñ��Խ����������

		SharedPreferences preferences2 = getSharedPreferences("clickDate", 0);

		clickDate = preferences2.getString("clickDate", "").split("-");

		int year1 = Integer.parseInt(clickDate[0]);
		int month1 = Integer.parseInt(clickDate[1]);
		int day1 = Integer.parseInt(clickDate[2]);
		int year2 = year1;
		int month2 = month1;
		int day2 = day1;

		if (day1 > getMaxDayOfMonth(year1, month1)
				|| day2 > getMaxDayOfMonth(year2, month2)) {
			Toast.makeText(MySchedule.this, "�����O���e�`", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		rangeFrom = Schedule.toDateString(year1, month1, day1);
		rangeTo = Schedule.toDateString(year2, month2, day2);

		alSelectedType.clear();

		for (int i = 0; i < type.size(); i++)// �x�С�ȫ���x�С����listview�e��������������checkbox�O���x�Р�B
		{
			alSelectedType.add(i, true);
		}

		cursor2 = searchSchedule(MySchedule.this, type);// ���������ce����

		alSelectedType.clear();

		TextView tvType = (TextView) findViewById(R.id.tvdialogcheckType);// �@ʾ��͵�TextView
		TextView tvTitle = (TextView) findViewById(R.id.tvdialogcheckTitle);// �@ʾ���}��TextView
		TextView tvNote = (TextView) findViewById(R.id.tvdialogcheckNote);// �@ʾ���]��TextView
		TextView tvDatetime1 = (TextView) findViewById(R.id.tvdialogcheckDate1);// �@ʾ�г����ں͕r�g��TextView
		// TextView
		// tvDatetime2=(TextView)findViewById(R.id.tvdialogcheckDate2);//�@ʾ�[����ں͕r�g��TextView
		hasListTextView = (TextView) findViewById(R.id.hasListTextView);
		Button btnEdit = (Button) findViewById(R.id.bdialogcheckEdit);// ��݋���o
		Button btnDel = (Button) findViewById(R.id.bdialogcheckDel);// �h�����o
		Button bBack = (Button) findViewById(R.id.bdialogcheckBack);// ���ذ��o

		showTodayList();

		// bNew�O��
		bNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				// String[] clickDate = new String[3];
				// clickDate=MyApplication.clickDate.split("-");

				int year1 = Integer.parseInt(clickDate[0]);
				int month1 = Integer.parseInt(clickDate[1]);
				int day1 = Integer.parseInt(clickDate[2]);

				schTemp = new Schedule(year1, month1, day1);// �R�r�½�һ���г�������������O�鮔ǰ����
				schTemp.setLittleImage(R.drawable.ttt);
				littleImage = R.drawable.ttt;

				wcNewOrEdit = WhoCall.NEW;// �{���г̾�݋��������½����o
				gotoSetting();// ȥ�г̾�݋����
			}
		});
		// bEdit�O��
		bEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wcNewOrEdit = WhoCall.EDIT;// �{���г̾�݋��������޸İ��o
				gotoSetting();// ȥ�г̾�݋����
			}
		});

		// �h���x�е��г̰��o
		bDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SCH_DEL_CONFIRM);
			}
		});

		// �г̲��Ұ��o
		bSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSearch();
			}
		});

	}

	// ===================================�г̾�݋����start=====================================
	public void gotoSetting()// ��ʼ���½��г̽���
	{
		setContentView(R.layout.newschedule);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.SETTING;

		TextView tvTitle = (TextView) findViewById(R.id.tvnewscheduleTitle);
		if (wcNewOrEdit == WhoCall.NEW) {
			tvTitle.setText("�½��г�");
		} else if (wcNewOrEdit == WhoCall.EDIT) {
			tvTitle.setText("�޸��г�");
		}

		littleImageView = (ImageView) findViewById(R.id.imageView1);

		littleImageView.setBackgroundDrawable(getResources().getDrawable(
				schTemp.getLittleImage()));

		final Spinner spType = (Spinner) findViewById(R.id.spxjrcType);
		Button bNewType = (Button) findViewById(R.id.bxjrcNewType);
		final EditText etTitle = (EditText) findViewById(R.id.etxjrcTitle);
		final EditText etNote = (EditText) findViewById(R.id.etxjrcNote);
		TextView tvDate = (TextView) findViewById(R.id.tvnewscheduleDate);
		Button bSetDate = (Button) findViewById(R.id.bxjrcSetDate);
		tvTime = (TextView) findViewById(R.id.tvnewscheduleTime);

		Button bDone = (Button) findViewById(R.id.bxjrcDone);
		Button bCancel = (Button) findViewById(R.id.bxjrcCancel);

		Button button = (Button) findViewById(R.id.button1);

		etTitle.setText(schTemp.getTitle());
		etNote.setText(schTemp.getNote());
		tvDate.setText(schTemp.getDate1());
		tvTime.setText(schTemp.getTimeSet() ? schTemp.getTime1() : "�o���w�r�g");

		// ���spinner�O��
		spType.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return alType.size();
			}

			@Override
			public Object getItem(int position) {
				return alType.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MySchedule.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				TextView tv = new TextView(MySchedule.this);
				tv.setText(alType.get(position));
				tv.setTextSize(17);
				tv.setTextColor(R.color.black);
				return tv;
			}
		});
		spType.setSelection(sel);

		// �½��г���Ͱ��o
		bNewType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());// ���ѽ�ݔ���title��note����schTemp���Է����ؕr�����
				schTemp.setNote(etNote.getText().toString());
				sel = spType.getSelectedItemPosition();// �惦spType�Į�ǰ�x��
				gotoTypeManager();// �M���г���͹������
			}
		});

		bSetDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());// ���ѽ�ݔ������}�͂��]����schTemp���Է��O����r�g���[犷��ؕr�����
				schTemp.setNote(etNote.getText().toString());
				sel = spType.getSelectedItemPosition();
				wcSetTimeOrAlarm = WhoCall.SETTING_DATE;// �{���O�����ڕr�g��Ԓ���K�����O���г����ڰ��o
				showDialog(DIALOG_SET_DATETIME);
			}
		});

		// ��ɰ��o�O��
		bDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if((hour!=null)   && (min!=null)){
				schTemp.setTime1(hour, min);
				}
				// ׌�½����г̕r�g�ͮ�ǰ�r�g���^���Ƿ��^��
				if (schTemp.isPassed()) {
					Toast.makeText(MySchedule.this, "���܄����^���г�",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String title = etTitle.getText().toString().trim();
				if (title.equals(""))// ����г̘��}�]��ݔ�룬Ĭ�J��δ����
				{
					title = "δ����";
				}
				schTemp.setTitle(title);
				String note = etNote.getText().toString();
				schTemp.setNote(note);
				String type = (String) spType.getSelectedItem();
				schTemp.setType(type);

				schTemp.setLittleImage(littleImage);

				if (wcNewOrEdit == WhoCall.NEW)// �����ǰ�������½��г̣��{�ò����г̷���
				{
					insertSchedule(MySchedule.this);
				} else if (wcNewOrEdit == WhoCall.EDIT)// �����ǰ�������޸��г̣��{�ø����г̷���
				{
					updateSchedule(MySchedule.this);
				}

				gotoMain();
			}

		});
		// ȡ�����o�O��
		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMain();
			}

		});

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog alert = null;
				LayoutInflater dialog = LayoutInflater.from(MySchedule.this);
				View dview = dialog.inflate(R.layout.set_bg_menu, null);

				final Integer[] mImageIds = { R.drawable.p01, R.drawable.p02,
						R.drawable.p03, R.drawable.p04, R.drawable.p05,
						R.drawable.p06, R.drawable.p07, R.drawable.p08,
						R.drawable.p09, R.drawable.p10, R.drawable.p11,
						R.drawable.p12, R.drawable.p13, R.drawable.p14,
						R.drawable.p15, R.drawable.p16 };

				GridView gridView01 = (GridView) dview
						.findViewById(R.id.GridView01);
				gridView01.setAdapter(new ImageAdapter(MySchedule.this, 85, 85,
						mImageIds)); // �O�� image ����
				gridView01.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						final PopupWindow popWin = new PopupWindow(
								MySchedule.this);
						// ���x PopupWindow �Ⱥ��� ImageView
						ImageView iv = new ImageView(MySchedule.this);
						// �� ImageView ���O���D��
						iv.setImageDrawable(getResources().getDrawable(
								mImageIds[position]));
						// �� popup window �����O���� ImageView
						popWin.setContentView(iv);
						popWin.setFocusable(true);
						popWin.setWidth(300);
						popWin.setHeight(300);
						popWin.showAtLocation(view, Gravity.CENTER, 0, 0);

						littleImage = mImageIds[position];

					}
				});

				AlertDialog.Builder builder = new AlertDialog.Builder(
						MySchedule.this);
				builder.setTitle("�����YӍ");
				builder.setView(dview);
				builder.setCancelable(true);

				builder.setPositiveButton("�_��",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										"tw.com.irons.try_case2_2");
								sendBroadcast(intent);
							}

						});

				builder.setNegativeButton("ȡ��",
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

	// ===================================��͹������start=====================================
	public void gotoTypeManager() {
		setContentView(R.layout.typemanager);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.TYPE_MANAGER;
		final ListView lvType = (ListView) findViewById(R.id.lvtypemanagerType);// �б��г������������
		final EditText etNew = (EditText) findViewById(R.id.ettypemanagerNewType);// ݔ����������Q��TextView
		final Button bNew = (Button) findViewById(R.id.btypemanagerNewType);// �½���Ͱ��o
		final Button bBack = (Button) findViewById(R.id.btypemanagerBack);// ������һ퓰��o

		bBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSetting();
			}
		});

		lvType.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return alType.size();
			}

			@Override
			public Object getItem(int position) {
				return alType.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MySchedule.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setGravity(Gravity.CENTER_VERTICAL);
				TextView tv = new TextView(MySchedule.this);
				tv.setText(alType.get(position));
				tv.setTextSize(17);
				tv.setTextColor(Color.BLACK);
				tv.setPadding(20, 0, 0, 0);
				ll.addView(tv);

				// ܛ�w�Ԏ�����Ͳ��܄h���������Խ�����������һ���t���Á�h���Խ����
				if (position >= defultType.length) {
					ImageButton ib = new ImageButton(MySchedule.this);
					ib.setBackgroundDrawable(MySchedule.this.getResources()
							.getDrawable(R.drawable.cross));
					ib.setLayoutParams(new LayoutParams(24, 24));
					ib.setPadding(20, 0, 0, 0);

					ib.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							deleteType(MySchedule.this, lvType
									.getItemAtPosition(position).toString());
							loadType(MySchedule.this);
							gotoTypeManager();
						}
					});
					ll.addView(ib);
				}
				return ll;
			}
		});

		bNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newType = etNew.getText().toString().trim();
				if (newType.equals("")) {
					Toast.makeText(MySchedule.this, "������Q���ܞ�ա�",
							Toast.LENGTH_SHORT).show();
					return;
				}
				insertType(MySchedule.this, newType);
				gotoTypeManager();
			}
		});
	}

	// ===================================���ҽ���start=========================================
	public void gotoSearch() {
		setContentView(R.layout.search);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.SEARCH;
		final Button bChange = (Button) findViewById(R.id.bsearchChange);// ��׃���ҹ������o
		final Button bSearch = (Button) findViewById(R.id.bsearchGo);// �_ʼ����
		final Button bCancel = (Button) findViewById(R.id.bsearchCancel);// ȡ��
		final CheckBox cbDateRange = (CheckBox) findViewById(R.id.cbsearchDateRange);// �����Ƿ����ƹ�����CheckBox
		final CheckBox cbAllType = (CheckBox) findViewById(R.id.cbsearchType);// �Ƿ�������������в��ҵ�CheckBox
		final ListView lv = (ListView) findViewById(R.id.lvSearchType);// �����������lv��
		final TextView tvFrom = (TextView) findViewById(R.id.tvsearchFrom);// ������ʼ�r�ڵ�tv
		final TextView tvTo = (TextView) findViewById(R.id.tvsearchTo);// //���ҽKֹ�r�ڵ�tv

		tvFrom.setText(rangeFrom);
		tvTo.setText(rangeTo);

		final ArrayList<String> type = getAllType(MySchedule.this);// �@ȡ�Ѵ��г��е�������ͺ��Ñ��Խ����������

		alSelectedType.clear();
		for (int i = 0; i < type.size(); i++)// �A�O����������O�à�BλԪfalse
		{
			alSelectedType.add(false);
		}

		cbDateRange.setOnCheckedChangeListener(new OnCheckedChangeListener() {// �����Ƿ��������ڹ�����CheckBox�Q���������ڹ����İ��o�Ƿ����
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						bChange.setEnabled(isChecked);
					}
				});

		// �O�á���ȫ���������������CheckBox��׃��B�r���О�
		cbAllType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (int i = 0; i < type.size(); i++)// �x�С�ȫ���x�С����listview�e��������������checkbox�O���x�Р�B
				{
					alSelectedType.set(i, isChecked);
				}
				lv.invalidateViews();// ˢ��ListView??
			}
		});

		bChange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SET_SEARCH_RANGE);
			}
		});

		lv.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return type.size();
			}

			@Override
			public Object getItem(int position) {
				return type.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MySchedule.this);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setGravity(Gravity.CENTER_VERTICAL);
				LinearLayout llin = new LinearLayout(MySchedule.this);
				llin.setPadding(20, 0, 0, 0);
				ll.addView(llin);
				CheckBox cb = new CheckBox(MySchedule.this);
				cb.setButtonDrawable(R.drawable.checkbox);
				llin.addView(cb);
				cb.setChecked(alSelectedType.get(position));// ��ArrayList�e��惦�Ġ�B�O��CheckBox��B

				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						alSelectedType.set(position, isChecked);// ��׃ArrayList�e�挦��λ��booleanֵ
					}
				});

				TextView tv = new TextView(MySchedule.this);
				tv.setText(type.get(position));
				tv.setTextSize(18);
				tv.setTextColor(R.color.black);
				ll.addView(tv);
				return ll;
			}
		});

		bSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ����]��һ����ͱ��x�Єt��ʾ
				boolean tmp = false;
				for (boolean b : alSelectedType) {
					tmp = tmp | b;
				}
				if (tmp == false) {
					Toast.makeText(MySchedule.this, "Ո�����x��һ�����",
							Toast.LENGTH_SHORT).show();
					return;
				}

				searchSchedule(MySchedule.this, type);
				gotoSearchResult();
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoMain();
			}
		});
	}

	// ===================================���ҽY������start=====================================
	public void gotoSearchResult()// ԓ�����������������ˎׂ����o������ȫһ��
	{
		setContentView(R.layout.searchresult);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.SEARCH_RESULT;

		sel = 0;

		final ImageButton bCheck = (ImageButton) findViewById(R.id.ibsearchresultCheck);
		final ImageButton bEdit = (ImageButton) findViewById(R.id.ibsearchresultEdit);
		final ImageButton bDel = (ImageButton) findViewById(R.id.ibsearchresultDel);
		ImageButton bBack = (ImageButton) findViewById(R.id.ibsearchresultBack);
		ListView lv = (ListView) findViewById(R.id.lvsearchresultSchedule);

		bCheck.setEnabled(false);
		bEdit.setEnabled(false);
		bDel.setEnabled(false);

		// �����ǲ��ҽY����ListView�O��
		lv.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return alSch.size();
			}

			@Override
			public Object getItem(int position) {
				return alSch.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout ll = new LinearLayout(MySchedule.this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.setPadding(5, 5, 5, 5);

				LinearLayout llUp = new LinearLayout(MySchedule.this);
				llUp.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout llDown = new LinearLayout(MySchedule.this);
				llDown.setOrientation(LinearLayout.HORIZONTAL);

				TextView tvDate = new TextView(MySchedule.this);
				tvDate.setText(alSch.get(position).getDate1() + "   ");
				tvDate.setTextSize(17);
				tvDate.setTextColor(Color.parseColor("#129666"));
				llUp.addView(tvDate);

				TextView tvTime = new TextView(MySchedule.this);
				tvTime.setText(alSch.get(position).timeForListView());
				tvTime.setTextSize(17);
				tvTime.setTextColor(Color.parseColor("#925301"));
				llUp.addView(tvTime);

				if (alSch.get(position).isPassed())// ���г����^�ڣ��t���ں͕r�g�ɫ������ɫ׃��
				{
					tvDate.setTextColor(Color.parseColor("#292929"));
					tvTime.setTextColor(Color.parseColor("#292929"));
					ll.setBackgroundColor(Color.parseColor("#818175"));
				}

				if (alSch.get(position).getAlarmSet()) {
					ImageView iv = new ImageView(MySchedule.this);
					iv.setImageDrawable(getResources().getDrawable(
							R.drawable.alarm));
					iv.setLayoutParams(new LayoutParams(20, 20));
					llUp.addView(iv);
				}

				TextView tvType = new TextView(MySchedule.this);
				tvType.setText(alSch.get(position).typeForListView());
				tvType.setTextSize(17);
				tvType.setTextColor(Color.parseColor("#b20000"));
				llDown.addView(tvType);

				TextView tvTitle = new TextView(MySchedule.this);
				tvTitle.setText(alSch.get(position).getTitle());
				tvTitle.setTextSize(17);
				tvTitle.setTextColor(Color.parseColor("#000000"));
				llDown.addView(tvTitle);

				ll.addView(llUp);
				ll.addView(llDown);
				return ll;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				bCheck.setEnabled(true);
				bEdit.setEnabled(true);
				bDel.setEnabled(true);
				schTemp = alSch.get(arg2);// �x��ĳ�������r����ԓ���������x�oschTemp
			}
		});

		// �޸��г̰��o�O��
		bEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wcNewOrEdit = WhoCall.EDIT;
				gotoSetting();
			}
		});
		// �h���x���г̰��o�O��
		bDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SCH_DEL_CONFIRM);
			}
		});

		// �����г̰��o�O��
		bBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSearch();
			}

		});

		// �鿴�г̰��o�O��
		bCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_CHECK);
			}
		});
	}

	// ������Ԓ���K
	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_SET_SEARCH_RANGE:
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setItems(null, null);
			dialogSetRange = b.create();
			dialog = dialogSetRange;
			break;

		case DIALOG_SET_DATETIME:
			AlertDialog.Builder abSetDatetime = new AlertDialog.Builder(this);
			abSetDatetime.setItems(null, null);
			dialogSetDatetime = abSetDatetime.create();
			dialog = dialogSetDatetime;
			break;

		case DIALOG_SCH_DEL_CONFIRM:
			AlertDialog.Builder abSchDelConfirm = new AlertDialog.Builder(this);
			abSchDelConfirm.setItems(null, null);
			dialogSchDelConfirm = abSchDelConfirm.create();
			dialog = dialogSchDelConfirm;
			break;

		case DIALOG_CHECK:
			AlertDialog.Builder abCheck = new AlertDialog.Builder(this);
			abCheck.setItems(null, null);
			dialogCheck = abCheck.create();
			dialog = dialogCheck;
			break;

		case DIALOG_ALL_DEL_CONFIRM:
			AlertDialog.Builder abAllDelConfirm = new AlertDialog.Builder(this);
			abAllDelConfirm.setItems(null, null);
			dialogAllDelConfirm = abAllDelConfirm.create();
			dialog = dialogAllDelConfirm;
			break;

		case DIALOG_ABOUT:
			AlertDialog.Builder abAbout = new AlertDialog.Builder(this);
			abAbout.setItems(null, null);
			dialogAbout = abAbout.create();
			dialog = dialogAbout;
			break;
		}
		return dialog;
	}

	// ÿ�Ώ���Dialog��Ԓ���K�r����Ԓ���K�ă���
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DIALOG_SET_SEARCH_RANGE:// �O������������Ԓ���K
			dialog.setContentView(R.layout.dialogsetrange);
			// year month day������1�ı�ʾ�P���ʼ�r�g�O�ã�2��ʾ�P춽Kֹ�r�g�O�ã�P��ʾplus��̖��M��ʾminus��̖
			final ImageButton bYear1P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeYear1P);
			final ImageButton bYear1M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeYear1M);
			final ImageButton bMonth1P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeMonth1P);
			final ImageButton bMonth1M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeMonth1M);
			final ImageButton bDay1P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeDay1P);
			final ImageButton bDay1M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeDay1M);
			final EditText etYear1 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeYear1);
			final EditText etMonth1 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeMonth1);
			final EditText etDay1 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeDay1);

			final ImageButton bYear2P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeYear2P);
			final ImageButton bYear2M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeYear2M);
			final ImageButton bMonth2P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeMonth2P);
			final ImageButton bMonth2M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeMonth2M);
			final ImageButton bDay2P = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeDay2P);
			final ImageButton bDay2M = (ImageButton) dialog
					.findViewById(R.id.bdialogsetrangeDay2M);
			final EditText etYear2 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeYear2);
			final EditText etMonth2 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeMonth2);
			final EditText etDay2 = (EditText) dialog
					.findViewById(R.id.etdialogsetrangeDay2);

			Button bSetRangeOk = (Button) dialog
					.findViewById(R.id.bdialogsetrangeOk);
			Button bSetRangeCancel = (Button) dialog
					.findViewById(R.id.bdialogsetrangeCancel);

			// ��YYYY/MM/DD��ʽ�������շ��x����,�K����@ʾ�����յ�TextView��
			String[] from = splitYMD(rangeFrom);
			String[] to = splitYMD(rangeTo);

			etYear1.setText(from[0]);
			etMonth1.setText(from[1]);
			etDay1.setText(from[2]);
			etYear2.setText(to[0]);
			etMonth2.setText(to[1]);
			etDay2.setText(to[2]);

			bYear1P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear1.getText().toString()
							.trim());
					year++;
					etYear1.setText("" + year);
				}
			});
			bYear1M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear1.getText().toString()
							.trim());
					year--;
					etYear1.setText("" + year);
				}
			});
			bMonth1P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int month = Integer.parseInt(etMonth1.getText().toString()
							.trim());
					if (++month > 12) {
						month = 1;
					}
					etMonth1.setText(month < 10 ? "0" + month : "" + month);
				}
			});
			bMonth1M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int month = Integer.parseInt(etMonth1.getText().toString()
							.trim());
					if (--month < 1) {
						month = 12;
					}
					etMonth1.setText(month < 10 ? "0" + month : "" + month);
				}
			});

			bDay1P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear1.getText().toString()
							.trim());
					int month = Integer.parseInt(etMonth1.getText().toString()
							.trim());
					int day = Integer.parseInt(etDay1.getText().toString()
							.trim());
					if (++day > getMaxDayOfMonth(year, month)) {
						day = 1;
					}
					etDay1.setText(day < 10 ? "0" + day : "" + day);
				}
			});
			bDay1M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear1.getText().toString()
							.trim());
					int month = Integer.parseInt(etMonth1.getText().toString()
							.trim());
					int day = Integer.parseInt(etDay1.getText().toString()
							.trim());
					if (--day < 1) {
						day = getMaxDayOfMonth(year, month);
					}
					etDay1.setText(day < 10 ? "0" + day : "" + day);
				}
			});
			// ================�ָ�����Ϟ��O����ʼ�r�g�İ��o�O ��һ���O�ýKֹ�r�g�İ��o�O ==================
			bYear2P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear2.getText().toString()
							.trim());
					year++;
					etYear2.setText("" + year);
				}
			});
			bYear2M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear2.getText().toString()
							.trim());
					year--;
					etYear2.setText("" + year);
				}
			});
			bMonth2P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int month = Integer.parseInt(etMonth2.getText().toString()
							.trim());
					if (++month > 12) {
						month = 1;
					}
					etMonth2.setText(month < 10 ? "0" + month : "" + month);
				}
			});
			bMonth2M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int month = Integer.parseInt(etMonth2.getText().toString()
							.trim());
					if (--month < 1) {
						month = 12;
					}
					etMonth2.setText(month < 10 ? "0" + month : "" + month);
				}
			});

			bDay2P.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear2.getText().toString()
							.trim());
					int month = Integer.parseInt(etMonth2.getText().toString()
							.trim());
					int day = Integer.parseInt(etDay2.getText().toString()
							.trim());
					if (++day > getMaxDayOfMonth(year, month)) {
						day = 1;
					}
					etDay2.setText(day < 10 ? "0" + day : "" + day);
				}
			});
			bDay2M.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year = Integer.parseInt(etYear2.getText().toString()
							.trim());
					int month = Integer.parseInt(etMonth2.getText().toString()
							.trim());
					int day = Integer.parseInt(etDay2.getText().toString()
							.trim());
					if (--day < 1) {
						day = getMaxDayOfMonth(year, month);
					}
					etDay2.setText(day < 10 ? "0" + day : "" + day);
				}
			});

			bSetRangeOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int year1 = Integer.parseInt(etYear1.getText().toString()
							.trim());
					int month1 = Integer.parseInt(etMonth1.getText().toString()
							.trim());
					int day1 = Integer.parseInt(etDay1.getText().toString()
							.trim());
					int year2 = Integer.parseInt(etYear2.getText().toString()
							.trim());
					int month2 = Integer.parseInt(etMonth2.getText().toString()
							.trim());
					int day2 = Integer.parseInt(etDay2.getText().toString()
							.trim());

					if (day1 > getMaxDayOfMonth(year1, month1)
							|| day2 > getMaxDayOfMonth(year2, month2)) {
						Toast.makeText(MySchedule.this, "�����O���e�`",
								Toast.LENGTH_SHORT).show();
						return;
					}
					rangeFrom = Schedule.toDateString(year1, month1, day1);
					rangeTo = Schedule.toDateString(year2, month2, day2);
					if (rangeFrom.compareTo(rangeTo) > 0) {
						Toast.makeText(MySchedule.this, "��ʼ�����ܴ����ֹ����",
								Toast.LENGTH_SHORT).show();
						return;
					}
					dialogSetRange.cancel();
					gotoSearch();
				}
			});

			// �cȡ���t��Ԓ���K�P�]
			bSetRangeCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogSetRange.cancel();
				}
			});

			break;

		case DIALOG_SET_DATETIME:// �O�Õr�g���ڌ�Ԓ���K
			TimePickerDialog timePickerDialog = new TimePickerDialog(this,
					new OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							// TODO Auto-generated method stub
							Toast.makeText(MySchedule.this,
									hourOfDay + ":" + minute, 1000).show();

							if (minute < 10) {
								min = "0" + minute;
							} else {
								min = minute + "";
							}
							if (hourOfDay < 10) {
								hour = "0" + hourOfDay;
							} else {
								hour = hourOfDay + "";
							}

							tvTime.setText(hour + ":" + min);
						}
					}, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
					Calendar.getInstance().get(Calendar.MINUTE), true);

			timePickerDialog.show();

			break;
		case DIALOG_SCH_DEL_CONFIRM:// �h���г̌�Ԓ���K
			dialog.setContentView(R.layout.dialogschdelconfirm);
			Button bDelOk = (Button) dialog
					.findViewById(R.id.bdialogschdelconfirmOk);
			Button bDelCancel = (Button) dialog
					.findViewById(R.id.bdialogschdelconfirmCancel);

			bDelOk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteSchedule(MySchedule.this);
					gotoMain();
					dialogSchDelConfirm.cancel();
				}
			});

			bDelCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogSchDelConfirm.cancel();
				}
			});
			break;

		case DIALOG_CHECK:// �鿴�г̌�Ԓ���K
			dialog.setContentView(R.layout.dialogcheck);
			TextView tvType = (TextView) dialog
					.findViewById(R.id.tvdialogcheckType);// �@ʾ��͵�TextView
			TextView tvTitle = (TextView) dialog
					.findViewById(R.id.tvdialogcheckTitle);// �@ʾ���}��TextView
			TextView tvNote = (TextView) dialog
					.findViewById(R.id.tvdialogcheckNote);// �@ʾ���]��TextView
			TextView tvDatetime1 = (TextView) dialog
					.findViewById(R.id.tvdialogcheckDate1);// �@ʾ�г����ں͕r�g��TextView
			// TextView
			// tvDatetime2=(TextView)dialog.findViewById(R.id.tvdialogcheckDate2);//�@ʾ�[����ں͕r�g��TextView
			Button bEdit = (Button) dialog.findViewById(R.id.bdialogcheckEdit);// ��݋���o
			Button bDel = (Button) dialog.findViewById(R.id.bdialogcheckDel);// �h�����o
			Button bBack = (Button) dialog.findViewById(R.id.bdialogcheckBack);// ���ذ��o
			littleImageView = (ImageView) dialog.findViewById(R.id.imageView1);

			tvType.setText(schTemp.typeForListView());
			tvTitle.setText(schTemp.getTitle());
			tvNote.setText(schTemp.getNote());

			// ������]��գ��@ʾ�o���]
			if (schTemp.getNote().equals("")) {
				tvNote.setText("(�o���])");
			}
			String time1 = schTemp.getTime1();

			// ������w�r�g��գ��r�g�@ʾ��--:--
			if (time1.equals("null")) {
				time1 = "- -:- -";
			}
			tvDatetime1.setText(schTemp.getDate1() + "  " + time1);

			String date2 = schTemp.getDate2();
			if (littleImage == 0) {
				littleImageView.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.ttt));
			} else {
				littleImageView.setBackgroundDrawable(getResources()
						.getDrawable(schTemp.getLittleImage()));
			}

			bEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogCheck.cancel();
					gotoSetting();
				}
			});

			bDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogCheck.cancel();
					showDialog(DIALOG_SCH_DEL_CONFIRM);
				}
			});

			bBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogCheck.cancel();
				}
			});
			break;
		case DIALOG_ALL_DEL_CONFIRM:// �h�������^���г̌�Ԓ���K

			break;
		}
	}

	// onKeyDown����
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// �����֙C���ذ��o�r
		if (keyCode == 4) {
			switch (curr) {
			case MAIN:// ���������Ԓ�˳���ʽ
				System.exit(0);
				break;
			case SETTING:// ���г̾�݋�����Ԓ����������
				gotoMain();
				break;
			case TYPE_MANAGER:// //����͹�������Ԓ�����г̾�݋����
				gotoSetting();
				break;
			case SEARCH:// ���г̲��ҽ����Ԓ����������
				gotoMain();
				break;
			case SEARCH_RESULT:// ���г̲��ҽY�������Ԓ�����г̲��ҽ���
				gotoSearch();
				break;
			case HELP:// ���h�������Ԓ����������
				gotoMain();
				break;
			case ABOUT:
				gotoMain();
				break;
			}
			return true;
		}
		return false;
	}

	// ����Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	// �Á�õ�year��month�µ�����씵
	public int getMaxDayOfMonth(int year, int month) {
		int day = 0;
		boolean run = false;
		if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) {
			run = true;
		}
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		} else if (month == 2) {
			if (run) {
				day = 29;
			} else {
				day = 28;
			}
		} else {
			day = 31;
		}
		return day;
	}

	// ���ذ�YYYY/MM/DD�ָ�����������ִ����
	public String[] splitYMD(String ss) {
		String[] s = ss.split("/");
		return s;
	}

	public void showTodayList() {
		listView = (ListView) findViewById(R.id.listView1);

		Toast.makeText(this, "������" + cursor2.getCount() + "�l�г�",
				Toast.LENGTH_SHORT).show();
		if (cursor2.getCount() == 0) {
			hasListTextView.setText("���՛]���κ��г�!");
			return;
		}

		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.listview_schedule, cursor2, new
		// String[]{"littleImage","type","title","note","date1","time1"}, new
		// int[]{R.id.imageView1,R.id.tvdialogcheckType,R.id.tvdialogcheckTitle,R.id.tvdialogcheckNote,R.id.tvdialogcheckDate1,R.id.tvdialogcheckTime1});
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.listview_schedule, cursor2, new String[] {
						"littleImage", "type", "title", "note", "date1",
						"time1" }, new int[] { R.id.imageView1,
						R.id.tvdialogcheckType, R.id.tvdialogcheckTitle,
						R.id.tvdialogcheckNote, R.id.tvdialogcheckDate1,
						R.id.tvdialogcheckTime1 });
		// new SimpleCurs

		v = new View(this);
		
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//listView.requestFocusFromTouch();
				//listView.setSelection(arg2);
				// TODO Auto-generated method stub
				/*
		        for(int i=0;i<arg0.getCount();i++){
		            View v=arg0.getChildAt(i);
		            if (arg2 == i) {
		                v.setBackgroundColor(Color.RED);
		            } else {
		                v.setBackgroundColor(0);
		            }
		        }*/
				
	            if (((ListView)arg0).getTag() != null){

                    ((View)((ListView)arg0).getTag()).setBackgroundColor(0);

            }

            ((ListView)arg0).setTag(arg1);

            arg1.setBackgroundColor(Color.GREEN);
            
            hightlight = arg2;
            v = arg1;
		
				bEdit.setEnabled(true);// �A�O�O�鲻���à�B
				bDel.setEnabled(true);

				schTemp = alSch.get(arg2);
				littleImage = schTemp.getLittleImage();
			}
		});
		
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//if((firstVisibleItem>hightlight) || (firstVisibleItem+visibleItemCount <hightlight)){
				if((firstVisibleItem-1==hightlight)||(firstVisibleItem+visibleItemCount==hightlight)){	
				v.setBackgroundColor(0);}
			
			}
		});
		
		
	}

	private class myResetReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {

			littleImageView.setBackgroundDrawable(getResources().getDrawable(
					littleImage));
		}
	}
}
