/*
 * Copyright (C) 2015 The Android Open Source Project.
 *
 *        yinglovezhuzhu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensource.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * This is a round corner ImageView, you can add the border for it.
 * 
 * @author yinglovezhuzhu@gmail.com
 * 
 * @version 1.0
 * 
 */
public class RoundImageView extends ImageView {

	private Context mContext;
	
	private final int DEFAULT_CORNER_RADIUS = 0;
	
	private final int DEFAULT_CORNER_RATE = 0;

	// 默认边框颜色，透明色
	private final int DEFAULT_COLOR = 0xFFFFFFFF;
	private final int DEFAUTL_BORDER_THICKNESS = 0;
	
	// 边框宽度
	private int mBorderThickness = 0;
	private int mBorderInsideThickness = 0;
	private int mBorderOutsideThickness = 0;

	// 边框颜色
	private int mBorderColor = 0;
	private int mBorderInsideColor = 0;
	private int mBorderOutsideColor = 0;

	// ImageView的尺寸
	private int mViewWidth;
	private int mViewHeight;
	
	private int mCornerRadius = 0;
	
	private int mCornerRate = 0;
	
	private final RectF mDrawRect = new RectF();
	private final RectF mTempRect = new RectF();
	
	private final PorterDuffXfermode mModeSrcIn = new PorterDuffXfermode(Mode.SRC_IN);
	private final PorterDuffXfermode mModeDstOver = new PorterDuffXfermode(Mode.DST_OVER);

	public RoundImageView(Context context) {
		super(context);
		this.mContext = context;
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setCustomAttributes(attrs);
	}

	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		this.setCustomAttributes(attrs);
	}

	/**
	 * 自定义属性
	 * @param attrs
	 */
	private void setCustomAttributes(AttributeSet attrs) {
		TypedArray typedArray = this.mContext.obtainStyledAttributes(attrs, R.styleable.roundimageview);
		
		mCornerRate = typedArray.getInt(R.styleable.roundimageview_corner_rate, DEFAULT_CORNER_RATE);
		if(mCornerRate <= DEFAULT_CORNER_RATE) {
			// xml中圆角比率优先圆角半径，当圆角比率没有设置或者无有效值时，圆角半径才有效
			mCornerRadius  = typedArray.getDimensionPixelSize(R.styleable.roundimageview_corner_radius, DEFAULT_CORNER_RADIUS);
			mCornerRate = DEFAULT_CORNER_RATE;
		}
		
		mBorderThickness = typedArray.getDimensionPixelSize(
				R.styleable.roundimageview_border_thickness, DEFAUTL_BORDER_THICKNESS);
		if(mBorderThickness <= DEFAUTL_BORDER_THICKNESS) {
			// xml中单框模式优先于双框的模式，单框模式没有设置或者没有有效值得时候，双框数值才会生效
			mBorderInsideThickness = typedArray.getDimensionPixelSize(
					R.styleable.roundimageview_border_inside_thickness, DEFAUTL_BORDER_THICKNESS);
			mBorderOutsideThickness = typedArray.getDimensionPixelSize(
					R.styleable.roundimageview_border_outside_thickness, DEFAUTL_BORDER_THICKNESS);
			mBorderThickness = DEFAUTL_BORDER_THICKNESS;
		}
		
		mBorderColor = typedArray.getColor(
				R.styleable.roundimageview_border_color, DEFAULT_COLOR);
		mBorderInsideColor = typedArray.getColor(
				R.styleable.roundimageview_border_inside_color,DEFAULT_COLOR);
		mBorderOutsideColor = typedArray.getColor
				(R.styleable.roundimageview_border_outside_color, DEFAULT_COLOR);
		
		// xml中单框模式优先于双框的模式
		if (mBorderInsideColor == DEFAULT_COLOR) {
			mBorderInsideColor = mBorderColor;
		}
		if (mBorderOutsideColor == DEFAULT_COLOR) {
			mBorderOutsideColor = mBorderColor;
		}
		typedArray.recycle();
	}
	
	/**
	 * 设置圆角半径，这个半径值会和圆角比率相冲突，只能有一个生效（最后设置的那个优先，xml中比率优先）<br>
	 * 另外需要注意的是，这个半径会在绘制过程中进行修正，最大的半径是1/2图形，也就是圆形
	 * 
	 * @param cornerRadius
	 * 
	 * @see {@link #setCornerRate(int)}} 设置圆角比率方法
	 */
	public void setCornerRadius(int cornerRadius) {
		this.mCornerRadius = cornerRadius;
		this.mCornerRate = DEFAULT_CORNER_RATE;
		invalidate();
 	}
	
	/**
	 * 设置圆角比率，这个比率值会和圆角半径相冲突，只能有一个生效（最后设置的那个优先，xml中比率优先）<br>
	 * <p>圆角比率说明：这个圆角比率的原理是以图形的最小边为基准，最小边值除以这个比率，就得到了<br>
	 * 圆角半径（即r = min(width, height) / rate），当值为2时是一个圆形。
	 * @param cornerRate
	 * 
	 * @see {@link #setCornerRadius(int)}} 设置圆角半径方法
	 */
	public void setCornerRate(int cornerRate) {
		this.mCornerRate = cornerRate;
		this.mCornerRadius = DEFAULT_CORNER_RADIUS;
		invalidate();
	}

	/**
	 * 设置边框宽度
	 * @param borderThickness
	 */
	public void setBorderThickness(int borderThickness) {
		this.mBorderThickness = borderThickness;
		if(mBorderThickness > DEFAUTL_BORDER_THICKNESS) {
			mBorderOutsideThickness = DEFAUTL_BORDER_THICKNESS;
			mBorderInsideThickness = DEFAUTL_BORDER_THICKNESS;
		}
		invalidate();
	}

	/**
	 * 设置内边框宽度
	 * @param borderInsideThickness
	 */
	public void setBorderInsideThickness(int borderInsideThickness) {
		this.mBorderInsideThickness = borderInsideThickness;
		if(mBorderInsideThickness > DEFAUTL_BORDER_THICKNESS) {
			mBorderThickness = DEFAUTL_BORDER_THICKNESS;
		}
		invalidate();
	}

	/**
	 * 设置外边框宽度
	 * @param borderOutsideThickness
	 */
	public void setBorderOutsideThickness(int borderOutsideThickness) {
		this.mBorderOutsideThickness = borderOutsideThickness;
		if(mBorderOutsideThickness > DEFAUTL_BORDER_THICKNESS) {
			mBorderThickness = DEFAUTL_BORDER_THICKNESS;
		}
		invalidate();
	}

	/**
	 * 设置边框颜色
	 * @param borderColor
	 */
	public void setBorderColor(int borderColor) {
		this.mBorderColor = borderColor;
		this.mBorderOutsideColor = borderColor;
		this.mBorderInsideColor = borderColor;
		invalidate();
	}

	/**
	 * 设置内边框颜色
	 * @param borderInsideColor
	 */
	public void setBorderInsideColor(int borderInsideColor) {
		this.mBorderInsideColor = borderInsideColor;
		invalidate();
	}

	/**
	 * 设置外边框颜色
	 * @param borderOutsideColor
	 */
	public void setBorderOutsideColor(int borderOutsideColor) {
		this.mBorderOutsideColor = borderOutsideColor;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
    	if(mCornerRate == DEFAULT_CORNER_RATE && mCornerRadius == DEFAULT_CORNER_RADIUS) {
    		// 没有设置边角属性，直接调用原来的绘制方法
    		super.onDraw(canvas);
    		return;
    	}
    	
		Drawable drawable = this.getDrawable();
		if (drawable == null) {
			super.onDraw(canvas);
			return;
		}

		if (getWidth() == 0 || getHeight() == 0) {
			super.onDraw(canvas);
			return;
		}
		
        if (drawable instanceof BitmapDrawable) {
        	if (mViewWidth == 0) {
        		mViewWidth = getWidth();
        	}
        	if (mViewHeight == 0) {
        		mViewHeight = getHeight();
        	}
        	
        	int width = mViewWidth;
        	int height = mViewHeight;
        	if (width <= 0 || height <= 0) {
        		return;
        	}
        	int diameter = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;
            Paint paint = ((BitmapDrawable) drawable).getPaint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.FILL);
            
            mDrawRect.set(0, 0, mViewWidth, mViewHeight);
            
            int saveCount = canvas.saveLayer(mDrawRect, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG 
            		| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG  
                    | Canvas.CLIP_TO_LAYER_SAVE_FLAG);  
      
            canvas.drawARGB(0, 0, 0, 0); 

        	float radius = 0;
        	if(mCornerRate == DEFAULT_CORNER_RATE) {
        		radius = mCornerRadius;
        	} else {
        		radius = ((float) diameter) / mCornerRate;
        	}
            	
            if(mBorderThickness != DEFAUTL_BORDER_THICKNESS) { // 单框
            	
            	float drawableRadius = radius - mBorderThickness;
            	paint.setColor(mBorderColor);
            	mTempRect.set(mDrawRect.left + mBorderThickness, mDrawRect.top + mBorderThickness, 
            			mDrawRect.right - mBorderThickness, mDrawRect.bottom - mBorderThickness);
            	canvas.drawRoundRect(mTempRect, drawableRadius, drawableRadius, paint);  
            	paint.setXfermode(mModeSrcIn);  
            	super.onDraw(canvas);

            	paint.setXfermode(mModeDstOver);
            	paint.setColor(mBorderColor);
            	canvas.drawRoundRect(mDrawRect, radius, radius, paint);  
            } else if(mBorderInsideThickness != DEFAUTL_BORDER_THICKNESS 
            		|| mBorderOutsideThickness != DEFAUTL_BORDER_THICKNESS) { // 双框
            	
            	float drawableRadius = radius - mBorderInsideThickness - mBorderOutsideThickness;
            	
            	if(mBorderInsideThickness == DEFAUTL_BORDER_THICKNESS) { // 只有外框
                	paint.setColor(mBorderOutsideColor);
                	mTempRect.set(mDrawRect.left + mBorderOutsideThickness, mDrawRect.top + mBorderOutsideThickness, 
                			mDrawRect.right - mBorderOutsideThickness, mDrawRect.bottom - mBorderOutsideThickness);
                	canvas.drawRoundRect(mTempRect, drawableRadius, drawableRadius, paint);  
                	paint.setXfermode(mModeSrcIn);  
                	super.onDraw(canvas);
                	paint.setColor(mBorderOutsideColor);
            	} else if(mBorderOutsideThickness == DEFAUTL_BORDER_THICKNESS) { // 只有内框
            		
            		paint.setColor(mBorderInsideColor);
            		mTempRect.set(mDrawRect.left + mBorderInsideThickness, mDrawRect.top + mBorderInsideThickness, 
            				mDrawRect.right - mBorderInsideThickness, mDrawRect.bottom - mBorderInsideThickness);
            		canvas.drawRoundRect(mTempRect, drawableRadius, drawableRadius, paint);  
            		paint.setXfermode(mModeSrcIn);  
            		super.onDraw(canvas);
            		paint.setColor(mBorderInsideColor);
            	} else { // 有内外框
            		
            		paint.setColor(mBorderInsideColor);
            		mTempRect.set(mDrawRect.left + mBorderInsideThickness + mBorderOutsideThickness, 
            				mDrawRect.top + mBorderInsideThickness + mBorderOutsideThickness, 
            				mDrawRect.right - mBorderInsideThickness - mBorderOutsideThickness, 
            				mDrawRect.bottom - mBorderInsideThickness - mBorderOutsideThickness);
            		canvas.drawRoundRect(mTempRect, drawableRadius, drawableRadius, paint);  
            		paint.setXfermode(mModeSrcIn);  
            		super.onDraw(canvas);
            		
            		float insideBorderRadius = radius - mBorderOutsideThickness;
            		paint.setXfermode(mModeDstOver);
            		paint.setColor(mBorderInsideColor);
            		mTempRect.set(mDrawRect.left + mBorderOutsideThickness, 
            				mDrawRect.top + mBorderOutsideThickness, 
            				mDrawRect.right - mBorderOutsideThickness, 
            				mDrawRect.bottom - mBorderOutsideThickness);
            		canvas.drawRoundRect(mTempRect, insideBorderRadius, insideBorderRadius, paint);  
            		
            		paint.setColor(mBorderOutsideColor);
            	}
            	
            	paint.setXfermode(mModeDstOver);
            	canvas.drawRoundRect(mDrawRect, radius, radius, paint);
            	
            } else { // 无框
            	paint.setColor(Color.WHITE);
            	canvas.drawRoundRect(mDrawRect, radius, radius, paint);  
            	paint.setXfermode(mModeSrcIn);  
            	super.onDraw(canvas);
            	
            	paint.setXfermode(mModeDstOver);
            	paint.setColor(Color.TRANSPARENT);
            	canvas.drawRoundRect(mDrawRect, radius, radius, paint);  
            	
            }
            canvas.restoreToCount(saveCount);
            
        } else {  
            super.onDraw(canvas);  
        }  

	}
	
//	以下代码据说效率更高，但是只支持API11及以上，而且不能画边框，如果符合条件的可以使用下面的代码
//	大概思路：创建以“圆角矩形”为结构的Path，并利用Path.FillType.INVERSE_WINDING反选“圆角矩形区域”。从而达到圆角边缘化的效果。
//	优点：与前者相比，由于不需要对ImageView的图片进行字节操作，所以速度快许多，而且在动画表现上十分平滑。
//	缺点：暂无。	
//	Paint mMaskPaint = new Paint();
//	
//	Path mMaskPath;
//	
//	@TargetApi(11)  
//    private void init() {  
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);  
//        this.mMaskPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
//    }  
//      
//    private void generateMaskPath(int width, int height) {  
//        this.mMaskPath = new Path();  
//        mCornerRadius = 50;
//        this.mMaskPath.addRoundRect(new RectF(0.0F, 0.0F, width, height), this.mCornerRadius, this.mCornerRadius, Path.Direction.CW);  
//        this.mMaskPath.setFillType(Path.FillType.INVERSE_WINDING);
//    }  
//  
//    @Override  
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
//        super.onSizeChanged(w, h, oldw, oldh);  
//        if ((w != oldw) || (h != oldh))  
//            generateMaskPath(w, h);  
//  
//    }  
//  
//    protected void onDraw(Canvas canvas) {  
//        // 保存当前layer的透明橡树到离屏缓冲区。并新创建一个透明度爲255的新layer  
//        int saveCount = canvas.saveLayerAlpha(0.0F, 0.0F, canvas.getWidth(), canvas.getHeight(),  
//                255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
//        
//        super.onDraw(canvas);
//        
//        if (this.mMaskPath != null) {  
//            canvas.drawPath(this.mMaskPath, this.mMaskPaint);
//        } 
//        
//        canvas.restoreToCount(saveCount);      
//    }
}
