package com.way.chat.activity;

import java.util.List;

import tw.com.irons.try_case2.R;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.chat.common.bean.User;
import com.way.chat.common.util.Constants;
import com.way.util.DialogFactory;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.SharePreferenceUtil;

/**
 * 自訂ExpandableListView的適配器
 * 
 * @author way
 * 
 */
public class AddAdapter extends BaseAdapter {
	private SharePreferenceUtil util;
	private User user;
	private MessageDB messageDB;
	private MyApplication application;
	
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 頭像資源陣列
	private Context context;
	private List<GroupFriend> group;// 傳遞過來的經過處理的總數據

	private Intent intent;
	
	public AddAdapter(Context context, List<GroupFriend> group) {
		super();
		this.context = context;
		this.group = group;
	}

	public AddAdapter(Context context, List<GroupFriend> group, Intent intent) {
		super();
		this.context = context;
		this.group = group;
		this.intent = intent;
	}




	// 得到大組成員總數
	public int getGroupCount() {
		return group.size();
	}


	// 得到小組成員id
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// 得到小組成員的名稱
	public Object getChild(int groupPosition, int childPosition) {
		return group.get(0).getChild(childPosition);
	}

	// 得到小組成員的數量
	public int getChildrenCount(int groupPosition) {
		return group.get(0).getChildSize();
	}

	/**
	 * Indicates whether the child and group IDs are stable across changes to
	 * the underlying data. 表明大組和小組id是否穩定的更改底層資料。
	 * 
	 * @return whether or not the same ID always refers to the same object
	 * @see Adapter#hasStableIds()
	 */
	public boolean hasStableIds() {
		return true;
	}

	// 得到小組成員是否被選擇
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * 這個方法是我自訂的，用於下拉刷新好友的方法
	 * 
	 * @param group
	 *            傳遞進來的新資料
	 */
	public void updata(List<GroupFriend> group) {
		this.group = null;
		this.group = group;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		return getChildrenCount(0);
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int childPosition, View convertView, ViewGroup parent) {


			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.item, null);
				Log.e("cc", "11");
			}
			Log.e("dd", "11");
			final TextView title = (TextView) convertView
					.findViewById(R.id.name_item);// 顯示用戶名
			final TextView title2 = (TextView) convertView
					.findViewById(R.id.id_item);// 顯示使用者id
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.imageView_item);// 顯示使用者頭像，其實還可以判斷是否線上，選擇黑白和彩色頭像，我這裡未處理，沒資源，呵呵

			final Button button = (Button) convertView.findViewById(R.id.button1);
			
			TextView isOnLineTextView = (TextView) convertView
					.findViewById(R.id.isOnlinetextView);
			
			button.setVisibility(View.INVISIBLE);
			
			final String name = group.get(0).getChild(childPosition)
					.getName();
			final String id = group.get(0).getChild(childPosition)
					.getId()
					+ "";
			final int img = group.get(0).getChild(childPosition)
					.getImg();
			title.setText(name);// 大標題
			title2.setText(id);// 小標題
			
			if(group.get(0).getChild(childPosition).getIsOnline()==1){
			icon.setImageResource(imgs[img]);
			isOnLineTextView.setText("ONLINE");
			}else{
				icon.setImageResource(R.drawable.ic_launcher);
				isOnLineTextView.setText("OFFLINE");
			}
			
			/*
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
					DialogFactory.ToastDialog(context, "QQ登錄",
							"親！您的帳號或密碼錯誤哦");

					
					
					// Toast.makeText(Tab2.this, "開始聊天", 0).show();

				}
			});*/
			return convertView;

	}

}
