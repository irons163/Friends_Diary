package com.way.util;

import java.util.List;

import com.way.chat.common.bean.User;

/**
 * 自訂的GroupFriend物件，用來封裝大組名稱和分配對應的資料
 * 
 * @author way
 * 
 */
public class GroupFriend {
	private String groupName;// 大組名稱
	private List<User> groupChild;// 對應大組的小組成員物件陣列

	public GroupFriend() {
		super();
	}

	public GroupFriend(String groupName, List<User> groupChild) {
		super();
		this.groupName = groupName;
		this.groupChild = groupChild;
	}

	public void add(User u) {// 往小組中添加用戶
		groupChild.add(u);
	}

	public void remove(User u) {// 根據使用者物件移除使用者
		groupChild.remove(u);
	}

	public void remove(int index) {// 根據下標移除使用者
		groupChild.remove(index);
	}

	public int getChildSize() {// 小組的大小
		return groupChild.size();
	}

	public User getChild(int index) {// 根據下標得到使用者
		return groupChild.get(index);
	}

	// get...set...
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<User> getGroupChild() {
		return groupChild;
	}

	public void setGroupChild(List<User> groupChild) {
		this.groupChild = groupChild;
	}

}
