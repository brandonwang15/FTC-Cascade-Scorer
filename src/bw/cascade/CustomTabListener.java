package bw.cascade;


import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class CustomTabListener<T extends Fragment> implements TabListener {

	private Fragment fragment;
	private final Activity mBaseActivity;//activity to attach fragment to
	private final Class<T> mFragmentClass;
	
	public CustomTabListener(Fragment fragment, Activity activity, Class<T> fragClass)
	{
		this.fragment = fragment;
		mBaseActivity = activity;
		mFragmentClass = fragClass;
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		//nothing needs to be done

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if(fragment == null)
		{
			throw new IllegalArgumentException("Fragment must already be instantiated.");
			
		}

		//if the fragment has not yet been added to the activity, add it now
		if(fragment.getActivity() == null)
			ft.add(R.id.fragment_container, fragment);
		
		ft.show(fragment);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if(fragment != null)
			ft.hide(fragment);
	}

}



