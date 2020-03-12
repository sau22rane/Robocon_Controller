package com.example.prath.robocon_controller_2_joystict;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button b2, b3, b4,b1,b5;
    Switch s4;
    TextView mac_add;
    String TFlag="0";
    private BluetoothAdapter BA;
    String T="180000";
    String ABTH1="20:14:12:03:05:69";
    String ABTH2="98:D3:32:70:AE:2D";
    String ABTH3="98:D3:31:90:69:68";
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1=(Button) findViewById(R.id.buttongameview2);
        b2 = (Button) findViewById(R.id.button2);
        b3 = (Button) findViewById(R.id.button3);
        b4=(Button) findViewById(R.id.buttongameview);
        s4 = (Switch) findViewById(R.id.switch1);
        mac_add = (TextView) findViewById(R.id.bt_mac);
        BA = BluetoothAdapter.getDefaultAdapter();
        lv = (ListView) findViewById(R.id.listView);
       // b5=(Button)findViewById(R.id.buttongameview3);

        s4.setChecked(false);
        //tools:context="heisenbergapp.trfx.Controller"



        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, GameViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("selected-item", ABTH1);
                bundle.putString("remainingtime",T);
                bundle.putString("TF", "0");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, GameViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("selected-item", ABTH2);
                bundle.putString("remainingtime",T);
                bundle.putString("TF", "0");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });






        s4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {
                    if (!BA.isEnabled())//if Bluetooth Adapter is disable
                    {
                        Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnOn, 0);

                        Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_SHORT).show();

                        s4.setText("BLUETOOTH ON");
                        s4.setChecked(true);
                    }

                    else
                    {
                        s4.setText("BLUETOOTH ON");
                        Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    BA.disable();
                    Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_SHORT).show();
                    s4.setText("BLUETOOTH OFF");
                }

            }
        });
        Toast.makeText(this, "Inside controller app", Toast.LENGTH_SHORT).show();
    }


    public void visible(View v) {
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    public void list(View v) {
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for (BluetoothDevice bt : pairedDevices)
        {
            list.add(bt.getName()+"\n"+bt.getAddress());
        }
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(ListClickHandler);
    }

    public AdapterView.OnItemClickListener ListClickHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            RadioButton trfController, trfGyro;
            final RadioGroup radioGroup;

            radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
            trfController = (RadioButton) findViewById(R.id.radioController);
            trfGyro = (RadioButton) findViewById(R.id.radioGyro);

            String value = (String) parent.getItemAtPosition(position);
            int length = value.length();
            String mac_address = value.substring(length - 17, length);
            mac_add.setText(mac_address);
            Intent intent = new Intent(MainActivity.this, GameViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("selected-item", mac_address);
            bundle.putString("remainingtime",T);
            bundle.putString("TF", "0");
            intent.putExtras(bundle);
            startActivity(intent);

        }
    };
}
