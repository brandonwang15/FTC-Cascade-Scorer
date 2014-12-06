package bw.cascade.misc;

import android.graphics.Bitmap;

public class BitmapEntry {
	private Bitmap bitmap;
	private String bitmapName;
	
	public BitmapEntry() {
		super();
		this.bitmap = null;
		this.bitmapName = null;
	}
	
	public BitmapEntry(Bitmap bitmap, String bitmapName) {
		super();
		this.bitmap = bitmap;
		this.bitmapName = bitmapName;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getBitmapName() {
		return bitmapName;
	}
	public void setBitmapName(String bitmapName) {
		this.bitmapName = bitmapName;
	}
	
	
}
