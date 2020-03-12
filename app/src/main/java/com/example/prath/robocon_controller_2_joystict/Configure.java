package com.example.prath.robocon_controller_2_joystict;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

public class Configure extends AppCompatActivity {


    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    String TAG = "Configure";
    int usbcount;
    String usb_val;
    ArrayList<EditText> par = new ArrayList<>();
    InputStream mmInStream = null;
    EditText  p_parameter,i_parameter,d_parameter,ID;
    TextView A_par_v,B_par_v,d_par_v;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    public double Y=0.0, Z=0.0;
    final static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public int Hcount=0, Lcount=0, county=0, countz=0, par_loc = 0, id = 0;
    private boolean stopThread = false;
    private byte[] mmBuffer;


    int total_par = 3;
    int total_id = 4;
    int index = 0;

    ArrayList<Float>[] data = new ArrayList[total_id];
    ArrayList<Float> steps = new ArrayList<>();
    float p= (float) 0.001,i= (float) 0.001,d= (float) 0.001;
    Button send,get;
    Button inc,dec,sh_lft,sh_rgt;
    Button Exit;

    boolean a = true;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_configure);


        //inputChar=findViewById(R.id.txt1);
        //connect_res=findViewById(R.id.connect_result);
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




        new ConnectBT().execute();
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



    /*IntentFilter filter = new IntentFilter();
    filter.addAction(ACTION_USB_ATTACHED);
    filter.addAction(ACTION_USB_DETACHED);
    registerReceiver(bReceiver,filter);*/

        IntentFilter filter4 = new IntentFilter(ACTION_USB_ATTACHED);
        IntentFilter filter5 = new IntentFilter(ACTION_USB_DETACHED);
        this.registerReceiver(bReceiver, filter4);
        this.registerReceiver(bReceiver, filter5);
        send = (Button)findViewById(R.id.send_data);
        get = (Button)findViewById(R.id.get_data);
        sh_lft = (Button)findViewById(R.id.Shift_left);
        sh_rgt = (Button)findViewById(R.id.Shift_right);
        inc = (Button) findViewById(R.id.Inc_val);
        dec = (Button) findViewById(R.id.Dec_val);
        A_par_v = (TextView)findViewById(R.id.a_par_v);
        d_par_v = (TextView)findViewById(R.id.c_par_v);
        B_par_v = (TextView)findViewById(R.id.b_par_v);
        p_parameter = (EditText)findViewById(R.id.a_par);
        i_parameter = (EditText)findViewById(R.id.b_par);
        d_parameter = (EditText)findViewById(R.id.c_par);
        ID = (EditText)findViewById(R.id.id_par);
        Exit = (Button) findViewById(R.id.exit_back);
        par.add(p_parameter);
        par.add(i_parameter);
        par.add(d_parameter);
        par.add(ID);
        steps.add(p);
        steps.add(i);
        steps.add(d);
        for (int i = 0; i < total_id; i++) {
            data[i] = new ArrayList<Float>();
        }
        int j = 0;
        for(int i = 0;i<total_id;i++){
            for(j=0;j<total_par;j++){
                data[i].add(0.0f);
                Log.d("Rane"," "+data[i].get(0));
            }
        }
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendDataToPairedDevice("q");
                finish();
            }
        });
        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index<total_par) {
                    EditText temp = par.get(index);
                    float a = Float.parseFloat(temp.getText().toString());
                    a += steps.get(index);
                    DecimalFormat df = new DecimalFormat("#.000");
                    temp.setText(String.valueOf(df.format(a)));
                }
                else{
                    EditText temp = par.get(index);
                    int a = Integer.parseInt(temp.getText().toString()) +1;
                    if(a>total_id)
                        a=1;
                    temp.setText(String.valueOf(a));
                    int id;
                    String temp1 = par.get(total_par).getText().toString();
                    id = Integer.parseInt(temp1)-1;
                    String sensor1 = data[id].get(0).toString();
                    A_par_v.setText(sensor1);
                    p_parameter.setText(sensor1);
                    String sensor2 = data[id].get(1).toString();
                    B_par_v.setText(sensor2);
                    i_parameter.setText(sensor2);
                    String sensor3 = data[id].get(2).toString();
                    d_par_v.setText(sensor3);
                    d_parameter.setText(sensor3);
                    Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));
                }
            }
        });
        par.get(index).setBackgroundColor(getColor(R.color.par_highlighted));
        sh_rgt.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                EditText temp_1 = par.get(index);
                temp_1.setBackgroundColor(getColor(R.color.par_default));
                if(index<total_par){
                    index++;
                }
                else{
                    index =0;
                }
                temp_1 = par.get(index);
                temp_1.setBackgroundColor(getColor(R.color.par_highlighted));

            }
        });

        sh_lft.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                EditText temp_1 = par.get(index);
                temp_1.setBackgroundColor(getColor(R.color.par_default));
                if(index>0){
                    index--;
                }
                else{
                    index = total_par;
                }
                temp_1 = par.get(index);
                temp_1.setBackgroundColor(getColor(R.color.par_highlighted));
            }
        });


        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index<total_par) {
                    EditText temp = par.get(index);
                    float a = Float.parseFloat(temp.getText().toString());
                    a -= steps.get(index);
                    DecimalFormat df = new DecimalFormat("#.000");
                    temp.setText(String.valueOf(df.format(a)));
                }
                else{
                    EditText temp = par.get(index);
                    int a = Integer.parseInt(temp.getText().toString()) -1;
                    if(a<1)
                        a=total_id;
                    temp.setText(String.valueOf(a));
                    int id;
                    String temp1 = par.get(total_par).getText().toString();
                    id = Integer.parseInt(temp1)-1;
                    String sensor1 = data[id].get(0).toString();
                    A_par_v.setText(sensor1);
                    p_parameter.setText(sensor1);
                    String sensor2 = data[id].get(1).toString();
                    B_par_v.setText(sensor2);
                    i_parameter.setText(sensor2);
                    String sensor3 = data[id].get(2).toString();
                    d_par_v.setText(sensor3);
                    d_parameter.setText(sensor3);
                    Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDataToPairedDevice("m");
                String final_string = ""+p_parameter.getText().toString()+","+i_parameter.getText().toString()+","+d_parameter.getText().toString()+",";

                int id;
                String temp = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp)-1;
                //sendDataToPairedDevice(final_string);
                data[id].set(0,Float.parseFloat(p_parameter.getText().toString()));
                data[id].set(1,Float.parseFloat(i_parameter.getText().toString()));
                data[id].set(2,Float.parseFloat(d_parameter.getText().toString()));
                A_par_v.setText(p_parameter.getText().toString());
                B_par_v.setText(i_parameter.getText().toString());
                d_par_v.setText(d_parameter.getText().toString());
                //finish();
                sendDataToPairedDevice(final_string);
            }
        });
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(a) {
                    beginListenForData();
                    a = false;
                }
                int id;
                String temp = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp)-1;
                String sensor1 = data[id].get(0).toString();
                A_par_v.setText(sensor1);
                p_parameter.setText(sensor1);
                String sensor2 = data[id].get(1).toString();
                B_par_v.setText(sensor2);
                i_parameter.setText(sensor2);
                String sensor3 = data[id].get(2).toString();
                d_par_v.setText(sensor3);
                d_parameter.setText(sensor3);
                Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));
            }
        });


    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            Toast.makeText(this, "WARNING! YOU PRESSED THE HOME BUTTON!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)&&(event.getRepeatCount()==0)) {

            if (event.getRepeatCount() == 0) {

                event.startTracking();
                sendDataToPairedDevice("m");
                String final_string = ""+p_parameter.getText().toString()+","+i_parameter.getText().toString()+","+d_parameter.getText().toString()+",";

                int id;
                String temp = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp)-1;
                //sendDataToPairedDevice(final_string);
                data[id].set(0,Float.parseFloat(p_parameter.getText().toString()));
                data[id].set(1,Float.parseFloat(i_parameter.getText().toString()));
                data[id].set(2,Float.parseFloat(d_parameter.getText().toString()));
                A_par_v.setText(p_parameter.getText().toString());
                B_par_v.setText(i_parameter.getText().toString());
                d_par_v.setText(d_parameter.getText().toString());
                //finish();
                sendDataToPairedDevice(final_string);

                return true;
            }
        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L1) && (event.getRepeatCount() == 0)) {

            if (event.getRepeatCount() == 0) {
                event.startTracking();
                if(a) {
                    beginListenForData();
                    a = false;
                }
                int id;
                String temp = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp)-1;
                String sensor1 = data[id].get(0).toString();
                A_par_v.setText(sensor1);
                p_parameter.setText(sensor1);
                String sensor2 = data[id].get(1).toString();
                B_par_v.setText(sensor2);
                i_parameter.setText(sensor2);
                String sensor3 = data[id].get(2).toString();
                d_par_v.setText(sensor3);
                d_parameter.setText(sensor3);
                Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));

            }
            return true;

        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_X) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("q");
            finish();
            return true;
        }

        return true;

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_DPAD_LEFT))
        {
            event.startTracking();
            return true;

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_UP))
        {
            event.startTracking();
            return true;

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_RIGHT))
        {
            event.startTracking();
            return true;

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_DOWN))
        {
            event.startTracking();
            return true;
        }

        else if((keyCode==KeyEvent.KEYCODE_BUTTON_X))
        {
            event.startTracking();
            return true;
        }



        /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)) {
            event.startTracking();
            return true;

        }
        else if ((keyCode == 105)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            return true;

        }
        else if ((keyCode == 104)) {
            event.startTracking();

            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L1)) {
            event.startTracking();
            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_B))
        {
            event.startTracking();
            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_Y)) {
            event.startTracking();

            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A)) {
            event.startTracking();
            return true;

        }



        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        float x = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float y = event.getAxisValue(MotionEvent.AXIS_HAT_Y);

        if(y<-0.5){
            //inc
            if(index<total_par) {
                EditText temp = par.get(index);
                float a = Float.parseFloat(temp.getText().toString());
                a += steps.get(index);
                DecimalFormat df = new DecimalFormat("#.000");
                temp.setText(String.valueOf(df.format(a)));
            }
            else{
                EditText temp = par.get(index);
                int a = Integer.parseInt(temp.getText().toString()) +1;
                if(a>total_id)
                    a=1;
                temp.setText(String.valueOf(a));
                int id;
                String temp1 = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp1)-1;
                String sensor1 = data[id].get(0).toString();
                A_par_v.setText(sensor1);
                p_parameter.setText(sensor1);
                String sensor2 = data[id].get(1).toString();
                B_par_v.setText(sensor2);
                i_parameter.setText(sensor2);
                String sensor3 = data[id].get(2).toString();
                d_par_v.setText(sensor3);
                d_parameter.setText(sensor3);
                Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));
            }
        }
        if(y>0.5){
            //dec
            if(index<total_par) {
                EditText temp = par.get(index);
                float a = Float.parseFloat(temp.getText().toString());
                a -= steps.get(index);
                DecimalFormat df = new DecimalFormat("#.000");
                temp.setText(String.valueOf(df.format(a)));
            }
            else{
                EditText temp = par.get(index);
                int a = Integer.parseInt(temp.getText().toString()) -1;
                if(a<1)
                    a=total_id;
                temp.setText(String.valueOf(a));
                int id;
                String temp1 = par.get(total_par).getText().toString();
                id = Integer.parseInt(temp1)-1;
                String sensor1 = data[id].get(0).toString();
                A_par_v.setText(sensor1);
                p_parameter.setText(sensor1);
                String sensor2 = data[id].get(1).toString();
                B_par_v.setText(sensor2);
                i_parameter.setText(sensor2);
                String sensor3 = data[id].get(2).toString();
                d_par_v.setText(sensor3);
                d_parameter.setText(sensor3);
                Log.d("Rane",""+id+" "+data[id].get(0)+" "+data[id].get(1)+" "+data[id].get(2));
            }
        }
        if(x>0.5){
            //right
            EditText temp_1 = par.get(index);
            temp_1.setBackgroundColor(getColor(R.color.par_default));
            if(index<total_par){
                index++;
            }
            else{
                index =0;
            }
            temp_1 = par.get(index);
            temp_1.setBackgroundColor(getColor(R.color.par_highlighted));
        }
        if(x<-0.5){
            //left
            EditText temp_1 = par.get(index);
            temp_1.setBackgroundColor(getColor(R.color.par_default));
            if(index>0){
                index--;
            }
            else{
                index = total_par;
            }
            temp_1 = par.get(index);
            temp_1.setBackgroundColor(getColor(R.color.par_highlighted));
        }

        return true;
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
        }


    }
    private void msg(String s) {if(index<3) {
                    EditText temp = par.get(index);
                    float a = Float.parseFloat(temp.getText().toString());
                    a -= steps.get(index);
                    DecimalFormat df = new DecimalFormat("#.000");
                    temp.setText(String.valueOf(df.format(a)));
                }
                else{
                    EditText temp = par.get(index);
                    int a = Integer.parseInt(temp.getText().toString()) -1;
                    if(a<0)
                        a=3;
                    temp.setText(String.valueOf(a));
                }
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


    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                usbcount = 1;
                usb_val="USB CONNECTED";
                //new ConnectBT().execute();
                // connect_res.setText(usb_val+"\n"+bt_val);

            }
            else  if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                usbcount = 0;
                usb_val="USB DISCONNECTED";
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //new ConnectBT().execute();

            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {


            } else if ((BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))||(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))||(!myBluetooth.isEnabled())||(!btSocket.isConnected())) {

                isBtConnected=false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
                sendDataToPairedDevice("S");

            }

        }
    };



    void beginListenForData() {
        final Handler handler = new Handler();//declaration of handler to pass the data from this thread to UI thread
        stopThread = false;
        mmBuffer = new byte[1024];

        Thread thread  = new Thread(new Runnable() //new thread
        {
            public void run() {
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
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    String a = message.toString();//Typecasting of stringbuilder to string
                                    Log.d("Rane", a);
                                    String[] separated = a.split(",");//splitting of string
                                    int j = 0;
                                    for(int i = 0;i<total_id;i++){
                                        for(;j<total_par;j++){
                                            data[i].add(Float.parseFloat(separated[j]));
                                        }
                                    }



                                    if (separated.length == 3) {
                                        String sensor1 = separated[0];
                                        A_par_v.setText(sensor1);
                                        p_parameter.setText(sensor1);
                                        String sensor2 = separated[1];
                                        B_par_v.setText(sensor2);
                                        i_parameter.setText(sensor2);
                                        String sensor3 = separated[2];
                                        d_par_v.setText(sensor3);
                                        d_parameter.setText(sensor3);
                                    }


                                }
                            },500);


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
