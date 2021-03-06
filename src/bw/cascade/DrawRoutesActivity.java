package bw.cascade;



import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import bw.cascade.misc.BitmapEntry;
import bw.cascade.sql.AutoRoutesDatabase;



public class DrawRoutesActivity extends Activity {

	private DrawingFragment mDrawFrag;
	private SavedRoutesFragment mSavedRoutesFrag;
	
	private ArrayList<BitmapEntry> bitmapEntries;
	private AutoRoutesDatabase routesDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_routes);

		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		routesDB = new AutoRoutesDatabase(this);
		bitmapEntries = routesDB.getBitmaps();
		
		//set action bar
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		//instantiate fragments here so that they can be referenced, instead of them being instantitaed in CustomTabListener, where they cannot be referenced
		mDrawFrag = new DrawingFragment();
		mSavedRoutesFrag = new SavedRoutesFragment(bitmapEntries);
		

		actionBar.addTab(actionBar.newTab()
				.setText("Draw Route")
				.setTabListener(new CustomTabListener<DrawingFragment>(mDrawFrag,this,DrawingFragment.class)));
		actionBar.addTab(actionBar.newTab()
				.setText("Saved Routes")
				.setTabListener(new CustomTabListener<SavedRoutesFragment>(mSavedRoutesFrag,this,SavedRoutesFragment.class)));
		
		
		


	}

	/**
	 * Returns whether the given name is already being used by a saved route bitmap entry
	 * @param name Name to check
	 * @return whether name is used
	 */
	public boolean doesSaveNameExist(String name)
	{
		for(BitmapEntry b : bitmapEntries)
		{
			if(b.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public void addSavedAutoRoute(BitmapEntry entry)
	{
		bitmapEntries.add(entry);
		routesDB.addRouteEntry(entry);
		mSavedRoutesFrag.notifyRouteDataChanged();
	}
	
	/**
	 * Removes the given entry from the database and arraylist
	 * @param entry
	 */
	public void deleteSavedAutoRoute(BitmapEntry entry)
	{
		bitmapEntries.remove(entry);
		routesDB.deleteEntry(entry);
	}
}
