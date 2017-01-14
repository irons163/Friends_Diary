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
	public String[] defultType = new String[] { "會議", "備忘", "待辦" };// 軟體的三個不能刪除的默認類型
	Dialog dialogSetRange;// 行程查找時設置日期起始範圍的對話方塊
	Dialog dialogSetDatetime;// 新建或修改行程時設置日期和時間的對話方塊
	Dialog dialogSchDelConfirm;// 刪除行程時的確認對話方塊
	Dialog dialogCheck;// 主介面中查看行程詳細內容的對話方塊
	Dialog dialogAllDelConfirm;// 刪除全部過期行程時的確認對話方塊
	Dialog dialogAbout;// 關於對話方塊
	public static ArrayList<String> alType = new ArrayList<String>();// 存儲所有行程類型的arraylist
	public static ArrayList<Schedule> alSch = new ArrayList<Schedule>();// 存儲所有schedule物件的ArrayList
	public Schedule schTemp;// 臨時的schedule
	public ArrayList<Boolean> alSelectedType = new ArrayList<Boolean>();// 記錄查找介面中類型前面checkbox狀態的
	public String rangeFrom = getNowDateString();// 查找行程時設置的起始日期，默認當前日期
	public String rangeTo = rangeFrom;// 查找行程時設置的終止日期，默認當前日期
	Layout curr = null;// 記錄當前介面的枚舉類型
	WhoCall wcSetTimeOrAlarm;// 用來判斷調用時間日期對話方塊的按鈕是設置時間還是設置鬧鐘,以便更改對話方塊中的一些控制項該設置為visible還是gone
	WhoCall wcNewOrEdit;// 用來判斷調用行程編輯介面的是新建行程按鈕還是在修改行程按鈕，以便設置對應的介面標題
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
	 * 臨時記錄新建行程介面裡的類型spinner的position，因為設置時間的對話方塊cancel後
	 * 回到新建行程介面時會刷新所有控制項，spinner中以選中的項目也會回到默認
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 無標題
		gotoMain();

		IntentFilter filter = new IntentFilter("tw.com.irons.try_case2_2");
		receiver = new myResetReceiver();
		registerReceiver(receiver, filter);

	}

	// ===================================主介面start===========================================
	public void gotoMain()// 初始化主介面
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

		final ArrayList<Boolean> alIsSelected = new ArrayList<Boolean>();// 記錄ListView中哪項選中了的標誌位元

		bEdit = (ImageButton) findViewById(R.id.ibmainEdit);// 修改行程按鈕
		bDel = (ImageButton) findViewById(R.id.ibmainDel);// 刪除當前選中行程的按鈕
		ImageButton bNew = (ImageButton) findViewById(R.id.ibmainNew);// 新建行程按鈕
		ImageButton bSearch = (ImageButton) findViewById(R.id.ibmainSearch);// 查找行程按鈕
		// final ListView lv=(ListView)findViewById(R.id.lvmainSchedule);//行程列表

		bEdit.setEnabled(false);// 預設設為不可用狀態
		bDel.setEnabled(false);

		alSch.clear();// 從資料庫讀取之前清空存儲行程的arraylist
		loadSchedule(this);// 從資料庫中讀取行程
		loadType(this);// 從資料庫中讀取類型

		alIsSelected.clear();

		for (int i = 0; i < alSch.size(); i++)// 全部設置為false，即沒有一項選中
		{
			alIsSelected.add(false);
		}

		final ArrayList<String> type = getAllType(MySchedule.this);// 獲取已存行程中的所有類型和用戶自建的所有類型

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
			Toast.makeText(MySchedule.this, "日期設置錯誤", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		rangeFrom = Schedule.toDateString(year1, month1, day1);
		rangeTo = Schedule.toDateString(year2, month2, day2);

		alSelectedType.clear();

		for (int i = 0; i < type.size(); i++)// 選中“全部選中”後把listview裡的所有類型後面的checkbox設成選中狀態
		{
			alSelectedType.add(i, true);
		}

		cursor2 = searchSchedule(MySchedule.this, type);// 根據日期與類別搜索

		alSelectedType.clear();

		TextView tvType = (TextView) findViewById(R.id.tvdialogcheckType);// 顯示類型的TextView
		TextView tvTitle = (TextView) findViewById(R.id.tvdialogcheckTitle);// 顯示標題的TextView
		TextView tvNote = (TextView) findViewById(R.id.tvdialogcheckNote);// 顯示備註的TextView
		TextView tvDatetime1 = (TextView) findViewById(R.id.tvdialogcheckDate1);// 顯示行程日期和時間的TextView
		// TextView
		// tvDatetime2=(TextView)findViewById(R.id.tvdialogcheckDate2);//顯示鬧鐘日期和時間的TextView
		hasListTextView = (TextView) findViewById(R.id.hasListTextView);
		Button btnEdit = (Button) findViewById(R.id.bdialogcheckEdit);// 編輯按鈕
		Button btnDel = (Button) findViewById(R.id.bdialogcheckDel);// 刪除按鈕
		Button bBack = (Button) findViewById(R.id.bdialogcheckBack);// 返回按鈕

		showTodayList();

		// bNew設置
		bNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				// String[] clickDate = new String[3];
				// clickDate=MyApplication.clickDate.split("-");

				int year1 = Integer.parseInt(clickDate[0]);
				int month1 = Integer.parseInt(clickDate[1]);
				int day1 = Integer.parseInt(clickDate[2]);

				schTemp = new Schedule(year1, month1, day1);// 臨時新建一個行程物件，年月日設為當前日期
				schTemp.setLittleImage(R.drawable.ttt);
				littleImage = R.drawable.ttt;

				wcNewOrEdit = WhoCall.NEW;// 調用行程編輯介面的是新建按鈕
				gotoSetting();// 去行程編輯介面
			}
		});
		// bEdit設置
		bEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wcNewOrEdit = WhoCall.EDIT;// 調用行程編輯介面的是修改按鈕
				gotoSetting();// 去行程編輯介面
			}
		});

		// 刪除選中的行程按鈕
		bDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SCH_DEL_CONFIRM);
			}
		});

		// 行程查找按鈕
		bSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSearch();
			}
		});

	}

	// ===================================行程編輯介面start=====================================
	public void gotoSetting()// 初始化新建行程介面
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
			tvTitle.setText("新建行程");
		} else if (wcNewOrEdit == WhoCall.EDIT) {
			tvTitle.setText("修改行程");
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
		tvTime.setText(schTemp.getTimeSet() ? schTemp.getTime1() : "無具體時間");

		// 類型spinner設置
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

		// 新建行程類型按鈕
		bNewType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());// 將已經輸入的title和note存入schTemp，以防返回時被清空
				schTemp.setNote(etNote.getText().toString());
				sel = spType.getSelectedItemPosition();// 存儲spType的當前選擇
				gotoTypeManager();// 進入行程類型管理介面
			}
		});

		bSetDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				schTemp.setTitle(etTitle.getText().toString());// 將已經輸入的主題和備註存入schTemp，以防設置完時間或鬧鐘返回時被清空
				schTemp.setNote(etNote.getText().toString());
				sel = spType.getSelectedItemPosition();
				wcSetTimeOrAlarm = WhoCall.SETTING_DATE;// 調用設置日期時間對話方塊的是設置行程日期按鈕
				showDialog(DIALOG_SET_DATETIME);
			}
		});

		// 完成按鈕設置
		bDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if((hour!=null)   && (min!=null)){
				schTemp.setTime1(hour, min);
				}
				// 讓新建的行程時間和當前時間比較看是否過期
				if (schTemp.isPassed()) {
					Toast.makeText(MySchedule.this, "不能創建過期行程",
							Toast.LENGTH_SHORT).show();
					return;
				}

				String title = etTitle.getText().toString().trim();
				if (title.equals(""))// 如果行程標題沒有輸入，默認為未命名
				{
					title = "未命名";
				}
				schTemp.setTitle(title);
				String note = etNote.getText().toString();
				schTemp.setNote(note);
				String type = (String) spType.getSelectedItem();
				schTemp.setType(type);

				schTemp.setLittleImage(littleImage);

				if (wcNewOrEdit == WhoCall.NEW)// 如果當前介面是新建行程，調用插入行程方法
				{
					insertSchedule(MySchedule.this);
				} else if (wcNewOrEdit == WhoCall.EDIT)// 如果當前介面是修改行程，調用更新行程方法
				{
					updateSchedule(MySchedule.this);
				}

				gotoMain();
			}

		});
		// 取消按鈕設置
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
						mImageIds)); // 設定 image 介面
				gridView01.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						final PopupWindow popWin = new PopupWindow(
								MySchedule.this);
						// 定義 PopupWindow 內含的 ImageView
						ImageView iv = new ImageView(MySchedule.this);
						// 在 ImageView 中設定圖樣
						iv.setImageDrawable(getResources().getDrawable(
								mImageIds[position]));
						// 將 popup window 內容設定成 ImageView
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
				builder.setTitle("分享資訊");
				builder.setView(dview);
				builder.setCancelable(true);

				builder.setPositiveButton("確定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										"tw.com.irons.try_case2_2");
								sendBroadcast(intent);
							}

						});

				builder.setNegativeButton("取消",
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

	// ===================================類型管理介面start=====================================
	public void gotoTypeManager() {
		setContentView(R.layout.typemanager);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.TYPE_MANAGER;
		final ListView lvType = (ListView) findViewById(R.id.lvtypemanagerType);// 列表列出所有已有類型
		final EditText etNew = (EditText) findViewById(R.id.ettypemanagerNewType);// 輸入新類型名稱的TextView
		final Button bNew = (Button) findViewById(R.id.btypemanagerNewType);// 新建類型按鈕
		final Button bBack = (Button) findViewById(R.id.btypemanagerBack);// 返回上一頁按鈕

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

				// 軟體自帶的類型不能刪除，其他自建類型後面添加一個紅叉用來刪除自建類型
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
					Toast.makeText(MySchedule.this, "類型名稱不能為空。",
							Toast.LENGTH_SHORT).show();
					return;
				}
				insertType(MySchedule.this, newType);
				gotoTypeManager();
			}
		});
	}

	// ===================================查找介面start=========================================
	public void gotoSearch() {
		setContentView(R.layout.search);

		SharedPreferences preferences = getSharedPreferences("clickDate", 0);
		int bg = preferences.getInt("background", 0);
		if (bg != 0) {
			View layout = (View) findViewById(R.id.layout1);
			layout.setBackgroundDrawable(getResources().getDrawable(bg));
		}

		curr = Layout.SEARCH;
		final Button bChange = (Button) findViewById(R.id.bsearchChange);// 改變查找範圍按鈕
		final Button bSearch = (Button) findViewById(R.id.bsearchGo);// 開始查找
		final Button bCancel = (Button) findViewById(R.id.bsearchCancel);// 取消
		final CheckBox cbDateRange = (CheckBox) findViewById(R.id.cbsearchDateRange);// 查找是否限制範圍的CheckBox
		final CheckBox cbAllType = (CheckBox) findViewById(R.id.cbsearchType);// 是否在在所有類型中查找的CheckBox
		final ListView lv = (ListView) findViewById(R.id.lvSearchType);// 所有類型列在lv中
		final TextView tvFrom = (TextView) findViewById(R.id.tvsearchFrom);// 查找起始時期的tv
		final TextView tvTo = (TextView) findViewById(R.id.tvsearchTo);// //查找終止時期的tv

		tvFrom.setText(rangeFrom);
		tvTo.setText(rangeTo);

		final ArrayList<String> type = getAllType(MySchedule.this);// 獲取已存行程中的所有類型和用戶自建的所有類型

		alSelectedType.clear();
		for (int i = 0; i < type.size(); i++)// 預設為所有類型設置狀態位元false
		{
			alSelectedType.add(false);
		}

		cbDateRange.setOnCheckedChangeListener(new OnCheckedChangeListener() {// 根據是否限制日期範圍的CheckBox決定更改日期範圍的按鈕是否可用
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						bChange.setEnabled(isChecked);
					}
				});

		// 設置“在全部類型中搜索”的CheckBox改變狀態時的行為
		cbAllType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				for (int i = 0; i < type.size(); i++)// 選中“全部選中”後把listview裡的所有類型後面的checkbox設成選中狀態
				{
					alSelectedType.set(i, isChecked);
				}
				lv.invalidateViews();// 刷新ListView??
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
				cb.setChecked(alSelectedType.get(position));// 按ArrayList裡面存儲的狀態設置CheckBox狀態

				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						alSelectedType.set(position, isChecked);// 改變ArrayList裡面對應位置boolean值
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
				// 如果沒有一個類型被選中則提示
				boolean tmp = false;
				for (boolean b : alSelectedType) {
					tmp = tmp | b;
				}
				if (tmp == false) {
					Toast.makeText(MySchedule.this, "請至少選中一個類型",
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

	// ===================================查找結果介面start=====================================
	public void gotoSearchResult()// 該介面和主介面除了少了幾個按鈕其他完全一樣
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

		// 以下是查找結果的ListView設置
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

				if (alSch.get(position).isPassed())// 若行程已過期，則日期和時間顏色、背景色變灰
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
				schTemp = alSch.get(arg2);// 選中某個專案時，把該專案對象賦給schTemp
			}
		});

		// 修改行程按鈕設置
		bEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				wcNewOrEdit = WhoCall.EDIT;
				gotoSetting();
			}
		});
		// 刪除選中行程按鈕設置
		bDel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SCH_DEL_CONFIRM);
			}
		});

		// 查找行程按鈕設置
		bBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoSearch();
			}

		});

		// 查看行程按鈕設置
		bCheck.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(DIALOG_CHECK);
			}
		});
	}

	// 創建對話方塊
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

	// 每次彈出Dialog對話方塊時更新對話方塊的內容
	@Override
	public void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DIALOG_SET_SEARCH_RANGE:// 設置搜索範圍對話方塊
			dialog.setContentView(R.layout.dialogsetrange);
			// year month day後面是1的表示關於起始時間設置，2表示關於終止時間設置，P表示plus加號，M表示minus建號
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

			// 把YYYY/MM/DD格式的年月日分離出來,並且填到顯示年月日的TextView中
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
			// ================分割線，以上為設置起始時間的按鈕監聽，一下為設置終止時間的按鈕監聽==================
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
						Toast.makeText(MySchedule.this, "日期設置錯誤",
								Toast.LENGTH_SHORT).show();
						return;
					}
					rangeFrom = Schedule.toDateString(year1, month1, day1);
					rangeTo = Schedule.toDateString(year2, month2, day2);
					if (rangeFrom.compareTo(rangeTo) > 0) {
						Toast.makeText(MySchedule.this, "起始日起不能大於中止日期",
								Toast.LENGTH_SHORT).show();
						return;
					}
					dialogSetRange.cancel();
					gotoSearch();
				}
			});

			// 點取消則對話方塊關閉
			bSetRangeCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogSetRange.cancel();
				}
			});

			break;

		case DIALOG_SET_DATETIME:// 設置時間日期對話方塊
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
		case DIALOG_SCH_DEL_CONFIRM:// 刪除行程對話方塊
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

		case DIALOG_CHECK:// 查看行程對話方塊
			dialog.setContentView(R.layout.dialogcheck);
			TextView tvType = (TextView) dialog
					.findViewById(R.id.tvdialogcheckType);// 顯示類型的TextView
			TextView tvTitle = (TextView) dialog
					.findViewById(R.id.tvdialogcheckTitle);// 顯示標題的TextView
			TextView tvNote = (TextView) dialog
					.findViewById(R.id.tvdialogcheckNote);// 顯示備註的TextView
			TextView tvDatetime1 = (TextView) dialog
					.findViewById(R.id.tvdialogcheckDate1);// 顯示行程日期和時間的TextView
			// TextView
			// tvDatetime2=(TextView)dialog.findViewById(R.id.tvdialogcheckDate2);//顯示鬧鐘日期和時間的TextView
			Button bEdit = (Button) dialog.findViewById(R.id.bdialogcheckEdit);// 編輯按鈕
			Button bDel = (Button) dialog.findViewById(R.id.bdialogcheckDel);// 刪除按鈕
			Button bBack = (Button) dialog.findViewById(R.id.bdialogcheckBack);// 返回按鈕
			littleImageView = (ImageView) dialog.findViewById(R.id.imageView1);

			tvType.setText(schTemp.typeForListView());
			tvTitle.setText(schTemp.getTitle());
			tvNote.setText(schTemp.getNote());

			// 如果備註為空，顯示無備註
			if (schTemp.getNote().equals("")) {
				tvNote.setText("(無備註)");
			}
			String time1 = schTemp.getTime1();

			// 如果具體時間為空，時間顯示成--:--
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
		case DIALOG_ALL_DEL_CONFIRM:// 刪除所有過期行程對話方塊

			break;
		}
	}

	// onKeyDown方法
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 按下手機返回按鈕時
		if (keyCode == 4) {
			switch (curr) {
			case MAIN:// 在主介面的話退出程式
				System.exit(0);
				break;
			case SETTING:// 在行程編輯介面的話返回主介面
				gotoMain();
				break;
			case TYPE_MANAGER:// //在類型管理介面的話返回行程編輯介面
				gotoSetting();
				break;
			case SEARCH:// 在行程查找介面的話返回主介面
				gotoMain();
				break;
			case SEARCH_RESULT:// 在行程查找結果介面的話返回行程查找介面
				gotoSearch();
				break;
			case HELP:// 在説明介面的話返回主介面
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

	// 創建Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return super.onMenuItemSelected(featureId, item);
	}

	// 用來得到year年month月的最大天數
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

	// 返回把YYYY/MM/DD分隔後的年月日字串陣列
	public String[] splitYMD(String ss) {
		String[] s = ss.split("/");
		return s;
	}

	public void showTodayList() {
		listView = (ListView) findViewById(R.id.listView1);

		Toast.makeText(this, "搜索到" + cursor2.getCount() + "條行程",
				Toast.LENGTH_SHORT).show();
		if (cursor2.getCount() == 0) {
			hasListTextView.setText("今日沒有任何行程!");
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
		
				bEdit.setEnabled(true);// 預設設為不可用狀態
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
