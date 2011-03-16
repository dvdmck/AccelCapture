package com.help4mood.AccelCapture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class AccelCapture extends Activity implements SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
    private int escrito = 0;

    private String linea;
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;
    
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    
    
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
     // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        
        // Check if there is a Bluetooth adapter
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
        	if (!mBluetoothAdapter.isEnabled()){
        		mBluetoothAdapter.enable();
        	}
        }
               
        setContentView(R.layout.main);
              
    }
    
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        if (escrito<100){
        	Date fecha = new Date();
        	linea= fecha.toString();
        	linea +=", X:" + event.values[0] + " ,Y:" + event.values[1] + " ,Z:"+ event.values[2] + "\n";
        	setStatus(linea);
        	if (mExternalStorageAvailable && mExternalStorageWriteable){
        		//File file = new File(getExternalFilesDir(null), "basura.txt"); //Solo en API 8
        		File file = new File("/sdcard", "H4Mdata.txt");
        		
    		    try {
    		        // Note that if external storage is
    		        // not currently mounted this will silently fail.
    		        /*
    		        OutputStream os = new FileOutputStream(file);
    		        os.write(linea.getBytes());
    		        os.
    		        os.close();
    		        */
    		    		    	
    		    	FileWriter salida = new FileWriter(file, true);
    		    	salida.write(linea);
    		    	salida.close();
    		    } catch (IOException e) {
    		        // Unable to create file, likely because external storage is
    		        // not currently mounted.
    		        Log.w("ExternalStorage", "Error writing "+ file, e);		        
    		    }
        	}

        	escrito++;
        }
        
    }
    
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
     // Check if there is a Bluetooth adapter
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
        	if (!mBluetoothAdapter.isEnabled()){
        		mBluetoothAdapter.enable();
        	}
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
     // Check if there is a Bluetooth adapter
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
        	if (mBluetoothAdapter.isEnabled()){
        		mBluetoothAdapter.disable();
        	}
        }
        
    }

    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    
    private TextView statusField;
    
    private void setStatus(String string) {
        if (statusField == null) {
            statusField = (TextView) findViewById(R.id.textview);
        }
        String current = (String) statusField.getText();
        current = string + "\n" + current;
        // don't let it get too long
        /*if (current.length() > 1500) {
            int truncPoint = current.lastIndexOf("\n");
            current = (String) current.subSequence(0, truncPoint);
        }*/
        statusField.setText(current);
    }
    	
}    
    
