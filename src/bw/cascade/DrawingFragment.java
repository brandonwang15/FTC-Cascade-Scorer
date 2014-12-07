package bw.cascade;


import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import bw.cascade.misc.BitmapEntry;

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
		final BitmapEntry entry = new BitmapEntry();
		
		//Create dialog to prompt for save name and notes
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_text_input, null);
		final EditText nameField = (EditText) dialogView.findViewById(R.id.nameField);
		final EditText notesField = (EditText)dialogView.findViewById(R.id.extraInfoField);
		
		//initalize dialog
		builder.setView(dialogView)
				.setPositiveButton(R.string.save_bitmap, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
						//nothing for now, will be overriden later
					}		
				})
				.setNegativeButton(R.string.cancel_bitmap, new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;//do nothing
					}			
				})
				.setTitle("Save Route");
		final AlertDialog dialog = builder.create();
		dialog.show();
		
		//override the dialog button behavior here, so that the automatic closing of the dialog when a button is clicked can be avoided
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = nameField.getText().toString().trim();
				
				//name must have non-whitespace characters
				if(name.equals(""))
				{
					Toast.makeText(getActivity(), "You can't input an empty name!", Toast.LENGTH_LONG).show();
					return;//end without closing dialog
				}
				
				//check if given name already exists
				if(callback.doesSaveNameExist(name))
				{
					Toast.makeText(getActivity(), "A saved route with the name:\"" +nameField.getText().toString() +"\" already exists!", Toast.LENGTH_LONG).show();
					return;//end without closing dialog
				}
				
				//if all data is valid
				entry.setName(name);
				entry.setBitmap(getAutonomousBitmap());
				entry.setNotes(notesField.getText().toString().trim());
				callback.addSavedAutoRoute(entry);
				Toast.makeText(getActivity(), "Image saved!", Toast.LENGTH_LONG).show();
				dialog.dismiss();
				
			}
		});
	
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
		//IMPORTANT: Must set drawing cache enabled true before AND set enabled false after - otherwise the cache is never refreshed (or something - either way it breaks)
		d.buildDrawingCache();
		Bitmap image = d.getDrawingCache().copy(Bitmap.Config.ARGB_8888,false);//make a copy of the bitmap so it doesn't get recycled with destoryDrwaingCache()
		d.destroyDrawingCache();
		return image;
		
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
