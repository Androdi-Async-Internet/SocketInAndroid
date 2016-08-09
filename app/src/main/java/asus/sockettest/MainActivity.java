package asus.sockettest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private SocketUtil socketUtil;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView);
        //开启服务器准备接受客户端传来的数据并显示到TextView上
        socketUtil = new SocketUtil(o-> tv.setText(Message.obj2Msg(o).name));
    }

    /**
     * 客户端Socket向服务器发送数据
     */
    public void send(View v){
        socketUtil.sendToServer(new Message("Hello, I am Client!"));
    }

}
