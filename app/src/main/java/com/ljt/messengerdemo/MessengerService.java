package com.ljt.messengerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private static String TAG="MessengerService";


    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
               case Constants.MSG_FROM_CLIENT:
                   Log.d(TAG, "服务端收到来自客户端的消息=== " + msg.getData().getString("msg"));
                    //声明一个客户端信使
                   Messenger client=msg.replyTo;
                   Message replayMessage = Message.obtain(null, Constants.MSG_FROM_SERVICE);
                   Bundle bundle = new Bundle();
                   bundle.putString("replay","服务端：最近很忙，一直在处理消息");
                   replayMessage.setData(bundle);
                   try {
                       //用客户端信使传消息
                       client.send(replayMessage);
                   }catch (RemoteException e){
                       e.printStackTrace();
                   }
                   break;
               default:
                   super.handleMessage(msg);
           }

        }
    }


    public MessengerService() {
    }
    //服务端信使
    private final Messenger mMessenger=new Messenger(new MessengerHandler());
    @Override
    public IBinder onBind(Intent intent) {

        return mMessenger.getBinder();
    }
}
