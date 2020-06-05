package com.example.githubtest;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.DBAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlueToothService extends Service {

    private static final String TAG="MainActivity";
    private List<BTConnection> list;
    private List<BTConnection> lastlist;
    private List<String> adresslist;
    private List<String> lastadresslist;
    DBAdapter dbAdapter;
    BluetoothAdapter btAdapt;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private Thread workThread;

    IntentFilter intent;

    @Override
    public void onCreate() {
        dbAdapter = new DBAdapter(this);
        dbAdapter.open();//启动数据库
        list=new ArrayList<>();
        lastlist=new ArrayList<>();
        adresslist=new ArrayList<>();
        lastadresslist = new ArrayList<>();
        if(!initData()) {stopSelf();return;}

        this.workThread = new Thread((ThreadGroup)null, this.backgroudWork, "WorkThread");
        this.workThread.start();
    }



    private boolean initData() {
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        // 注册Receiver来获取蓝牙设备相关的结果
        intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(searchDevices, intent);

        if (btAdapt == null) {
            Toast.makeText(getBaseContext(), "您的机器上没有发现蓝牙适配器，本程序将不能运行!", Toast.LENGTH_SHORT).show();
            HomeFragment.UpdateGUI();
            this.stopSelf();
            return false;
        }

        if (btAdapt.getState() != BluetoothAdapter.STATE_ON)
        {// 如果蓝牙还没开启
            Toast.makeText(getBaseContext(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            HomeFragment.UpdateGUI();
            this.stopSelf();
            return false;
        }

        //addPairedDevice();
        return  true;
    }

    private Runnable backgroudWork = new Runnable() {
        public void run() {
            while(true) {
                try {
                    if (!Thread.interrupted()) {
                        lastadresslist=adresslist;
                        lastlist=list;
                        adresslist=new ArrayList<>();
                        list = new ArrayList<>();
                    btAdapt.startDiscovery();
                        Thread.sleep(30*1000L);
                        continue;
                    }
                } catch (InterruptedException var3) {
                    var3.printStackTrace();
                }

                return;
            }
        }
    };


    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();


            if (action.equals(BluetoothDevice.ACTION_FOUND)) { //found device
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String adress = device.getAddress();
                Log.e(TAG, "onReceive str: "+adress );
                if (adresslist.indexOf(adress)==-1)
                {
                    Date date = new Date();
                    adresslist.add(adress);
                    if(lastadresslist.indexOf(adress)==-1)
                    {
                        BTConnection bt = new BTConnection(date,adress);
                        bt.ID = dbAdapter.insertBTConnection(bt);
                        list.add(bt);
                        Toast.makeText(getBaseContext(),date.toString()+"  "+adress, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        for(BTConnection bt:lastlist)
                        {
                            if(bt.MAC_address.equals(adress))
                            {
                                BTConnection newbt =dbAdapter.queryBTConnectionByID(bt.ID)[0];
                                newbt.duration += date.getTime()-newbt.datetime.getTime();
                                dbAdapter.updateBTConnection(newbt.ID,newbt);
                                newbt.datetime=date;
                                list.add(newbt);
                            }
                        }
                    }
                }

            }
        }
    };
    // 增加配对设备
//    private void addPairedDevice() {
//        Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                String str = device.getName() + "|" + device.getAddress() + "\n";
//                list.add(str);
//                displayview.setText(list.toString());
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(searchDevices);
        if(workThread!=null) this.workThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) { return null;}
}