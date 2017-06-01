package hu.pe.remoiler.remoiler;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WifiActivity extends AppCompatActivity {

    EditText mSSIDEdit;
    EditText mPassEdit;
    Button mSaveButton;
    String mWifiSSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        mSSIDEdit = (EditText) findViewById(R.id.wifi_ssid);
        mPassEdit = (EditText) findViewById(R.id.wifi_password);
        mSaveButton = (Button) findViewById(R.id.wifi_save_button);

        mWifiSSID = NetworkUtils.getWifiSSID(this);
        switch (mWifiSSID) {
            case NetworkUtils.WIFI_ADAPTER_OFF:
                Toast.makeText(this, "Wi-Fi adapter is off, please turn on.", Toast.LENGTH_LONG).show();
                this.finish();
                break;
            case NetworkUtils.WIFI_NOT_CONNECTED:
                Toast.makeText(this, "Wi-Fi not connected to any access point, enter SSID manually.", Toast.LENGTH_LONG).show();
                break;
            default:
                mSSIDEdit.setText(mWifiSSID);
                break;
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send Packets to air with SSID&Pass
                // TODO: Send packets to air? Communicate with Bluetooth? Make Pi become an AccessPoint and then send it the wifi details?
                // TODO: Encrypt sent packets
                try {
                    DatagramSocket dgSocket = new DatagramSocket(NetworkUtils.WIFI_SOCKET_PORT);
                    String wifiSSID = mSSIDEdit.getText().toString();
                    String wifiPass = mPassEdit.getText().toString();
                    byte[] SSIDBytes = wifiSSID.getBytes(Charset.forName("UTF-8"));
                    byte[] PassBytes = wifiPass.getBytes(Charset.forName("UTF-8"));

                    //DatagramPacket dgPacket = new DatagramPacket();
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
