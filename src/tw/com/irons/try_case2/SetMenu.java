package tw.com.irons.try_case2;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class SetMenu extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		final TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("通知")
				.setContent(new Intent(this, Notice.class)));

		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("背景更換")
				.setContent(new Intent(this, BackgroundChange.class)));
		/*
		 * tabHost.addTab(tabHost.newTabSpec("tab3") .setIndicator("資料")
		 * .setContent(new Intent(this, ClientMsg.class)));
		 */
	}
}
