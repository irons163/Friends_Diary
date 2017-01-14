package com.way.chat.activity;

import java.util.List;

import tw.com.irons.try_case2.R;
import tw.com.irons.try_case2.Constant.WhoCall;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.way.chat.common.bean.TextMessage;
import com.way.chat.common.bean.User;
import com.way.chat.common.tran.bean.TranObject;
import com.way.chat.common.tran.bean.TranObjectType;
import com.way.chat.common.util.Constants;
import com.way.client.Client;
import com.way.client.ClientOutputThread;
import com.way.util.GroupFriend;
import com.way.util.MessageDB;
import com.way.util.MyDate;
import com.way.util.SharePreferenceUtil;

/**
 * 自定义ExpandableListView的适配器
 * 
 * @author way
 * 
 */
public class MyExAdapter extends BaseAdapter {
	private SharePreferenceUtil util;
	private User user;
	private MessageDB messageDB;
	private MyApplication application;
	
	private int[] imgs = { R.drawable.icon, R.drawable.f1, R.drawable.f2,
			R.drawable.f3, R.drawable.f4, R.drawable.f5, R.drawable.f6,
			R.drawable.f7, R.drawable.f8, R.drawable.f9 };// 头像资源数组
	private Context context;
	private List<GroupFriend> group;// 传递过来的经过处理的总数据

	private Intent intent;
	
	public MyExAdapter(Context context, List<GroupFriend> group) {
		super();
		this.context = context;
		this.group = group;
	}

	public MyExAdapter(Context context, List<GroupFriend> group, Intent intent) {
		super();
		this.context = context;
		this.group = group;
		this.intent = intent;
	}




	// 得到大组成员总数
	public int getGroupCount() {
		return group.size();
	}


	// 得到小组成员id
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	// 得到小组成员的名称
	public Object getChild(int groupPosition, int childPosition) {
		return group.get(0).getChild(childPosition);
	}

	// 得到小组成员的数量
	public int getChildrenCount(int groupPosition) {
		return group.get(0).getChildSize();
	}

	/**
	 * Indicates whether the child and group IDs are stable across changes to
	 * the underlying data. 表明大組和小组id是否稳定的更改底层数据。
	 * 
	 * @return whether or not the same ID always refers to the same object
	 * @see Adapter#hasStableIds()
	 */
	public boolean hasStableIds() {
		return true;
	}

	// 得到小组成员是否被选择
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * 这个方法是我自定义的，用于下拉刷新好友的方法
	 * 
	 * @param group
	 *            传递进来的新数据
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
					.findViewById(R.id.name_item);// 显示用户名
			final TextView title2 = (TextView) convertView
					.findViewById(R.id.id_item);// 显示用户id
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.imageView_item);// 显示用户头像，其实还可以判断是否在线，选择黑白和彩色头像，我这里未处理，没资源，呵呵

			final String name = group.get(0).getChild(childPosition)
					.getName();
			final String id = group.get(0).getChild(childPosition)
					.getId()
					+ "";
			final int img = group.get(0).getChild(childPosition)
					.getImg();
			title.setText(name);// 大标题
			title2.setText(id);// 小标题
			
			if(group.get(0).getChild(childPosition).getIsOnline()==1){
			icon.setImageResource(imgs[img]);
			}else{
				icon.setImageResource(R.drawable.ic_launcher);
			}
			
			
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 下面是切换到聊天界面处理
					User u = new User();
					u.setName(name);
					u.setId(Integer.parseInt(id));
					u.setImg(img);
					
					
					if(Constants.WhoCall.equals("SEND")){
						//intent = new Intent(context, SendOnly.class);
						intent.putExtra("user", u);
						context.startActivity(intent);
			}else{
				Intent intent = new Intent(context, ChatActivity.class);
				intent.putExtra("user", u);
				context.startActivity(intent);
			}

					
					// Toast.makeText(Tab2.this, "开始聊天", 0).show();

				}
			});
			return convertView;

	}

}
