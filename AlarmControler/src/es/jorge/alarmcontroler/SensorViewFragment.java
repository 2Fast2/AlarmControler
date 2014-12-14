package es.jorge.alarmcontroler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Jorge on 02/11/2014.

*/

/**
 * A sensor view fragment
 */
public class SensorViewFragment extends Fragment {

    static Switch Sensor_Switch;
    private static boolean Sensor_Switch_State = false;
    private static int Sensor_Number = 0;
    private static boolean Reset = false;
    private static SensorViewFragment frag;

    public SensorViewFragment() {}

    public static SensorViewFragment newInstance(int Sens_Num) {

        Sensor_Number = Sens_Num;
        frag = new SensorViewFragment();
        Bundle args = new Bundle();
        args.putInt("Sensor_Number", Sens_Num);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_acmain_sensor_view,
                container, false);
        TextView SensorViewTextView = (TextView) rootView
                .findViewById(R.id.section_label);

        Sensor_Switch = (Switch)rootView.findViewById(R.id.sensor_switch);

        Sensor_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Sensor_Switch_State = isChecked;

            }
        });


        return rootView;
    }

    /*************************************************************************/
    /*  onClick_Reset_Sensor_Button                                          */
    /*************************************************************************/
    public static void onClick_Reset_Sensor_Button(View view, int Tab_Number){

        Sensor_Number = Tab_Number;
        Reset = true;
        TX_Messages.Set_Msg_2(Sensor_Number, Sensor_Switch_State, Reset);
        Reset = false;

    }

    /*************************************************************************/
    /*  onClick_Reset_Sensor_Button                                          */
    /*************************************************************************/
    public static void onClick_Sensor_Power_Switch(View view, int Tab_Number){

        Sensor_Number = Tab_Number;
        TX_Messages.Set_Msg_2(Sensor_Number, Sensor_Switch_State, Reset);
        Reset = false; /* change to false the Reset value to send the correct
                          value several times */
    }

    /*************************************************************************/
    /*  Get_Sensor_Number                                                    */
    /*************************************************************************/
    public static int Get_Sensor_Number(){
        return Sensor_Number;
    }

    /*************************************************************************/
    /*  Get_Sensor_Switch_State                                              */
    /*************************************************************************/
    public static boolean Get_Sensor_Switch_State (){
        return Sensor_Switch_State;
    }

    /*************************************************************************/
    /*  Get_Sensor_Reset                                                     */
    /*************************************************************************/
    public static boolean Get_Sensor_Reset(){
        return Reset;
    }

}