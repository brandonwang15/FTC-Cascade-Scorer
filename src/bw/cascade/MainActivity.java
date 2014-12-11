package bw.cascade;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import bw.cascade.misc.BitmapEntry;
import bw.cascade.sql.AutoRoutesDatabase;

public class MainActivity extends Activity{
	private DrawingFragment mDrawFrag;
	private SavedRoutesFragment mSavedRoutesFrag;
	
	private ArrayList<BitmapEntry> bitmapEntries;
	private AutoRoutesDatabase routesDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		
		
		


	}


}
