# RoundImageView
A Custom ImageView to show image with round corner

一个可以设置图片显示成圆角的图片显示控件，还支持给图片添加边框，并且支持双边框

这里提供了两种圆角图片显示控件的方案，RoundImageView 和 NewRoundImageView，这两种方案各有各的局限，详细请看源码的类说明。


#RoundImageView
可以添加边框，支持双边框<br>

<p>大概思路：ImageView会将源图片最终转化为一个Drawable，<br>
通过ImageView.getDrawable()获取该Drawable，<br>
并通过BitmapDrawable.getPaint()获取其画笔。<br>
通过saveLayer创建一个新图层，并在上面绘制。<br>
对画笔使用Paint.setXfermode(android.graphics.Xfermode)设置PorterDuffXfermode。<br>
从而将圆角效果绘制出来。<br>
<p>优点：能很好的兼容ImageView的scaleType<br>
<p>缺点：<br>
 1、运行速度较为缓慢，由于onDraw运行在ui线程，PorterDuffXfermode是采用SRC_IN的方式进行图像裁剪，
这种裁剪方式的速度具体视图像大小质量而视，使用不当容易Anr。<br><br>
2、从上面获取画笔的方式可以看出，这种方案只支持BitmapDrawable的图片类型，这个对xml中设置图片和
代码中通过ImageView.setImageBitmap(android.graphics.Bitmap)、ImageView.setImageResource(int)
方法设置图片时，可以完美达到效果，但是如果代码中通过ImageView.setImageDrawable(Drawable)的方式设置图片,
当且仅当入参为BitmapDrawable 对象及其子类对象时，才能实现效果，否者将可能不是预期的效果，
（比如入参类型为TransitionDrawable（有渐变显示动画效果）时）

<p><font color="#FF0000">
特别提示：如果图片的长宽比例和控件的长宽不一定一致是，
强烈建议控件的缩放方式设置为{@link ImageView.ScaleType#CENTER_CROP},否则将可能达不到预期效果。
因为只是控件显示的时候处理，并没有处理图片，也就是图片本身不是圆角的，当图片没有填满控件的时候，
空白区域将会使用填充颜色填充，最终的效果可能图片不是圆角的。

</font>

#NewRoundImageView
可以添加边框，支持双边框<br>

<p>大概思路：创建以“圆角矩形”为结构的Path，并利用Path.FillType.INVERSE_WINDING反选“圆角矩形区域”。
从而达到圆角边缘化的效果。<br>
优点：由于不需要对ImageView的图片进行字节操作，所以速度快许多，而且在动画表现上十分平滑。<br>
缺点：暂无，如果要说有，那就是在API 11及以上版本才能使用。<br>

<p><font color="#FF0000">
特别提示：如果在API 11或者以上版本的系统，强烈建议使用这个。<br><br>
如果图片的长宽比例和控件的长宽不一定一致是，
强烈建议控件的缩放方式设置为ImageView.ScaleType.CENTER_CROP,否则将可能达不到预期效果。
因为只是控件显示的时候处理，并没有处理图片，也就是图片本身不是圆角的，当图片没有填满控件的时候，
空白区域将会使用填充颜色填充，最终的效果可能图片不是圆角的。
</font>