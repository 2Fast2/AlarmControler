package es.jorge.alarmcontroler;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Jorge on 14/12/2014.
 *
 * http://developer.android.com/guide/components/services.html
 *
 */
public class AlarmPullService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    static final int SocketServerPORT = 5001;
    Socket socket;
    /* input stream to read the input information */
    InputStream InStream;
    DataInputStream DataInStream;
    byte[] IncomingMsg = new byte[250];


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            /*long endTime = System.currentTimeMillis() + 5*1000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                        // in case of exception stop the service
                        stopSelf(msg.arg1);
                    }
                }
            }*/

            int ret = -1;

            /* create the server socket connection */
            ServerSocket serverSocket;

            /* Una dir donde pasan info de un servicio a una activity
             * http://android-coding.blogspot.in/2011/11/pass-data-from-service-to-activity.html
              *
              * */
            try
            {
                serverSocket = new ServerSocket(SocketServerPORT);

                /* Waits for an incoming request */
                socket = serverSocket.accept();

                InStream = socket.getInputStream();
                DataInStream = new DataInputStream(InStream);

                while ((ret = DataInStream.read(IncomingMsg)) != -1)
                {
                    System.out.println( "ret =" + ret);
                    System.out.println(IncomingMsg);
                    DecodeCommand(IncomingMsg, ret);

                }

            }
            catch (IOException e)
            {
                // in case of exception stop the service
                stopSelf(msg.arg1);
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }


    /* Decode received msg */
    private void DecodeCommand(byte[] Cmd, int Size)
    {
        byte[] AuxArray = new byte[50];
        int AuxArrayIndex = 0;
        int i = 0;
        int EoM = 0;
        byte Default = (byte)0xff;

        // Decode all the received mesg
        for (i=0; i < (Size +1); i++)
        {
            if(Cmd[i] != '/')
            {
                AuxArray[AuxArrayIndex] = Cmd[i];
            }else if (Cmd[i+1] == '/' && Cmd[i+2] == '/'){
                EoM = 1;
            }else{
                AuxArray[AuxArrayIndex] = '/';
            }
            AuxArrayIndex += 1;

            //if it is the end of the msg decode the command
            if (EoM == 1)
            {
                if (AuxArray[0] == '1')
                {
                    DecodeCmd1(AuxArray);
                    EoM = 0;
                    Arrays.fill(AuxArray, Default);
                     i += 2;
                    AuxArrayIndex = 0;
                }else if (AuxArray[0] == '2') {
                    DecodeCmd2(AuxArray);
                    EoM = 0;
                    Arrays.fill(AuxArray, Default);
                    i += 2;
                    AuxArrayIndex = 0;
                }else{
                    System.out.println("ERROR: Unknown command");
                }

            }
        }
    }

    private void DecodeCmd1(byte[] Cmd){}

    private void DecodeCmd2(byte[] Cmd){}

}
