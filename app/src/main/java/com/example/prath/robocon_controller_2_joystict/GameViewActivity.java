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
import android.os.Build;
import android.os.CountDownTimer;
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
import java.util.UUID;

public class GameViewActivity extends AppCompatActivity {
    public int rackcount = 0;
    public int stopflag = 0;

    Thread con_fig;
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
    public double Y=0.0;
    public double Z=0.0;
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

    String Rnewchar = "S", Lnewchar = "S";

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
    Button configure;

    String prevchar="",newchar="S";

    IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);

    IntentFilter filter4 = new IntentFilter(ACTION_USB_ATTACHED);
    IntentFilter filter5 = new IntentFilter(ACTION_USB_DETACHED);


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
        configure = (Button)findViewById(R.id.To_Data);
        configure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                unregisterReceiver(mReceiver);
                unregisterReceiver(bReceiver);
                Intent i = new Intent(getApplicationContext(),Configure.class);
                /*getApplicationContext().unregisterReceiver(mReceiver);
                getApplicationContext().unregisterReceiver(bReceiver);*/
                Bundle bundle = new Bundle();
                bundle.putString("selected-item",address);
                i.putExtras(bundle);
                startActivity(i);
                registerReceiver(mReceiver, filter1);

                registerReceiver(mReceiver, filter2);
                registerReceiver(mReceiver, filter3);
                registerReceiver(bReceiver, filter4);
                registerReceiver(bReceiver, filter5);
                //finish();
            }
        });


    /*TVUSB.setText("USB: Disconnected");
    TVUSB.setTextColor(Color.rgb(255,0,0));*/
        ADDR.setText(address);
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
        unregisterReceiver(mReceiver);
        unregisterReceiver(bReceiver);
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


        else if ((keyCode == 105) && (event.getRepeatCount()==0)) {


            inputChar.setText("g");
            event.startTracking();
            sendDataToPairedDevice("g");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            return true;




        }

        else if ((keyCode == 104) && (event.getRepeatCount()==0)) {

            event.startTracking();
            sendDataToPairedDevice("p");
            inputChar.setText("p");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            return true;
        }

        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_X) &&(event.getRepeatCount()==0)){

            inputChar.setText("x");
            sendDataToPairedDevice("x");
            //con_fig.start();
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

            /*unregisterReceiver(mReceiver);
            unregisterReceiver(bReceiver);
            Intent i = new Intent(getApplicationContext(),Configure.class);
            i.putExtra("Address",address);
            startActivity(i);*/
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

            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BUTTON_A) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("a");
            inputChar.setText("a");
            tkey=0;
            if(timestatus==0) {
                timer.start();
                timestatus = 1;
            }
            return true;
        }   else if ((keyCode == KeyEvent.KEYCODE_BUTTON_Y) &&(event.getRepeatCount()==0)) {
            event.startTracking();
            sendDataToPairedDevice("y");
            inputChar.setText("y");            //Temp="Y";
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }

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
            return true;
        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP)
        {
            sendDataToPairedDevice("S");
            inputChar.setText("S");
            if(timestatus==0)
            {
                timer.start();
                timestatus=1;
            }
            return true;

        }
        else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            sendDataToPairedDevice("S");
            inputChar.setText("S");
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
            event.startTracking();
            inputChar.setText("S");
            sendDataToPairedDevice("S");
            //con_fig.start();
/*
            event.startTracking();
            inputChar.setText("S");
            MODE.setText("MANUAL");


            Temp="S";
            tkey=0;
*/
            /*unregisterReceiver(mReceiver);
            unregisterReceiver(bReceiver);
            Intent i = new Intent(getApplicationContext(),Configure.class);
            i.putExtra("Address",address);
            startActivity(i);*/

        }



        /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        else if ((keyCode == KeyEvent.KEYCODE_BUTTON_R1)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");



            return true;

        }
        else if ((keyCode == 105)) {
            event.startTracking();
            sendDataToPairedDevice("S");
            inputChar.setText("S");



            return true;

        }
        else if ((keyCode == 104)) {
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

        }



        return super.onKeyUp(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {





        String Q=event.getAxisValue(MotionEvent.AXIS_X)+"  "+event.getAxisValue(MotionEvent.AXIS_Y);
        String P=event.getAxisValue(MotionEvent.AXIS_Z)+" "+event.getAxisValue(MotionEvent.AXIS_RZ);

        SHAGAI_S.setText("A="+Q);
        SHAGAI_G.setText("B="+P);

        event.getSource();
        inputChar.setTextSize(10);

        /*InputDevice.SOURCE_CLASS_JOYSTICK;
        MotionEvent.ACTION_BUTTON_PRESS;
        KeyEvent.KEYCODE_BUTTON_THUMBL;
        KeyEvent.KEYCODE_BUTTON_THUMBR;*/

        if (KeyEvent.ACTION_DOWN ==InputDevice.SOURCE_UNKNOWN && event.getAction() == MotionEvent.ACTION_MOVE) {
            float A = event.getX();
            float B = event.getY();
            float AR = event.getAxisValue(MotionEvent.AXIS_Z);
            float BR = event.getAxisValue(MotionEvent.AXIS_RZ);


            Double angleR = Math.atan(AR/BR)*180/Math.PI+90;

            if(AR<0.01 && AR>-0.01 && BR<0.01 && BR>-0.01)
            {
                Rnewchar="S";
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }
            else {

                if (BR < 0) {

                    if (angleR > 18.5 && angleR < 71.5) {
                        Rnewchar = "W";
                        DIREC.setText("RIGHT-FORWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else if (angleR > 108.5 && angleR < 161.5) {
                        Rnewchar = "X";
                        DIREC.setText("LEFT-FORWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else{
                        Rnewchar="S";
                        if(timestatus==0)
                        {
                            timer.start();
                            timestatus=1;
                        }
                    }

                }

                if (BR > 0) {

                    if (angleR > 18.5 && angleR < 71.5) {
                        Rnewchar = "Y";
                        DIREC.setText("LEFT-BACKWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else if (angleR > 108.5 && angleR < 161.5) {
                        Rnewchar = "Z";
                        DIREC.setText("RIGHT-BACKWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else{
                        Rnewchar="S";
                        if(timestatus==0)
                        {
                            timer.start();
                            timestatus=1;
                        }
                    }

                }
                inputChar.setText(Rnewchar);


                if(prevchar!=Rnewchar)
                {
                    sendDataToPairedDevice(Rnewchar);
                    prevchar=Rnewchar;
                }


                return true;


            }




            Double angleL = Math.atan(A/B)*180/Math.PI+90;

            if(A<0.01 && A>-0.01 && B<0.01 && B>-0.01)
            {
                newchar="S";
                DIREC.setText("STOP");
                if(timestatus==0)
                {
                    timer.start();
                    timestatus=1;
                }

            }
            else {

                if (B < 0) {
                    if (angleL < 22.5) {
                        newchar = "R";
                        DIREC.setText("RIGHT");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }


                    else if (angleL > 67.5 && angleL < 112.5) {
                        newchar = "F";
                        DIREC.setText("FORWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }

                    else if (angleL > 157.5) {
                        newchar = "L";
                        DIREC.setText("LEFT");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else{
                        newchar="S";
                        if(timestatus==0)
                        {
                            timer.start();
                            timestatus=1;
                        }
                    }

                }

                if (B > 0) {
                    if (angleL < 22.5) {
                        newchar = "L";
                        DIREC.setText("Left");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }


                    else if (angleL > 67.5 && angleL < 112.5) {
                        newchar = "B";
                        DIREC.setText("BACKWARD");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else if (angleL > 157.5) {
                        newchar = "R";
                        DIREC.setText("RIGHT");
                        if (timestatus == 0) {
                            timer.start();
                            timestatus = 1;
                        }
                    }
                    else{
                        newchar="S";
                        if(timestatus==0)
                        {
                            timer.start();
                            timestatus=1;
                        }
                    }

                }



            }

            //inputChar.setText(""+Y+" "+Z);

            inputChar.setText(newchar);


            if(prevchar!=newchar)
            {
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




}
