package bw.cascade;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import bw.cascade.misc.BitmapEntry;

public class SavedRoutesFragment extends Fragment {
	
	private FrameLayout drawingViewFrame;
	private DrawingView drawingView;
	private Spinner chooseRouteSpinner;
	private ArrayAdapter<String> spinnerData;
	private ArrayList<BitmapEntry> savedRoutes;
	
	public SavedRoutesFragment(ArrayList<BitmapEntry> savedRoutes)
	{
		this.savedRoutes = savedRoutes;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		
	}
	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_saved_routes, container,false);
		
		drawingViewFrame = (FrameLayout) v.findViewById(R.id.savedRouteDisplayFrame);
		chooseRouteSpinner = (Spinner)v.findViewById(R.id.selectSavedImageSpinner);
		
		initializeDrawingView();
		populateSpinner();
		
		return v;
	}

	/**
	 * populate the spinner with list of saved routes that can be displayed
	 */
	private void populateSpinner()
	{
		ArrayList<String> choices = new ArrayList<String>();
		for(BitmapEntry b : savedRoutes)
			choices.add(b.getBitmapName());
		
		spinnerData = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1,choices);
		chooseRouteSpinner.setAdapter(spinnerData);
		chooseRouteSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				displayBitmapEntry(savedRoutes.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				return;
			}
			
		});
	
	}
	
	/**
	 * called when the data svaed in savedRoutes changes
	 */
	public void notifyRouteDataChanged()
	{
		if(spinnerData != null)
			spinnerData.notifyDataSetChanged();
	}
	
	private void initializeDrawingView()
	{
		drawingView = new DrawingView(getActivity());
		drawingView.setVisibility(View.GONE);
		drawingView.setEditable(false);
		
		drawingViewFrame.addView(drawingView);
		if(savedRoutes.size() > 0)
		{
			displayBitmapEntry(savedRoutes.get(0));
		}
		
	}

	private void displayBitmapEntry(BitmapEntry entry)
	{
		Bitmap b = entry.getBitmap();
		if(b == null)
			Toast.makeText(getActivity(), entry.getBitmapName() +" was not able to be displayed.", Toast.LENGTH_LONG).show();
		else
		{
			drawingView.setNewImage(entry.getBitmap());
			drawingView.invalidate();
			drawingView.setVisibility(View.VISIBLE);
		}
	}

}
