package es.jorge.alarmcontroler;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Casa on 02/11/2014.

public class MainControlFragment {
}
*/

/**
 * A main control fragment
 */
public class MainControlFragment extends Fragment {

    static Button Recon_Button;

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

        Recon_Button = (Button)rootView.findViewById(R.id.Recon_Button);

        /* change the reconnect button colour background */
        if (ACMainActivity.Get_Is_Connected()){
            /* put the background colour in green */
            Change_Reconnected_Button_BG(Color.GREEN);
        }else{
            /* put the background colour in red */
            Change_Reconnected_Button_BG(Color.RED);
        }

        return rootView;
    }

    public static void Change_Reconnected_Button_BG(int Colour){

        Recon_Button.getBackground().setColorFilter(Colour, PorterDuff.Mode.MULTIPLY);

    }
}