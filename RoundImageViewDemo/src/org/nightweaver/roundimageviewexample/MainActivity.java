package org.nightweaver.roundimageviewexample;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import com.opensource.roundimageview.demo.R;
import com.opensource.view.RoundImageView;

public class MainActivity extends Activity {

	RoundImageView img;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img = (RoundImageView) this.findViewById(R.id.rd_img);
	}

	public void changeBorder(View view) {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		img.setBorderInsideColor(Color.argb(255, r, g, b));
		r = (int) (Math.random() * 255);
		g = (int) (Math.random() * 255);
		b = (int) (Math.random() * 255);
		img.setBorderOutsideColor(Color.argb(255, r, g, b));
	}
	
	int resId;

	public void changeRes(View view) {
		if(resId == R.drawable.head01) {
			resId = R.drawable.head;
		} else {
			resId = R.drawable.head01;
		}
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		img.setImageBitmap(bm);
	}
}
