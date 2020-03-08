package com.example.prath.robocon_controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class Configure extends AppCompatActivity {


    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    String TAG = "Configure";
    int usbcount;
    String usb_val;
    ArrayList<TextView> parameters = new ArrayList<>();
    InputStream mmInStream = null;
    EditText  A_Parameter,B_Parameter,C_Parameter;
    TextView A_par_v,B_par_v,C_par_v,ID;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    public double Y=0.0, Z=0.0;
    final static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public int Hcount=0, Lcount=0, county=0, countz=0, par_loc = 0, id = 0;
    private boolean stopThread = false;
    private byte[] mmBuffer;

    int index = 0;
    int sensor_id = 0;
    Button send;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        A_Parameter = (EditText) findViewById(R.id.a_par);
        A_Parameter.setBackgroundColor(getColor(R.color.par_highlighted));
        B_Parameter = (EditText) findViewById(R.id.b_par);
        C_Parameter = (EditText) findViewById(R.id.c_par);
        A_par_v = (TextView) findViewById(R.id.a_par_v);
        A_par_v.setBackgroundColor(getColor(R.color.par_highlighted));
        B_par_v = (TextView) findViewById(R.id.b_par_v);
        C_par_v = (TextView) findViewById(R.id.c_par_v);
        ID = (TextView) findViewById(R.id.id_par);
        parameters.add(A_Parameter);
        parameters.add(B_Parameter);
        parameters.add(C_Parameter);
        parameters.add(ID);
        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            address = bundle2.getString("selected-item");
        }

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);

        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);

        new Configure.ConnectBT().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (btSocket!=null){
                        btSocket.getOutputStream().flush();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 300);

        IntentFilter filter4 = new IntentFilter(ACTION_USB_ATTACHED);
        IntentFilter filter5 = new IntentFilter(ACTION_USB_DETACHED);
        this.registerReceiver(bReceiver, filter4);
        this.registerReceiver(bReceiver, filter5);
        send = (Button)findViewById(R.id.send_data);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = String.valueOf(A_Parameter.getText()) +','+ String.valueOf(B_Parameter.getText())+','+String.valueOf(C_Parameter.getText())+',';
                sendDataToPairedDevice(temp);
            }
        });


    }

    @Override
    public void onStop()
    {
        super.onStop();
        //sendDataToPairedDevice("S");


    }







    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
        //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"Connecting");
            progress = ProgressDialog.show(Configure.this, "Connecting...", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if ((!ConnectSuccess)||(!myBluetooth.isEnabled())) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                isBtConnected=false;

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //startActivity(new Intent(GameViewActivity.this,MainActivity.class));
                // Vibrate for 500 milliseconds
                v.vibrate(1000);



            } else if(ConnectSuccess) {
                Log.d(TAG,"Connected");
                msg("Connected.");
                isBtConnected = true;




            }
            progress.dismiss();
            sendDataToPairedDevice("k");
            beginListenForData();
        }


    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
    private void sendDataToPairedDevice(String command) {



        if (btSocket != null) {
            try {
                // msg("Sending " + command + "..." );

                btSocket.getOutputStream().write(command.toString().getBytes());
                Log.d(TAG,"Sent"+command);

            } catch (IOException e) {

                //finish();
                //msg("Error sending " + command);
            }
        }
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) { }
            else if ((BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))||(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))||(!myBluetooth.isEnabled())||(!btSocket.isConnected())) {

                isBtConnected=false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
                //sendDataToPairedDevice("S");

            }

        }
    };

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                usbcount = 1;
                usb_val="USB CONNECTED";/*
                USB_STAT.setTextColor(Color.GREEN);
                USB_STAT.setText(usb_val);*/
                //new ConnectBT().execute();
                // connect_res.setText(usb_val+"\n"+bt_val);

            }
            else  if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                usbcount = 0;
                usb_val="USB DISCONNECTED";
                /*
                USB_STAT.setTextColor(Color.RED);
                USB_STAT.setText(usb_val);*/
                //sendDataToPairedDevice("S");

                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //new ConnectBT().execute();

            }
        }
    };

    void beginListenForData() {
        final Handler handler = new Handler();//declaration of handler to pass the data from this thread to UI thread
        stopThread = false;
        mmBuffer = new byte[1024];

        Thread thread  = new Thread(new Runnable() //new thread
        {
            public void run()
            {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {

                    try {
                        mmInStream = btSocket.getInputStream();// Instream to read the input data
                        mmBuffer = new byte[65536];//Initialization of mmBuffer
                        int numBytes;
                        if (mmInStream != null) {
                            final StringBuilder message = new StringBuilder(); // Declaring string builder to create a mutable string
                            numBytes = mmInStream.read(mmBuffer);

                            for (int i = 0; i < numBytes; i++) {
                                message.append(((char) Integer.parseInt(String.valueOf(mmBuffer[i]))));

                            }
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    String a = message.toString();//Typecasting of stringbuilder to string
                                    Log.d("Rane",a);
                                    String[] separated = a.split(",");//splitting of string
                                    if(separated.length==3) {
                                        String sensor1 = separated[0];
                                        A_par_v.setText(sensor1);
                                        String sensor2 = separated[1];
                                        B_par_v.setText(sensor2);
                                        String sensor3 = separated[2];
                                        C_par_v.setText(sensor3);
                                    }


                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Input stream null", Toast.LENGTH_SHORT).show();//toast
                        }


                    } catch (IOException e) {
                        Log.d(TAG, "Input stream was disconnected", e);//log
                        stopThread = true;
                        break;
                    }
                }

            }
        });

        thread.start();
    };
}
