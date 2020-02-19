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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
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
import java.util.UUID;

import static android.util.LayoutDirection.RTL;

public class GameViewActivity extends AppCompatActivity {
    public int rackcount = 0;
    public int stopflag = 0;
    public int totalbuttonpress = 0;
    public int forcount = 0;
    public int backcount = 0;
    public int flag=0;
    String bt_val="",usb_val="";
    public  int rangeA=0;
    public  int rangeB=0;
    public int usbcount = 0;
    public int startbtncount = 0;
    String RT=null;
    long RTL=180000;
    public int AX=0;
    String TT;
    public int BX=0;
    public int p=0;
    private static final String TAG = "DetactUSB";
    String address = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    public int timestatus=0;

    private ProgressDialog progress;
    private boolean isBtConnected = false;
    EditText BASE, STEPF, STEPB;
    private static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    String BP, SPF, SPB;
    String XXX="";
    int B, SF, SB;
    int PWMFin = 0;
    public double Y=0.0, Z=0.0;
    String remTime="180000";
    String Rack = "Command";
    final static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //
    //e80c8ea5-57e0-4c8e-8fcc-71031880dd73
    public double angleR=0.0;
    public double angleD=0.0;
    public int r1=0, r2=0, r3=0, r4=0, r5=0;
    public int county=0;
    public int countz=0;
    public int gcount=0;
    public int ncount=0;
    //public int HPWM=0;
    public int Hcount=0;
    public int Lcount=0;
    public int shagai_sdisp=0;
    public int shagai_gdisp=0;
    public int gerege_disp=0;

    public String Temp="";
    public int tkey=0;

    //connect_res
    TextView inputChar;
    TextView USB_STAT;
    TextView BT_STAT;
    TextView MODE;
    TextView DIREC;
    TextView SHAGAI_S;
    TextView SHAGAI_G;
    TextView GEREGE;
    TextView ADDR;
    TextView MIN;
    TextView SEC;
    Button RECON;
    Button RESET;
    Button RESCUE;
    Button STOP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_game_view);


        //inputChar=findViewById(R.id.txt1);
        //connect_res=findViewById(R.id.connect_result);
        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            address = bundle2.getString("selected-item");
            RT=bundle2.getString("remainingtime");
            TT=bundle2.getString("TF");
        }

        RECON=findViewById(R.id.reconnect);
        RESET=findViewById(R.id.timeres);
        RESCUE=findViewById(R.id.rescue);
        STOP=findViewById(R.id.stop);
        inputChar=findViewById(R.id.btnpressdisp);
        USB_STAT=findViewById(R.id.textviewUSB);
        BT_STAT=findViewById(R.id.textViewBT);
        MODE=findViewById(R.id.mode);
        DIREC=findViewById(R.id.direction);
        ADDR=findViewById(R.id.textViewADDR);
        SHAGAI_G=findViewById(R.id.g);
        SHAGAI_S=findViewById(R.id.s);
        GEREGE=findViewById(R.id.gerege);
        MIN=findViewById(R.id.TVMin);
        SEC=findViewById(R.id.TVSec);



    /*TVUSB.setText("USB: Disconnected");
    TVUSB.setTextColor(Color.rgb(255,0,0));*/
        ADDR.setText(address);
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



RECON.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        new ConnectBT().execute();
    }
});

STOP.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        sendDataToPairedDevice("S");
        inputChar.setText("S");
    }
});
RESCUE.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(GameViewActivity.this, rescue.class);
        Bundle bundle = new Bundle();
        bundle.putString("ADDR", address);
        bundle.putString("time",remTime);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();


    }
});
RESET.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        timestatus=0;
        timer.cancel();
        MIN.setText("03");
        SEC.setText("00");

    }
});








        if (bundle2 != null) {
            address = bundle2.getString("selected-item");
            RT=bundle2.getString("remainingtime");
            TT=bundle2.getString("TF");
        }
        ADDR.setText(address);
        RTL=Long.parseLong(RT);








    }


    @Override
    public void onStop()
    {
     super.onStop();
        sendDataToPairedDevice("S");


    }

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
                shagai_gdisp=0;
                shagai_sdisp=0;
                gerege_disp=0;
                SHAGAI_G.setText("g="+shagai_gdisp);
                SHAGAI_S.setText("p="+shagai_sdisp);
                GEREGE.setText(""+gerege_disp);
                sendDataToPairedDevice("S");
                inputChar.setText("S");

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

    public class MyCounter extends CountDownTimer {


        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        @Override
        public void onFinish() {

            MIN.setText("00");
            SEC.setText("00");
           /* MediaPlayer ring = MediaPlayer.create(GameViewActivity.this, R.raw.laugh);
            ring.start();*/
        }

        @Override
        public void onTick(long millisUntilFinished) {
            SEC.setText((((millisUntilFinished / 1000) % 60) + ""));
            MIN.setText("0" + (millisUntilFinished / 1000 / 60) + "");
            remTime = (millisUntilFinished) + "";

        }
    }

    MyCounter timer = new MyCounter(RTL, 1000);


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
                sendDataToPairedDevice("d");
                inputChar.setText("d");

                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }
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
                sendDataToPairedDevice("c");
                inputChar.setText("c");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }


            }
            return true;

        }


        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R2) && (event.getRepeatCount()==0)) {


            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("g");
                inputChar.setText("g");
                shagai_sdisp=0;

                SHAGAI_S.setText("s="+shagai_sdisp);
                shagai_gdisp++;
                if(shagai_gdisp==1)
                {

                    SHAGAI_G.setText("g="+shagai_gdisp);
                }
                if(shagai_gdisp==2){

                    SHAGAI_G.setText("g="+shagai_gdisp);
                }
                if(shagai_gdisp==3)
                {

                    SHAGAI_G.setText("g="+shagai_gdisp);
                }
                if(shagai_gdisp==4)
                {

                    SHAGAI_G.setText("g="+shagai_gdisp);
                }
                if(shagai_gdisp==5)
                {
                    shagai_gdisp=1;
                    SHAGAI_G.setText("g="+shagai_gdisp);

                }

                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }
            return true;




        }

        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L2) && (event.getRepeatCount()==0)) {

            if (event.getRepeatCount() == 0) {
                event.startTracking();
                sendDataToPairedDevice("p");
                inputChar.setText("p");
                shagai_sdisp++;
                shagai_gdisp=0;
                SHAGAI_G.setText("g="+shagai_gdisp);

                if(shagai_sdisp==1)
                {
                    SHAGAI_S.setText("p="+shagai_sdisp);
                }
                if (shagai_sdisp==2)
                {
                    SHAGAI_S.setText("p="+shagai_sdisp);

                }
                if(shagai_sdisp==3)
                {
                    shagai_sdisp=1;
                    SHAGAI_S.setText("p="+shagai_sdisp);
                }
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }





            }
            return true;
        }

        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_X) &&(event.getRepeatCount()==0)){

            /*sendDataToPairedDevice("x");
            inputChar.setText("x");
            MODE.setText("AUTOMATIC");
            tkey=0;


            ncount+=1;

            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            // Temp="X";*/

            Intent i = new Intent(getApplicationContext(),Configure.class);
            i.putExtra("Address",address);
            startActivity(i);
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)&&(event.getRepeatCount()==0)) {
            event.startTracking();


            XXX = "S";
            sendDataToPairedDevice(XXX);
            inputChar.setText("S");

            Temp="S";
            tkey=1;
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }

           /* totalbuttonpress += 1;
            if ((totalbuttonpress == 1)) {
                timer.start();
            }
            stopflag = 0;
            */

            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_LEFT)&&(event.getRepeatCount()==0)) {
            event.startTracking();



            sendDataToPairedDevice("S");
            inputChar.setText("S");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }

           /* totalbuttonpress += 1;
            if ((totalbuttonpress == 1)) {
                timer.start();
            }
            stopflag = 0;
            */
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_UP)&&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");

            totalbuttonpress += 1;
            if ((totalbuttonpress == 1)) {
                timer.start();
            }
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            /*totalbuttonpress += 1;
            if ((totalbuttonpress == 1)) {
                timer.start();
           }
            stopflag = 0;
            */
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_DPAD_DOWN)&&(event.getRepeatCount()==0)) {
            event.startTracking();

            sendDataToPairedDevice("S");
            inputChar.setText("S");

            Temp="B";

            tkey=1;

            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            /*
            totalbuttonpress += 1;
            if ((totalbuttonpress == 1)) {

                timer.start();

            }

            stopflag = 0;
            */
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("a");
            inputChar.setText("a");
            tkey=0;
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            //Temp="A";


            gcount+=1;


            if(gcount%2==0)
            {
            }
            else if(gcount%2!=0)
            {
            }
            return true;
        }   else if ((keyCode == KeyEvent.KEYCODE_BUTTON_Y) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("y");
            inputChar.setText("y");            //Temp="Y";
            gerege_disp++;
            if (gerege_disp==1){
                GEREGE.setText(""+gerege_disp);
            }
            if(gerege_disp==2)
            {
                gerege_disp=0;
                GEREGE.setText(""+gerege_disp);


            }

            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            } /* ncount+=1;
                if(ncount%2==0)
                {
                }



                else if(ncount%2!=0)
                {
                }
*/
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_B) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("b");
            inputChar.setText("b");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
               /* sendDataToPairedDevice("c");
                //Temp="c";

                    tkey=0;*/
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            sendDataToPairedDevice("h");
            inputChar.setText("h");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            return true;

        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            sendDataToPairedDevice("e");
            inputChar.setText("e");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
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
            sendDataToPairedDevice("S");
            inputChar.setText("S");

            Temp="S";
            tkey=0;


        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_UP))
        {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");

            Temp="S";
            tkey=0;

        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_RIGHT))
        {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");

            Temp="S";
            tkey=0;


        }
        else if((keyCode==KeyEvent.KEYCODE_DPAD_DOWN))
        {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");

            Temp="S";
            tkey=0;


        }

        else if((keyCode==KeyEvent.KEYCODE_BUTTON_X))
        {
/*
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");
            MODE.setText("MANUAL");


            Temp="S";
            tkey=0;
*/
            Intent i = new Intent(getApplicationContext(),Configure.class);
            i.putExtra("Address",address);
            startActivity(i);

        }



        /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");



            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_L1)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");


            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_B))
        {






            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");
            DIREC.setText("STOP");

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

                sendDataToPairedDevice("S");
                inputChar.setText("S");
                DIREC.setText("STOP");

            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");
            DIREC.setText("STOP");
            return true;

        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_X)) {
            event.startTracking();
            if(tkey==1) {

        }
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
                DIREC.setText("STOP");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }

           else if((int)A==0 && (int)B<=1 && (int)B>=0)
            {
                newchar="B";
                DIREC.setText("BACKWARD");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }


            }

            else if((int)A==0 && (int)B<=0 && (int)B>=-1)
            {

                    newchar="F";
                    DIREC.setText("FORWARD");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }


            else if((int)B==0 && (int)A<=0 && (int)A>=-1)
            {
                newchar="L";
                DIREC.setText("LEFT");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }

            else if((int)B==0 && (int)A<=1 && (int)A>=0)
            {
                newchar="R";
                DIREC.setText("RIGHT");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }


            if(prevchar!=newchar)
            {
                inputChar.setText(newchar);
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
            progress = ProgressDialog.show(GameViewActivity.this, "Connecting...", "Please wait!");  //show a progress dialog
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
                bt_val="BLUETOOTH DISCONNECTED";
                BT_STAT.setTextColor(Color.RED);
               BT_STAT.setText(bt_val);
               shagai_gdisp=0;
               shagai_sdisp=0;
               gerege_disp=0;
              SHAGAI_G.setText("g="+shagai_gdisp);
               SHAGAI_S.setText("p="+shagai_sdisp);
               GEREGE.setText(""+gerege_disp);

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                //startActivity(new Intent(GameViewActivity.this,MainActivity.class));
                // Vibrate for 500 milliseconds
                v.vibrate(1000);



            } else if(ConnectSuccess) {
                Log.d(TAG,"Connected");
                msg("Connected.");
                bt_val="BLUETOOTH CONNECTED";
                BT_STAT.setTextColor(Color.GREEN);
                BT_STAT.setText(bt_val);
                isBtConnected = true;
               



            }
            progress.dismiss();
        }


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

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public int rangesetA(float RA) {

        if((RA>0.0)&&(RA<=20.0))
        {
            rangeA=50;
            r1+=1; r2=0; r3=0; r4=0; r5=0;

        }
        else if((RA>20.0)&&(RA<=40.0))
        {
            rangeA=100;
            r1=0; r2+=1; r3=0; r4=0; r5=0;
        }
        else if((RA>40.0)&&(RA<=60.0))
        {
            rangeA=150;
            r1=0; r2=0; r3+=1; r4=0; r5=0;
        }
        else if((RA>60.0)&&(RA<=80.0))
        {
            rangeA=200;
            r1=0; r2=0; r3=0; r4+=1; r5=0;
        }
        else if((RA>80.0)&&(RA<=100.0))
        {
            rangeA=250;
            r1=0; r2=0; r3=0; r4=0; r5+=1;
        }

        /*
        if((RA>0.0)&&(RA<=10.0))
        {
            rangeA=25;
        }
        else if((RA>10.0)&&(RA<=20.0))
        {
            rangeA=50;
        }
        else if((RA>20.0)&&(RA<=30.0))
        {
            rangeA=75;
        }
        else if((RA>30.0)&&(RA<=40.0))
        {
            rangeA=100;
        }
        else if((RA>40.0)&&(RA<=50.0))
        {
            rangeA=125;
        }
        else if((RA>50.0)&&(RA<=60.0))
        {
            rangeA=150;
        }
        else if((RA>60.0)&&(RA<=70.0))
        {
            rangeA=175;
        }
        else if((RA>70.0)&&(RA<=80.0))
        {
            rangeA=200;
        }
        else if((RA>80.0)&&(RA<=90.0))
        {
            rangeA=225;
        }
        else if((RA>90.0)&&(RA<=100.0))
        {
            rangeA=250;
        }
*/
        return rangeA;
    }

    public int rangesetB(float RB) {

        if((RB>0.0)&&(RB<=20.0))
        {
            rangeB=50;
            r1+=1; r2=0; r3=0; r4=0; r5=0;
        }
        else if((RB>20.0)&&(RB<=40.0))
        {
            rangeB=100;
            r1=0; r2+=1; r3=0; r4=0; r5=0;
        }
        else if((RB>40.0)&&(RB<=60.0))
        {
            rangeB=150;
            r1=0; r2=0; r3+=1; r4=0; r5=0;
        }
        else if((RB>60.0)&&(RB<=80.0))
        {
            rangeB=200;
            r1=0; r2=0; r3=0; r4+=1; r5=0;
        }
        else if((RB>80.0)&&(RB<=100.0))
        {
            rangeB=250;
            r1=0; r2=0; r3=0; r4=0; r5+=1;
        }



      /*  if((RB>0.0)&&(RB<=10.0))
        {
            rangeB=25;
        }
        else if((RB>10.0)&&(RB<=20.0))
        {
            rangeB=50;
        }
        else if((RB>20.0)&&(RB<=30.0))
        {
            rangeB=75;
        }
        else if((RB>30.0)&&(RB<=40.0))
        {
            rangeB=100;
        }
        else if((RB>40.0)&&(RB<=50.0))
        {
            rangeB=125;
        }
        else if((RB>50.0)&&(RB<=60.0))
        {
            rangeB=150;
        }
        else if((RB>60.0)&&(RB<=70.0))
        {
            rangeB=175;
        }
        else if((RB>70.0)&&(RB<=80.0))
        {
            rangeB=200;
        }
        else if((RB>80.0)&&(RB<=90.0))
        {
            rangeB=225;
        }
        else if((RB>90.0)&&(RB<=100.0))
        {
            rangeB=250;
        }
        */

        return rangeB;
    }



}
