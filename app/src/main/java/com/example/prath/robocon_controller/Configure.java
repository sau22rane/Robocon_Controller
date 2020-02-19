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
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class Configure extends AppCompatActivity {


    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    String TAG = "Configure";
    int usbcount;
    String usb_val;
    TextView USB_STAT;
    private ProgressDialog progress;
    private boolean isBtConnected = false;
    public double Y=0.0, Z=0.0;
    final static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public int Hcount=0;
    public int Lcount=0;
    public int county=0;
    public int countz=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        USB_STAT = (TextView) findViewById(R.id.usb_status);

        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            address = bundle2.getString("Address");
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
    }

    @Override
    public void onStop()
    {
        super.onStop();
        sendDataToPairedDevice("S");


    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            Toast.makeText(this, "WARNING! YOU PRESSED THE HOME BUTTON!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        /*if(HPWM==0)
        {

                sendDataToPairedDevice("l");

        }
        else if(HPWM==1)
        {

                sendDataToPairedDevice("h");

        }*/




        if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)&&(event.getRepeatCount()==0)) {

            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("R1");
                /*inputChar.setText("d");

                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/
            }
            return true;
        }/*
        else if ((keyCode == KeyEvent.KEYCODE_HOME)) {
            event.startTracking();
            Toast.makeText(this, "WARNING! YOU PRESSED THE HOME BUTTON!", Toast.LENGTH_LONG).show();
            return true;
        }*/
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L1) && (event.getRepeatCount()==0)) {

            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("L1");
                /*inputChar.setText("c");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/


            }
            return true;

        }


        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R2) && (event.getRepeatCount()==0)) {


            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("R2");
            }
            return true;




        }

        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L2) && (event.getRepeatCount()==0)) {

            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("L2");
            }
            return true;
        }

        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_X) &&(event.getRepeatCount()==0)){
            event.startTracking();
            sendDataToPairedDevice("X");

            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)&&(event.getRepeatCount()==0)) {
            event.startTracking();
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)&&(event.getRepeatCount()==0)) {
            event.startTracking();
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP)&&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)&&(event.getRepeatCount()==0)) {
            event.startTracking();

            sendDataToPairedDevice("S");
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("A");

            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_Y) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("Y");
            return true;
        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_B) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("b");
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            sendDataToPairedDevice("h");
            return true;

        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            sendDataToPairedDevice("e");
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)  {
        if((keyCode==KeyEvent.KEYCODE_DPAD_LEFT))
        {
            event.startTracking();
            sendDataToPairedDevice("D_Left");
            /*inputChar.setText("S");

            Temp="S";
            tkey=0;
*/

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_UP))
        {
            event.startTracking();
            sendDataToPairedDevice("D_UP");
            /*inputChar.setText("S");

            Temp="S";
            tkey=0;*/

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_RIGHT))
        {
            event.startTracking();
            sendDataToPairedDevice("D_Right");
            /*inputChar.setText("S");

            Temp="S";
            tkey=0;*/


        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_DOWN))
        {
            event.startTracking();
            sendDataToPairedDevice("D_Down");
            /*inputChar.setText("S");

            Temp="S";
            tkey=0;*/


        }

        else if((keyCode==KeyEvent.KEYCODE_BUTTON_X))
        {

            event.startTracking();
            sendDataToPairedDevice("X");
            /*inputChar.setText("S");
            MODE.setText("MANUAL");


            Temp="S";
            tkey=0;
*/

        }



        /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)) {
            event.startTracking();
            sendDataToPairedDevice("R1");
            /*inputChar.setText("S");*/



            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L1)) {
            event.startTracking();
            sendDataToPairedDevice("L1");
            //inputChar.setText("S");


            return true;

        }
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_B))
        {
            event.startTracking();
            sendDataToPairedDevice("B");
            /*inputChar.setText("S");
            DIREC.setText("STOP");*/

            /*if(tkey==1) {
                sendDataToPairedDevice(Temp);

                if(Temp=="F") {
                }
                else if(Temp=="B") {
                }




                else if(Temp=="L") {
                }
                else if(Temp=="R") {
                }


            }*/

            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_Y)) {
            event.startTracking();

            sendDataToPairedDevice("Y");
            /*inputChar.setText("S");
            DIREC.setText("STOP");*/

            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A)) {
            event.startTracking();
            sendDataToPairedDevice("A");
            /*inputChar.setText("S");
            DIREC.setText("STOP");*/
            return true;

        }


        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {





        Y=event.getAxisValue(MotionEvent.AXIS_BRAKE);
        Z=event.getAxisValue(MotionEvent.AXIS_GAS);

      /*  if(event.getActionButton()==MotionEvent.AXIS_BRAKE)
        {
        }
        if(event.getActionButton()==MotionEvent.AXIS_GAS)
        {
        }*/







        if ((Y>=0.5)) {
            //    sendDataToPairedDevice("y");
            county += 1;
            //HPWM=0;

            if(Lcount==0) {
                // sendDataToPairedDevice("l");
            }
            Lcount++;
            Hcount=0;

        }

        else if ((Z>=0.5)) {
            //    sendDataToPairedDevice("y");
            countz += 1;
            //HPWM=0;

            if(Lcount==0) {
                //sendDataToPairedDevice("l");
            }
            Lcount++;
            Hcount=0;

        }

        else
        {

            //HPWM=1;
            countz=0;
            if(Hcount==0) {
                //  sendDataToPairedDevice("h");
            }
            Hcount++;
            Lcount=0;

        }





        event.getSource();
        //inputChar.setText(event.toString());
        //inputChar.setTextSize(20);

        /*InputDevice.SOURCE_CLASS_JOYSTICK;
        MotionEvent.ACTION_BUTTON_PRESS;
        KeyEvent.KEYCODE_BUTTON_THUMBL;
        KeyEvent.KEYCODE_BUTTON_THUMBR;*/

        String prevchar="",newchar="S";
        if (KeyEvent.ACTION_DOWN ==InputDevice.SOURCE_UNKNOWN && event.getAction() == MotionEvent.ACTION_MOVE)

        {
            float A = event.getX();
            float B = event.getY();


            if((int)A==0 && (int)B==0)
            {
                newchar="S";
                /*DIREC.setText("STOP");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/

            }

            else if((int)A==0 && (int)B<=1 && (int)B>=0)
            {
                newchar="B";
                /*DIREC.setText("BACKWARD");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/


            }

            else if((int)A==0 && (int)B<=0 && (int)B>=-1)
            {

                newchar="F";
                /*DIREC.setText("FORWARD");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/

            }


            else if((int)B==0 && (int)A<=0 && (int)A>=-1)
            {
                newchar="L";
                /*DIREC.setText("LEFT");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/

            }

            else if((int)B==0 && (int)A<=1 && (int)A>=0)
            {
                newchar="R";
                /*DIREC.setText("RIGHT");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }*/

            }


            if(prevchar!=newchar)
            {
                //inputChar.setText(newchar);
                sendDataToPairedDevice(newchar);
                prevchar=newchar;
            }


            return true;
        }



        return super.onGenericMotionEvent(event);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;
        //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"Connecting");
            progress = ProgressDialog.show(getApplicationContext(), "Connecting...", "Please wait!");  //show a progress dialog
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

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {


            } else if ((BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))||(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))||(!myBluetooth.isEnabled())||(!btSocket.isConnected())) {

                isBtConnected=false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
                sendDataToPairedDevice("S");

            }

        }
    };

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                usbcount = 1;
                usb_val="USB CONNECTED";
                USB_STAT.setTextColor(Color.GREEN);
                USB_STAT.setText(usb_val);
                //new ConnectBT().execute();
                // connect_res.setText(usb_val+"\n"+bt_val);

            }
            else  if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                usbcount = 0;
                usb_val="USB DISCONNECTED";
                USB_STAT.setTextColor(Color.RED);
                USB_STAT.setText(usb_val);
                sendDataToPairedDevice("S");

                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //new ConnectBT().execute();

            }
        }
    };
}
