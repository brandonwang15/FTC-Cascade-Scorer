package bw.cascade;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import bw.cascade.misc.BitmapEntry;

public class SavedRoutesFragment extends Fragment {
	
	private FrameLayout drawingViewFrame;
	private DrawingView drawingView;
	private Spinner chooseRouteSpinner;
	private Button deleteEntryBtn;
	private TextView notesView;
	
	private ArrayAdapter<String> spinnerData;
	private ArrayList<BitmapEntry> savedRoutes;
	private ArrayList<String> adapterData;//choices for array adapter
	
	private MainActivity callback;
	public SavedRoutesFragment(ArrayList<BitmapEntry> savedRoutes)
	{
		this.savedRoutes = savedRoutes;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
		callback = (MainActivity)getActivity();
	}
	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_saved_routes, container,false);
		
		notesView = (TextView)v.findViewById(R.id.notesView);
		drawingViewFrame = (FrameLayout) v.findViewById(R.id.savedRouteDisplayFrame);
		chooseRouteSpinner = (Spinner)v.findViewById(R.id.selectSavedImageSpinner);
		deleteEntryBtn = (Button)v.findViewById(R.id.deleteSaveBtn);
		deleteEntryBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				deleteCurrentEntry();
			}
			
		});
		
		initializeDrawingView();
		populateSpinner();
		
		return v;
	}

	/**
	 * Deletes the current route
	 */
	private void deleteCurrentEntry()
	{
		int pos = chooseRouteSpinner.getSelectedItemPosition();
		
		if(pos == -1)
		{
			Toast.makeText(getActivity(), "Nothing to delete!", Toast.LENGTH_LONG).show();
			return;
		}
		
		BitmapEntry entryToDelete = savedRoutes.get(pos);
		callback.deleteSavedAutoRoute(entryToDelete);
		
		//update which route is now displayed
		notifyRouteDataChanged();
		if(pos >= savedRoutes.size())//if the last route in the list was deleted, display the new last route
		{
			pos = savedRoutes.size()-1;
		}
		if(savedRoutes.size() == 0)//if the list is now empty
		{
			drawingView.setVisibility(View.INVISIBLE);
			notesView.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * populate the spinner with list of saved routes that can be displayed
	 */
	private void populateSpinner()
	{
		adapterData = new ArrayList<String>();
		for(BitmapEntry b : savedRoutes)
			adapterData.add(b.getName());
		
		spinnerData = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1,adapterData);
		chooseRouteSpinner.setAdapter(spinnerData);
		chooseRouteSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				displayBitmapEntry(savedRoutes.get(position));
				notesView.setText(savedRoutes.get(position).getNotes());
				notesView.setVisibility(View.VISIBLE);
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
		{
			//update choices
			adapterData.clear();
			for(BitmapEntry b : savedRoutes)
				adapterData.add(b.getName());
			
			spinnerData.notifyDataSetChanged();
		}
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
		{
			Toast.makeText(getActivity(), entry.getName() +" was not able to be displayed.", Toast.LENGTH_LONG).show();
			drawingView.setVisibility(View.INVISIBLE);
		}
		else
		{
			drawingView.setNewImage(entry.getBitmap());
			drawingView.invalidate();
			drawingView.setVisibility(View.VISIBLE);
		}
	}

}
