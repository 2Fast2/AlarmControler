package es.jorge.alarmcontroler;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Jorge on 02/11/2014.

public class MainControlFragment {
}
*/

/**
 * A main control fragment
 */
public class MainControlFragment extends Fragment {

    private static Button Recon_Button;

    /* main switch On/Off */
    Switch Main_Switch;
    private static boolean Main_Switch_State;

    public MainControlFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_acmain_main_control,
                container, false);
        TextView MainControlTextView = (TextView) rootView
                .findViewById(R.id.section_label);

        Recon_Button = (Button)rootView.findViewById(R.id.Recon_Button);

        /* change the reconnect button colour background */
        if (ACMainActivity.Get_Is_Connected()){
            /* put the background colour in green */
            Change_Reconnected_Button_BG(Color.GREEN);
        }else{
            /* put the background colour in red */
            Change_Reconnected_Button_BG(Color.RED);
        }

        /* take id for main switch */
        Main_Switch = (Switch)rootView.findViewById(R.id.sensor_switch);

        /* check main switch change */
        if (Main_Switch != null){
            Main_Switch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener)this);
        }

        return rootView;
    }

    /*************************************************************************/
    /*  Change_Reconnected_Button_BG                                         */
    /*************************************************************************/
    public static void Change_Reconnected_Button_BG(int Colour){

        if (Recon_Button != null) {
            Recon_Button.getBackground().setColorFilter(Colour, PorterDuff.Mode.MULTIPLY);
        }

    }

    /*************************************************************************/
    /*  onCheckedChanged                                                     */
    /*************************************************************************/
    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Main_Switch_State = isChecked;
    }

    /*************************************************************************/
    /*  Get_Main_Switch_State                                                */
    /*************************************************************************/
    public static boolean Get_Main_Switch_State(){
        return Main_Switch_State;
    }
}