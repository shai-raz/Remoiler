package hu.pe.remoiler.remoiler;

import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class SwitchFragment extends Fragment {

    // LOG TAG Constant
    private final String LOG_TAG = SwitchFragment.class.getSimpleName();

    // Constants that hold on/off src
    private final int SWITCH_ON_BUTTON = R.drawable.switch_on_button;
    private final int SWITCH_OFF_BUTTON = R.drawable.switch_off_button;

    // Constants that hold on/off status int
    private final int SWITCH_STATUS_OFF = 0;
    private final int SWITCH_STATUS_ON = 1;

    // Status within the app, initiated to 0 (OFF)
    private int status = 0;

    // Switch Button ImageView
    private ImageView switchButton;

    Vibrator vibe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_switch, container, false);

        switchButton = (ImageView) rootView.findViewById(R.id.switch_button);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        // TODO: Get current status from server and set status accordingly.

        setButtonByStatus();

        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStatus();
                vibe.vibrate(20);
                setButtonByStatus();
            }
        });

        return rootView;
    }

    private void changeStatus() {
        status = 1 - status;
        // TODO: update server with new status.
    }

    private void setButtonByStatus() {
        if (status == SWITCH_STATUS_ON)
            switchButton.setImageResource(SWITCH_ON_BUTTON);
        else
            switchButton.setImageResource(SWITCH_OFF_BUTTON);

        Toast.makeText(getActivity(), String.format(getResources().getString(R.string.status_change_msg), statusToText()), Toast.LENGTH_SHORT).show();
    }

    /**
     * Returns status as text ( 0 = OFF , 1 = ON )
     */
    private String statusToText() {
        String statusText;

        if (status == SWITCH_STATUS_OFF)
            statusText = getString(R.string.status_off);
        else
            statusText = getString(R.string.status_on);

        return statusText;

    }
}
