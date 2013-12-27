package es.jorge.alarmcontroler;

import java.util.Locale;

import es.jorge.alarmcontroler.R;
import es.jorge.alarmcontroler.SettingsActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ACMainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	// local copy of preferences
	SharedPreferences pref;
	
	// store number of sensors
	private int Sensors = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acmain);

		// Create the adapter that will return a fragment for each of the
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
	}
		
	@Override
	protected void onRestart (){
		super.onRestart();

		// take the new number of sensors
//		SharedPreferences pref =
//				PreferenceManager.getDefaultSharedPreferences(ACMainActivity.this);			
		String NumSensors = pref.getString("num_sensors","");

		// check if the number of sensors was changed
		if (Sensors != Integer.valueOf(NumSensors)){
			// update viewPager
			mSectionsPagerAdapter.notifyDataSetChanged();
			Toast.makeText(this, "ACTUALIZAMOS NUM SENSORES", Toast.LENGTH_SHORT).show();
		}		
		Toast.makeText(this, NumSensors, Toast.LENGTH_SHORT).show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.acmain, menu);
		return true;
	}
	
	// launch the setting activity
	public void lanzarSettingsActivity(View view) {
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);		
	}

	// to catch the settings menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.SettingsActivity:
			lanzarSettingsActivity(null);
			break;
		}
		
		return true;
		/** true -> consumimos el item, no se propaga */
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			
			// store the initial number of sensors
			pref = PreferenceManager.getDefaultSharedPreferences(ACMainActivity.this);			
			String NumSensors = pref.getString("num_sensors","");
			// change from String to an Int
			Sensors = Integer.valueOf(NumSensors);

		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			// get number of sensors
			
			Fragment fragment = new MainControlFragment();
			Bundle args = new Bundle();
			args.putInt(MainControlFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {		
	        // get number of sensors
//			SharedPreferences pref =
//		            PreferenceManager.getDefaultSharedPreferences(ACMainActivity.this);		
			
			String NumSensors = pref.getString("num_sensors","");
			
			return NumSensors == null ? -1 : (Integer.valueOf(NumSensors) + 1);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			case 5:
				return getString(R.string.title_section6).toUpperCase(l);
			case 6:
				return getString(R.string.title_section7).toUpperCase(l);
			case 7:
				return getString(R.string.title_section8).toUpperCase(l);
			case 8:
				return getString(R.string.title_section9).toUpperCase(l);
			case 9:
				return getString(R.string.title_section10).toUpperCase(l);
			case 10:
				return getString(R.string.title_section11).toUpperCase(l);
				
			}
			return null;
		}
		
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class MainControlFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public MainControlFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_acmain_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	

}
