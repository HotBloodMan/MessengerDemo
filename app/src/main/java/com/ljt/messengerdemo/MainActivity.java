package com.ljt.messengerdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    private Messenger mService;//（服务端信使）
    //用于接受服务端消息的信使（客户端信使）
    private Messenger mGetReplayMessenger= new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.MSG_FROM_SERVICE:
                    Log.d(TAG,"客户端收到来自服务端的消息=== "+msg.getData().getString("replay"));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //实例化服务端信使
            mService=new Messenger(service);
            Log.d(TAG,"bind service");
            //第二个参数what，可以表明是来自客户端
            Message msg = Message.obtain(null, Constants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg","客户端：你好，服务端，最近忙吗");
            msg.setData(data);
            //说明来自客户端
            msg.replyTo=mGetReplayMessenger;
            try {
                //服务短信使发送
                mService.send(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
/*
*
* 服务一旦绑定成功，客户端先发送一个消息，服务端回复一个消息
* */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent("com.ljt.messenger.messengerservice");
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        //传conn对象 实质是只是与服务解绑，服务并没有销毁。
        unbindService(conn);
        super.onDestroy();
    }
}
