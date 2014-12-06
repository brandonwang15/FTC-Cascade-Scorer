package bw.cascade;


import java.util.ArrayList;

import bw.cascade.misc.BitmapEntry;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class DrawingFragment extends Fragment implements OnClickListener{
	
	private ImageButton bluePicker, redPicker, lightBluePicker, lightRedPicker;
	private Button undoBtn,clearBtn,switchBtn, saveBtn;
	private DrawingView d;
	
	private int currentFieldIndex;//index of currentField to show as backgorund for drawingview
	private Bitmap[] fieldImages;
	
	private MainActivity callback;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		callback = (MainActivity) getActivity();
		
		fieldImages = new Bitmap[3];
		loadFieldImages();
		currentFieldIndex = 0;
		
	}
	
	private void loadFieldImages()
	{
		try{
			fieldImages[0] = BitmapFactory.decodeResource(getResources(), R.drawable.field1);
			fieldImages[1] = BitmapFactory.decodeResource(getResources(), R.drawable.field2);
			fieldImages[2] = BitmapFactory.decodeResource(getResources(), R.drawable.field3);
		}catch(Exception e)
		{
			Log.e("abc", "Error loading bitmaps.");
		}
	}
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_drawing, container,false);
		
		switchBtn = (Button)v.findViewById(R.id.switchFieldBtn);
		switchBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				currentFieldIndex++;
				currentFieldIndex %= 3;
				d.setNewImage(fieldImages[currentFieldIndex]);
				
			}
			
		});
		
		clearBtn = (Button)v.findViewById(R.id.clearDrawingsButton);
		clearBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				d.clear();
				
			}
			
		});
		
		undoBtn = (Button)v.findViewById(R.id.undoBtn);
		undoBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				d.undoLastPath();
				Toast.makeText(getActivity(), "undo", Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		saveBtn = (Button)v.findViewById(R.id.saveImageBtn);
		saveBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				saveCurrentRoute();
			}
			
		});
		
		//set up color picker buttons
		bluePicker = (ImageButton)v.findViewById(R.id.blueColorBtn);
		lightBluePicker = (ImageButton)v.findViewById(R.id.lightBlueColorBtn);
		redPicker = (ImageButton)v.findViewById(R.id.redColorBtn);
		lightRedPicker = (ImageButton)v.findViewById(R.id.lightRedColorBtn);
		
		bluePicker.setOnClickListener(this);
		lightBluePicker.setOnClickListener(this);
		redPicker.setOnClickListener(this);
		lightRedPicker.setOnClickListener(this);
		
		d = new DrawingView(getActivity());
		FrameLayout f = (FrameLayout)v.findViewById(R.id.draw_frag_frame);
		f.addView(d);
		
		return v;
	}
	
	/**
	 * saves the current route
	 */
	private void saveCurrentRoute()
	{
		BitmapEntry entry = new BitmapEntry();
		//TODO: prompt user to input title 
		entry.setBitmapName("placeholder_name");
		entry.setBitmap(getAutonomousBitmap());
		callback.addSavedAutoRoute(entry);
		Toast.makeText(getActivity(), "Image saved!", Toast.LENGTH_LONG).show();
	}
	
	/***
	 * @return: the autonomous path, null if a path was never created
	 */
	public ArrayList<Path> getAutonomousPaths()
	{
		if(d == null)
			return null;
		
		return d.getPaths();
	}
	
	/***
	 * @return bitmap of the view 
	 */
	@SuppressLint("WrongCall")
	public Bitmap getAutonomousBitmap()
	{
		if(d == null)
			return null;
		d.setDrawingCacheEnabled(true);
		return d.getDrawingCache();
		
//		Bitmap b = Bitmap.createBitmap(d.getWidth(), d.getHeight(), Bitmap.Config.ARGB_8888);
//		Canvas c = new Canvas(b);
//		d.layout(0, 0, d.getLayoutParams().width, d.getLayoutParams().height);
//		d.drawToGivenCanvas(c);
//		return b;
	}
	



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == redPicker)
			d.setDrawColor(Color.parseColor("#FF0000"));
		else if(v == bluePicker)
			d.setDrawColor(Color.parseColor("#004F9E"));
		else if(v == lightBluePicker)
			d.setDrawColor(Color.parseColor("#5AB7FA"));
		else if(v == lightRedPicker)
			d.setDrawColor(Color.parseColor("#FFCCCC"));
			
		
			
	}
	
}
