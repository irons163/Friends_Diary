package tw.com.irons.try_case2;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Mrt extends Activity {
	// private WebView OlaWebView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		WebView OlaWebView;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mrtmap);

		String uri = "http://web.trtc.com.tw/img/all/routemapDongmen.jpg";

		OlaWebView = (WebView) findViewById(R.id.webView1);
		OlaWebView.loadUrl(uri);
	}
}