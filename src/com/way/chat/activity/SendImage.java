package com.way.chat.activity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;

public class SendImage extends Activity{
    static final String OUT_IP = "127.0.0.1";
    static final int OUT_PORT = 8082;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	
        try {
            Socket socket = new Socket(OUT_IP, OUT_PORT);
                FileInputStream fis = new FileInputStream("C://test1.jpg"); //Size = 39923
                    byte buffer[] = new byte[fis.available()];
                    fis.read(buffer);   //Read 0.jpg into buffer (byte array)
 
                    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.write(buffer);
                    out.flush();
                    out.close();
                fis.close();    //The received image will be only 0x8194 if forget to close!
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }//end of try
    }

}
