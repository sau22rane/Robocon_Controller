package com.example.prath.robocon_controller_2_joystict;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class rescue extends AppCompatActivity {
    Button BA, BStop, BX, BY, BB, BU, BL, BR, BD,BPP, BPM, ResetBtn,BtnPWMToggle, L1,R1,L2,R2;
    TextView command, TVAD,  TVBTH, TVUSBCON, TVMin, TVSec;
    String address = null;
    long TIME=180000;
    String remTime="180000";
    String remTime1="180000";
    String joy_op = null;
    String TFlag="1";
    private ProgressDialog progress;
    private static final String ACTION_USB_ATTACHED  = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED  = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String op = "";
    String prev_op = "";
    public int flag=1;
    public int timestatus=0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue);


        command = (TextView) findViewById((R.id.textViewCommand));
        TVMin=(TextView) findViewById(R.id.textView4);
        TVSec=(TextView) findViewById(R.id.textView9);
        BA = (Button) findViewById((R.id.buttonA));
        BStop = (Button) findViewById((R.id.buttonStop));
        BB = (Button) findViewById((R.id.buttonB));

        L1=(Button) findViewById(R.id.buttonL1);
        R1=(Button) findViewById(R.id.buttonR1);
        L2=(Button) findViewById(R.id.buttonL2);
        R2=(Button) findViewById(R.id.buttonR2);
        R2=(Button) findViewById(R.id.buttonR2);
        ResetBtn=(Button) findViewById(R.id.buttonReset);

        BX = (Button) findViewById((R.id.buttonX));
        TVAD=(TextView)findViewById(R.id.textViewAD);
        BY = (Button) findViewById((R.id.buttonY));
        /*BL = (Button) findViewById((R.id.buttonL));
        BU = (Button) findViewById((R.id.buttonU));
        BR = (Button) findViewById((R.id.buttonR));
        BD = (Button) findViewById((R.id.buttonD));
        BPP = (Button) findViewById((R.id.buttonpp));
        BPM = (Button) findViewById((R.id.buttonpm));*/
        TVBTH=(TextView) findViewById(R.id.textViewBT);
        TVUSBCON=(TextView)findViewById(R.id.tvusb);

        /*TVUSBCON.setText("USB: Disconnected");
        TVUSBCON.setTextColor(Color.rgb(255,0,0));*/

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter1);
        this.registerReceiver(mReceiver, filter2);
        this.registerReceiver(mReceiver, filter3);




        /*IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_ATTACHED);
        filter.addAction(ACTION_USB_DETACHED);
        registerReceiver(bReceiver,filter);*/
        IntentFilter filter4 = new IntentFilter(ACTION_USB_ATTACHED);
        IntentFilter filter5 = new IntentFilter(ACTION_USB_DETACHED);
        this.registerReceiver(bReceiver,filter4);
        this.registerReceiver(bReceiver,filter5);


        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            address = bundle2.getString("ADDR");
            //remTime=bundle2.getString("time");

        }

        //TIME=Long.parseLong((remTime+""));



        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setFixedCenter(false);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength)
            {
                if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
                if (btSocket != null) {
                    try {
                        if(strength != 0) {
                            if (angle >= 67.5 && angle <= 112.5)
                                op = "F";
                            if (angle >= 247.5 && angle <=292.5)
                                op =  "B";
                            if(angle>157.5 && angle<202.5)
                                op = "L";
                            if(angle>337.5 || angle<22.5)
                                op = "R";
                            if (angle >= 22.5 && angle <= 67.5)
                                op = "W";
                            if (angle >= 112.5 && angle <= 157.5)
                                op = "X";
                            if (angle >= 202.5 && angle <= 247.5)
                                op = "Y";
                            if (angle >= 292.5 && angle <= 337.5)
                                op = "Z";
                        }
                        else
                            op =  "S";
                        if(!(prev_op.equals(op))) {
                            command.setText(op);
                            btSocket.getOutputStream().write(op.toString().getBytes());
                        }
                        prev_op = "" + op;
                        Log.d("Bt", op);
                    }
                    catch (IOException e) {
                        msg("ERROR!!!");
                    }
                }
            }
        });
        new ConnectBT().execute();
        TVAD.setText(address);
        addListenerOnButton();






    }


   /*@Override
    public void onBackPressed() {
        Log.d("TAG","BackPressed");
        //super.onBackPressed();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(rescue.this, GameViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("selected-item", address);
        bundle.putString("remainingtime", remTime1);
        bundle.putString("TF", "1");
        intent.putExtras(bundle);
        startActivity(intent);



    }*/





    public void onStop()
    {
        super.onStop();
        sendDataToPairedDevice("S");


    }

    public class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        @Override
        public void onFinish() {

            TVMin.setText("00");
            TVSec.setText("00");
            /*MediaPlayer ring = MediaPlayer.create(buttons.this, R.raw.laugh);
            ring.start();*/
        }

        @Override
        public void onTick(long millisUntilFinished) {
            TVSec.setText((((millisUntilFinished / 1000) % 60) + ""));
            TVMin.setText("0" + (millisUntilFinished / 1000 / 60) + "");
            remTime1=(millisUntilFinished)+"";

        }
    }
    MyCounter timer = new rescue.MyCounter(TIME, 1000);
    public void addListenerOnButton() {

        /*BtnClock.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                command.setText("c");
                sendDataToPairedDevice("c");
            }
          });*/
        L1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("c");
                    sendDataToPairedDevice("c");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });
        R1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("d");
                    sendDataToPairedDevice("d");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });
        L2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("p");
                    sendDataToPairedDevice("p");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });
        R2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("g");
                    sendDataToPairedDevice("g");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });









        BA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("a");
                    sendDataToPairedDevice("a");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });



        BB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("b");
                    sendDataToPairedDevice("b");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });


        BX.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("x");
                    sendDataToPairedDevice("x");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });



        BY.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("y");
                    sendDataToPairedDevice("y");
                } else if ( motionEvent.getAction() == MotionEvent.ACTION_UP ){
                    if(timestatus==0)
                    {
                        timer.start();
                        timestatus=1;
                    }
                    command.setText("S");
                    sendDataToPairedDevice("S");
                }

                return true;
            }
        });
       BStop.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               timer.cancel();
               TVMin.setText("03");
               TVSec.setText("00");
               command.setText("S");
               sendDataToPairedDevice("S");
           }
       });
        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timestatus=0;
                timer.cancel();
                TVMin.setText("03");
                TVSec.setText("00");
            }
        });
    }

    private void sendDataToPairedDevice(String command) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(command.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(rescue.this, "Connecting...", "Please Wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
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
                TVBTH.setText("BT: Disconnected");
                TVBTH.setTextColor(Color.rgb(255, 0, 0));
                isBtConnected=false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(1000);



            } else if(ConnectSuccess) {
                msg("Connected.");
                isBtConnected = true;
                TVBTH.setText("BT: Connected");
                TVBTH.setTextColor(Color.rgb(0, 255, 0));



            }
            progress.dismiss();
        }
    }

    private final BroadcastReceiver bReceiver= new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
            {
                TVUSBCON.setText("USB: Connected");
                TVUSBCON.setTextColor(Color.rgb(0,255,0));
            }
            if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
            {
                TVUSBCON.setText("USB: Disconnected");
                TVUSBCON.setTextColor(Color.rgb(255,0,0));
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                TVBTH.setText("BT: Connected");
                TVBTH.setTextColor(Color.rgb(0, 255, 0));

            } else if ((BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))||(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action))||(!myBluetooth.isEnabled())||(!btSocket.isConnected())) {
                TVBTH.setText("BT: Disconnected");
                TVBTH.setTextColor(Color.rgb(255, 0, 0));
                isBtConnected=false;
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
                sendDataToPairedDevice("S");

            }

        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(rescue.this, GameViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("selected-item", address);
            bundle.putString("remainingtime", remTime1);
            bundle.putString("TF", "1");
            intent.putExtras(bundle);
            startActivity(intent);
        }

        if((keyCode==KeyEvent.KEYCODE_VOLUME_UP)&&(event.getRepeatCount()==0))
        {
            event.startTracking();
            command.setText("l");
            sendDataToPairedDevice("l");
            return true;

        }
        else if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)&&(event.getRepeatCount()==0))
        {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_VOLUME_UP)&&(event.getRepeatCount()==0))
        {
            event.startTracking();
            command.setText("h");
            sendDataToPairedDevice("h");
            return true;

        }
        else if((keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)&&(event.getRepeatCount()==0))
        {
            event.startTracking();
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }


}
