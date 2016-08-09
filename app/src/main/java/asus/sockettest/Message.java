package asus.sockettest;

import java.io.Serializable;

/**
 * Created by asus on 2016/8/9.
 */
public class Message implements Serializable {
    public String name;
    public Message(String n){
        name = n;
    }

    public static Message obj2Msg(Object o){
        Message message = (Message) o;
        return message;
    }
}
