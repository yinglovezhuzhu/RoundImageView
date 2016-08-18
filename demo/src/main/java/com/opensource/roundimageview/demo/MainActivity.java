/*
 * Copyright (C) 2016. The Android Open Source Project.
 *
 *         yinglovezhuzhu@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opensource.roundimageview.demo;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;

import com.opensource.widget.NewRoundImageView;
import com.opensource.widget.RoundImageView;

public class MainActivity extends Activity {

	private RoundImageView mImg;
	
	private NewRoundImageView mNBImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImg = (RoundImageView) findViewById(R.id.rd_img);
		mNBImg = (NewRoundImageView) findViewById(R.id.nbrd_img);
	}

	public void changeBorder(View view) {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
//		img.setBorderInsideColor(Color.argb(255, r, g, b));
//		r = (int) (Math.random() * 255);
//		g = (int) (Math.random() * 255);
//		b = (int) (Math.random() * 255);
//		img.setBorderOutsideColor(Color.argb(255, r, g, b));
		mImg.setBorderColor(Color.argb(255, r, g, b));
		mNBImg.setBorderColor(Color.argb(255, r, g, b));
//		mNBImg.setBorderInsideColor(Color.argb(255, r, g, b));
//		r = (int) (Math.random() * 255);
//		g = (int) (Math.random() * 255);
//		b = (int) (Math.random() * 255);
//		mNBImg.setBorderOutsideColor(Color.argb(255, r, g, b));
	}
	
	int resId;

	public void changeRes(View view) {
		if(resId == R.drawable.head01) {
			resId = R.drawable.head;
		} else {
			resId = R.drawable.head01;
		}
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resId);
		mImg.setImageBitmap(bm);
		
		final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
                        new ColorDrawable(Color.TRANSPARENT),
                        new BitmapDrawable(getResources(), bm)
                });
		mNBImg.setImageDrawable(td);
        td.startTransition(300);
//		mNBImg.setImageBitmap(bm);
	}
}
