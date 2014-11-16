package es.jorge.alarmcontroler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jorge on 02/11/2014.

public class SensorViewFragment {
}
*/

/**
 * A sensor view fragment
 */
public class SensorViewFragment extends Fragment {

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