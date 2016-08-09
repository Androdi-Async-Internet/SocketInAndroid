package asus.sockettest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by asus on 2016/8/9.
 */
public class SocketUtil extends Activity{
    public Socket socket;
    private ServerSocket serverSocket;
    private GetClientObj getObj;
    private Util util;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            getObj.get(msg.obj);
        }
    };

    public SocketUtil(final GetClientObj getObj){
        try {
            this.getObj = getObj;
            util = new Util();
            serverSocket = new ServerSocket(6666);
            new Thread(new Runnable() {
                public InputStream is;
                @Override
                public void run() {
                    try {
                        while (true){
                            socket = serverSocket.accept();
                            Log.d("Test", "连接成功！");
                            is = socket.getInputStream();
                            byte[] bytes = new byte[1000];
                            while((is.read(bytes, 0, bytes.length))!=0){
                                Message message = Message.obtain();
                                message.obj = util.ByteToObject(bytes);
                                handler.sendMessage(message);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public <T> void sendToServer(final T t){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("127.0.0.1", 6666);
                    doSend(t, socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                doSend(t, s);
//            }
//        }).start();

    }

    private <T> void doSend(T t, Socket s) {
        OutputStream os = null;
        try {
            os = s.getOutputStream();
            //os.write("HelloSocket".getBytes());
            os.write(util.ObjectToByte(t));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(os!=null)
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface GetClientObj<T>{
        void get(T t);
    }

    public class Util {

        public <T> byte[] ObjectToByte(T obj) {
            byte[] bytes = null;
            try {
                // object to bytearray
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(bo);
                oo.writeObject(obj);

                bytes = bo.toByteArray();

                bo.close();
                oo.close();
            } catch (Exception e) {
                Log.d("Test", "translation" + e.getMessage());
                e.printStackTrace();
            }
            return bytes;
        }

        public Object ByteToObject(byte[] bytes) {
            Object obj = null;
            try {
                // bytearray to object
                ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
                ObjectInputStream oi = new ObjectInputStream(bi);

                obj = oi.readObject();
                bi.close();
                oi.close();
            } catch (Exception e) {
                System.out.println("translation" + e.getMessage());
                e.printStackTrace();
            }
            return obj;
        }

    }

    class NetWorkUtils {

        /**
         * 检查网络是否可用
         */
        public boolean checkEnable(Context paramContext) {
            boolean i = false;
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                    .getSystemService("connectivity")).getActiveNetworkInfo();
            if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
                return true;
            return false;
        }

        /**
         * 将ip的整数形式转换成ip形式
         */
        public String int2ip(int ipInt) {
            StringBuilder sb = new StringBuilder();
            sb.append(ipInt & 0xFF).append(".");
            sb.append((ipInt >> 8) & 0xFF).append(".");
            sb.append((ipInt >> 16) & 0xFF).append(".");
            sb.append((ipInt >> 24) & 0xFF);
            return sb.toString();
        }

        /**
         * 获取当前ip地址
         */
        public String getLocalIpAddress(Context context) {
            try {
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int i = wifiInfo.getIpAddress();
                return int2ip(i);
            } catch (Exception ex) {
                return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
            }
        }
    }

}


