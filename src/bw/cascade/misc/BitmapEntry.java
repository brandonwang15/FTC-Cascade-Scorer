package bw.cascade.misc;

import android.graphics.Bitmap;

public class BitmapEntry {
	private Bitmap bitmap;
	private String name;
	private String notes;
	
	public BitmapEntry() {
		super();
		this.bitmap = null;
		this.name = null;
		this.notes = null;
	}
	
	public BitmapEntry(Bitmap bitmap, String name,String notes) {
		super();
		this.bitmap = bitmap;
		this.name = name;
		this.notes = notes;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
