package com.example.githubtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.DBAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlueToothService extends Service {

    public static final String CHANNEL_ID = "com.example.githubtest.BlueToothService";
    public static final String CHANNEL_NAME = "com.example.githubtest";
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

        registerNotificationChannel();
        int notifyId = (int) System.currentTimeMillis();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder
                //必须要有
                .setSmallIcon(R.mipmap.ic_launcher)
        //可选
        //.setSound(null)
        //.setVibrate(null)
        //...
        ;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mBuilder.setContentTitle(getResources().getString(R.string.app_name));
        }
        startForeground(notifyId, mBuilder.build());

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

    private void registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            if (notificationChannel == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                //是否在桌面icon右上角展示小红点
                channel.enableLights(true);
                //小红点颜色
                channel.setLightColor(Color.RED);
                //通知显示
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                //是否在久按桌面图标时显示此渠道的通知
                //channel.setShowBadge(true);
                mNotificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
   Log.d(TAG, "onStartCommand()");
    // 在API11之后构建Notification的方式
   Notification.Builder builder = new Notification.Builder
    (this.getApplicationContext()); //获取一个Notification构造器
    Intent nfIntent = new Intent(this, HomeActivity.class);

   builder.setContentIntent(PendingIntent.
                getActivity(this, 0, nfIntent, 0)) // 设置PendingIntent
    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
    .setContentTitle("蓝牙预警") // 设置下拉列表里的标题
        .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
        .setContentText("蓝牙扫描中") // 设置上下文内容
        .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,                    NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }

Notification notification = builder.build(); // 获取构建好的Notification
notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(110, notification);// 开始前台服务

        return super.onStartCommand(intent, flags, startId);
    }






    private boolean initData() {
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        // 注册Receiver来获取蓝牙设备相关的结果
        intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(searchDevices, intent);


        if(!testBlueTooth()) return false;
        //addPairedDevice();
        return  true;
    }

    private Boolean testBlueTooth()
    {
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

        return true;
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
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        unregisterReceiver(searchDevices);
        if(workThread!=null) this.workThread.interrupt();
    }

    @Override
    public IBinder onBind(Intent intent) { return null;}
}
