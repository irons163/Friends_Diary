package tw.com.irons.try_case2.utils;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private Integer width;
	private Integer height;
	private Integer[] mImageIds;
	private Resources res;

	public ImageAdapter(Context mContext, Integer width, Integer height,
			Integer[] mImageIds) {
		this.mContext = mContext;
		this.mImageIds = mImageIds;
		this.width = width;
		this.height = height;

	}

	public ImageAdapter(Context mContext, Integer width, Integer height,
			Integer[] mImageIds, Resources res) {
		this.mContext = mContext;
		this.mImageIds = mImageIds;
		this.width = width;
		this.height = height;

		this.res = res;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			// if it's not recycled, initialize some attributes
			imageView = new ImageView(mContext);
			// 設定圖片的寬、高
			imageView.setLayoutParams(new GridView.LayoutParams(width, height));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		Bitmap bitmap = null;
		if (res != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = false;
			options.inSampleSize = 50;
			// bitmap = BitmapFactory.decodeResource(res, mImageIds[position],
			// options);
			InputStream is = res.openRawResource(mImageIds[position]);
			bitmap = BitmapFactory.decodeResourceStream(res, null, is, null,
					options);
			Drawable drawable = new BitmapDrawable(bitmap);
			imageView.setImageBitmap(bitmap);
			Log.e("gg", "1");

		} else {
			// 設定圖片來源
			imageView.setImageResource(mImageIds[position]);
			Log.e("gg", "2");
		}
		// bitmap.recycle();
		// imageView.setBackgroundColor(Color.BLACK);
		return imageView;

	}

	@Override
	public int getCount() {
		return mImageIds.length;
	}

	@Override
	public Object getItem(int position) {
		return mImageIds[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
