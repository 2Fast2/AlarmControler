package es.jorge.alarmcontroler;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Locale;


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
	private static int Sensors = 0;
	
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

    /* to see if the socket is connected */
    private static boolean Is_Connected = false;

    /* output stream to send the information */
    OutputStream out;
    DataOutputStream dout;

    /* progress circle dialog */
    private ProgressDialog Circle_Pogress;

    /* Tx messages */
    TX_Messages Msg_Tx;


	private void log0(String string) {
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

        /* create the circle progress */
        Circle_Pogress = new ProgressDialog(this);

        /* create TX msg */
        Msg_Tx = new TX_Messages();

		// start the thread that connect to the server.
        Connection_Thread = new Thread(new ClientThread());
        Connection_Thread.start();
	}

    public void Reconnection (){

        Thread.State State;

        // take IP server
        String New_Server_IP = pref.getString("ip","");

        // check server ip changes
        if(SERVER_IP != New_Server_IP) {
            // update server ip
            SERVER_IP = New_Server_IP;
            Toast.makeText(this, SERVER_IP, Toast.LENGTH_SHORT).show();
            /* after change the server ip try to connect to the new server ip */
            if (socket != null) {
                /* the socket is connected. Close the socket */
                try {
                    socket.close();
                    MainControlFragment.Change_Reconnected_Button_BG(0xFFFF0000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
             /* connect again to the new ip server */
            State = Connection_Thread.getState();

            Toast.makeText(this,State.toString() , Toast.LENGTH_SHORT).show();

        if (Connection_Thread.isAlive()){
            /* first close the thread */
            Connection_Thread.interrupt();
        }
            Connection_Thread = new Thread(new ClientThread());
            Connection_Thread.start();

    }

    protected void onDestroy(){
        super.onDestroy();

        /* release the connection */
        try {
            if (dout != null)
               dout.close();
            if (out != null)
               out.close();
            if (socket != null) {
                socket.close();
                if (socket.isClosed()) {
                    Toast.makeText(this, "SOCKET RELEASE", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            Toast.makeText(this,"MEEEEC" , Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
			
	@Override
	protected void onRestart (){
		super.onRestart();


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

        /* reconnect with the server */
        Reconnection();

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
			pref = PreferenceManager.getDefaultSharedPreferences(ACMainActivity.this);	// esto se puede meter en la funci�n de getcount o en el OnCreate	
					
			//////// esto se puede sustituir por la llamada a getCount
			String NumSensors = pref.getString("num_sensors","");
			if (NumSensors.equals("")){
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
				Fragment SWfragment = new SensorViewFragment().newInstance(position);
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

    public void Alert_Message (String Message)
    {
        new AlertDialog.Builder(this)
                .setMessage(Message)
                .setPositiveButton("OK", null)
                .show();

    }

    public static boolean Get_Is_Connected(){
        return Is_Connected;
    }

    public static int Get_Number_Of_Sensors(){
        return Sensors;
    }

    public void Click_Sensor_Power_Switch(View view){
        SensorViewFragment.onClick_Sensor_Power_Switch(view);
    }

    public void Click_Reset_Sensor_Button(View view){
        SensorViewFragment.onClick_Reset_Sensor_Button(view);
    }

    public void click_data_refresh_button(View view) {

        String Msg_1;
        String Msg_2;
        String Msg;
        int i;

        try {
            // SOLO PARA DEPURAR****************************************************
            Msg_1 = Msg_Tx.Get_Msg_1();
            Msg = Msg_1;
            for (i=0; i < Sensors; i++) {
                Msg_2 = Msg_Tx.Get_Msg_2(i);
                Msg = Msg + Msg_2;
            }
            // SOLO PARA DEPURAR****************************************************

            if (socket != null) {
                /* create msg#1 */
                Msg_1 = Msg_Tx.Get_Msg_1();
                Msg = Msg_1;
                for (i=0; i < Sensors; i++) {
                    Msg_2 = Msg_Tx.Get_Msg_2(i);
                    Msg = Msg + Msg_2;
                }


                out = socket.getOutputStream();
                dout = new DataOutputStream(out);
                dout.writeInt(1234);
            /*dout.writeLong(123L);
            dout.writeFloat(1.2f);*/
                //dout.writeChars("Boton1");
                dout.flush();
            }else{
                Alert_Message("Not connected with the Alarm. Try to reconnect.");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR: Not connected to the Alarm", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR to send the petition", Toast.LENGTH_SHORT).show();
        }
        /* TODO SOLO PARA PRUEBAS */


    }
	
	public void click_reconnect_button(View view){
		
		if( socket != null ) {
            IP = socket.getInetAddress().toString();
            Port = socket.getPort();

            Log.d("IP: ", IP);
            Log.d("Puerto", Integer.toString(Port));

            String msg = "IP: " + IP + " Puerto: " + Port;

            Alert_Message(msg);
        }else{

            Alert_Message("the socket is not connected");

            /* reconnect with the server */
            Reconnection();

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

    // Start circle progress bar
    public void Start_Circle_Progress(){
        Circle_Pogress.setMessage("Connecting with the Alarm");
        Circle_Pogress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        Circle_Pogress.setIndeterminate(true);
        Circle_Pogress.show();
    }

    // Stop circle progress bar
    public void Stop_Circle_Progress(){
        Circle_Pogress.dismiss();
    }

	
	// Thread to connect to the server.
	class ClientThread implements Runnable {

        @Override
        public void run() {

            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            /* start circle progress bar */
            ACMainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Start_Circle_Progress();
            }
                });
            try {

                // take the IP number from the preferences
                SERVER_IP = pref.getString("ip","");

        /* Check if there are any network connected and there are
         any server ip specified*/
                if (isNetworkConnected() && (SERVER_IP != "") ){
                    // try to connect to the server
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            /* check if the server is reachable and connect if it is */

                    socket = new Socket(serverAddr, SERVERPORT);
                    if (socket.isBound())
                        Is_Connected = true;
                 }else{
            /* not connected finish the thread*/
                    Is_Connected = false;
   				}

			} catch (UnknownHostException e1) {
            Log.e("Error connexion", "" + e1);
            e1.printStackTrace();
            Is_Connected = false;
                ACMainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Stop_Circle_Progress();
                        MainControlFragment.Change_Reconnected_Button_BG(0xFFFF0000);
                        Alert_Message("Unable to connect with the server");

                    }
                });
        } catch (IOException e1) {
            Log.e("Error connexion", "" + e1);
            e1.printStackTrace();
            Is_Connected = false;
                ACMainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Stop_Circle_Progress();
                        MainControlFragment.Change_Reconnected_Button_BG(0xFFFF0000);
                 Alert_Message("Unable to connect with the server");

                    }
                });

        }

            if (Is_Connected){
                ACMainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Stop_Circle_Progress();
                        /* change o green colour */
                        MainControlFragment.Change_Reconnected_Button_BG(Color.GREEN);
                    }
                });
            }else{
                ACMainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Stop_Circle_Progress();
                        /* change o red colour */
                        MainControlFragment.Change_Reconnected_Button_BG(Color.RED);
                    }
                });
            }

		}

	}

}
