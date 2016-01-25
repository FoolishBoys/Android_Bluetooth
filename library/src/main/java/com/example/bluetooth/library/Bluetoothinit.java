package com.example.bluetooth.library;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.text.AndroidCharacter;
import android.widget.Toast;

/**
 * ＊
 * ＊
 * Created by wenxi on 16/1/24.
 */
public class Bluetoothinit {
    private BluetoothSPP bluetoothSPP;
    private Handler mhandler;
    private Activity mcontext;
    public Bluetoothinit(Activity context,Handler handler) {
        super();
        this.mcontext=context;
        this.mhandler=handler;
    }

    /**
     * 初始化蓝牙
     */

    public void initBluetooth(){

        //if Bluetooth is unable,finish
        //如果蓝牙不可用，结束当前activity
       if (!bluetoothSPP.isBluetoothEnabled()){
           Toast.makeText(mcontext.getApplicationContext()
                   , "Bluetooth is not available"
                   , Toast.LENGTH_SHORT).show();
           mcontext.finish();
       }
        bluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(mcontext.getApplicationContext()
                        , mcontext.getResources().getString(R.string.Connected_to) + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() {
                Toast.makeText(mcontext.getApplicationContext()
                        , mcontext.getResources().getString(R.string.Connection_lost), Toast.LENGTH_SHORT).show();
            }
            public void onDeviceConnectionFailed() {
                Toast.makeText(mcontext.getApplicationContext()
                        ,mcontext.getResources().getString(R.string.Unable_to_connect), Toast.LENGTH_SHORT).show();
            }
        });
        Bluetoothfindable();
    }
    /**
     * post data
     * 蓝牙发送数据
     * @param a 发送的字符串
     */
    public void send(String a){

        bluetoothSPP.send(a, true);

    }
    public void setisAndroid(boolean is){
        if (is){
          bluetoothSPP.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
        }else{
            bluetoothSPP.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        }
    }

    /**
     * 最长300S可见
     */
    public void Bluetoothfindable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //最长可见时间为300s
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        mcontext.startActivity(discoverableIntent);
    }

    public void connet(){
        if (bluetoothSPP.getServiceState() == BluetoothState.STATE_CONNECTED) {
            bluetoothSPP.disconnect();
        } else {
            Intent intent = new Intent(mcontext.getApplicationContext(), DeviceList.class);
            mcontext.startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }

    }

    public BluetoothSPP getBluetoothSPP(){
        return bluetoothSPP;
    }
    public void onstatrt(){
        bluetoothSPP=new BluetoothSPP(mcontext,mhandler);
        if (!bluetoothSPP.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mcontext.startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bluetoothSPP.isServiceAvailable()) {
                bluetoothSPP.setupService();
                bluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
            }
        }

    }

    public void Result(int requestCode, int resultCode, Intent data){
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                try {
                    bluetoothSPP.connect(data);
                    String address = data.getExtras().getString(BluetoothState.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                }catch (Exception e){
                    e.printStackTrace();
                }

        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bluetoothSPP.setupService();
                bluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }
}
