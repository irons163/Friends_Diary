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

		tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("�q��")
				.setContent(new Intent(this, Notice.class)));

		tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("�I����")
				.setContent(new Intent(this, BackgroundChange.class)));
		/*
		 * tabHost.addTab(tabHost.newTabSpec("tab3") .setIndicator("���")
		 * .setContent(new Intent(this, ClientMsg.class)));
		 */
	}
}
