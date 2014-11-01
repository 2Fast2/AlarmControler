package es.jorge.alarmcontroler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;

import es.jorge.alarmcontroler.R;
import es.jorge.alarmcontroler.SettingsActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;


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
	
	// socket name
	private Socket socket;

	// server port connection
	private static final int SERVERPORT = 5000;
	// server IP, this must be the same as the IP in the configuration
	// this value is a default value
	private String SERVER_IP = "192.168.1.40";
	
	// Para ver la IP remota
	String IP;
	int Port;
	private Button btDataRefresh;

    /* connection thread */
	private Thread Connection_Thread;
	
	
	private void log(String string) {
		//
		Toast.makeText(this, (string), Toast.LENGTH_LONG).show();
	}
	

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
		
		// start the thread that connect to the server.
        Connection_Thread = new Thread(new ClientThread());
        Connection_Thread.start();
		

	/*	btDataRefresh = (Button) findViewById(R.id.Data_Ref);
		
		
		btDataRefresh.setOnClickListener(new OnClickListener() {
			@Override
			// conectar
			public void onClick(View v) {
								
				//click_data_refresh();
				Log.d("Estamos","Aqui");
				
			}
		});*/

	}
	
			
	@Override
	protected void onRestart (){
		super.onRestart();

        Thread.State State;
		// take the new number of sensors		
		String NumSensors = pref.getString("num_sensors","");

		// check if the number of sensors was changed
		if (Sensors != Integer.valueOf(NumSensors)){
			// update viewPager with the new number of sensors
			mSectionsPagerAdapter.notifyDataSetChanged();
			// update sensors value
			Sensors = Integer.valueOf(NumSensors);
			Toast.makeText(this, "ACTUALIZAMOS NUM SENSORES", Toast.LENGTH_SHORT).show();
		}		
		Toast.makeText(this, NumSensors, Toast.LENGTH_SHORT).show();

        // take IP server
        String New_Server_IP = pref.getString("ip","");

        // check server ip changes
        if(SERVER_IP != New_Server_IP){
            // update server ip
            SERVER_IP = New_Server_IP;
            Toast.makeText(this, SERVER_IP, Toast.LENGTH_SHORT).show();
            /* after change the server ip try to connect to the new server ip */
            if (socket != null) {
                /* the socket is connected. Close the socket */
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

             /* connect again to the new ip server */
            State = Connection_Thread.getState();

            Toast.makeText(this,State.toString() , Toast.LENGTH_SHORT).show();

            Connection_Thread = new Thread(new ClientThread());
            Connection_Thread.start();



        }
	}
	
	// TODO HAY QUE CREAR UN SERVICIO DE QUE CUANDO SE DESTRUYA LIBERE LA CONEXI�N
	
	
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
			pref = PreferenceManager.getDefaultSharedPreferences(ACMainActivity.this);	// esto se puede meter en la funci�n de getcount o en el OnCreate	
					
			//////// esto se puede sustituir por la llamada a getCount
			String NumSensors = pref.getString("num_sensors","");
			if (NumSensors == ""){
				Sensors = 1;
			}else{
				// change from String to an Int
				Sensors = Integer.valueOf(NumSensors);
			}
			/////////////////////////////////

		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			switch (position) {
			case 0:
				// activity for Main control tab
				Fragment MCfragment = new MainControlFragment();
				return MCfragment;
			default:
				//activity for sensor tabs
				Fragment SWfragment = new SensorViewFragment();
				return SWfragment;
			}
		
		}

		@Override
		public int getCount() {		
	        // get number of sensors
			String NumSensors = pref.getString("num_sensors","");
			
			return NumSensors == "" ? 1 : (Integer.valueOf(NumSensors) + 1);
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
	 * A main control fragment
	 */
	public static class MainControlFragment extends Fragment {
		
		public MainControlFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_acmain_main_control,
					container, false);
			TextView MainControlTextView = (TextView) rootView
					.findViewById(R.id.section_label);
//			MainControlTextView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}
	
	/**
	 * A sensor view fragment
	 */
	public static class SensorViewFragment extends Fragment {
		
		public SensorViewFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_acmain_sensor_view,
					container, false);
			TextView SensorViewTextView = (TextView) rootView
					.findViewById(R.id.section_label);
//			MainControlTextView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

    public void Alert_Message (String Message)
    {
        new AlertDialog.Builder(this)
                .setMessage(Message)
                .setPositiveButton("OK", null)
                .show();

    }
	
	public void click_data_refresh(View view){
		
		if( socket != null ) {
            IP = socket.getInetAddress().toString();
            Port = socket.getPort();

            Log.d("IP: ", IP);
            Log.d("Puerto", Integer.toString(Port));

            String msg = "IP: " + IP + " Puerto: " + Port;

            // a provisional alert box to see the IP and port connected
           /* new AlertDialog.Builder(this)
                    .setMessage(msg)
                    .setPositiveButton("OK", null)
                    .show();
                    */
            Alert_Message(msg);
        }else{
           /* new AlertDialog.Builder(this)
                    .setMessage("THE SOCKET IS NOT CONNECTED")
                    .setPositiveButton("OK", null)
                    .show();
*/
            Alert_Message("THE SOCKET IS NOT CONNECTED");
        }
		
	}
	
	// Check if the phone is connected to any network
	private boolean isNetworkConnected() {
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo ni = cm.getActiveNetworkInfo();
		  if (ni == null) {
		   // There are no active networks.
		   return false;
		  } else
			  // there are active networks
		   return true;
		 }
	
	// Thread to connect to the server.
	class ClientThread implements Runnable {
	
		@Override
		public void run() {

			try {

                // take the IP number from the preferences
                SERVER_IP = pref.getString("ip","");
				/*
				A�ADIR QUE PRIMERO MIRE SI HAY CONEXION A INET
				Y SI LA HAY CONECTARSE, SINO DEVOLVER UN MSGBOX
				ADVIRTIENDO DEL ERROR
				check if the phone is connected to any network
				*/
                /* Check if there are any network connected and there are
                 any server ip specified*/
                if (isNetworkConnected() && (SERVER_IP != "") ){
					// try to connect to the server
					InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    /* check if the server is reachable and connect if it is */
                    if (serverAddr.isReachable(2000)) {
                        socket = new Socket(serverAddr, SERVERPORT);
                   }else{
                        return;
                    }
				}else{
					// no connection
                    return;

/* TODO NO SE SACAR UN ALERT DIALOG DESDE EL HILO
					AlertDialog.Builder builder = new AlertDialog.Builder(ACMainActivity.this);
					builder.setMessage("There are no networkts available")
			         .setPositiveButton("OK", null)
			         .show();
*/


				}

			} catch (UnknownHostException e1) {
				Log.e("Error connexion", "" + e1);
				e1.printStackTrace();
			} catch (IOException e1) {
				Log.e("Error connexion", "" + e1);
				e1.printStackTrace();
			}

		}

	}
	

}
