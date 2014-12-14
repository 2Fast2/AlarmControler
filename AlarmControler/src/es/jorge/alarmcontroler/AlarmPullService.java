package es.jorge.alarmcontroler;

import android.app.Service;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Jorge on 14/12/2014.
 *
 * http://developer.android.com/guide/components/services.html
 *
 */
public class AlarmPullService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final String SOCKET_ADDRESS = "AlarmConnection";


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

            int ret;

            /* create the server socket connection */
            LocalSocket receiver;
            LocalServerSocket server;

            try
            {
                server = new LocalServerSocket(SOCKET_ADDRESS);

               /* localServerCreated();*/
                receiver = server.accept();

                System.out.println("---------server.accept();------------- ");

                while ((ret = receiver.getInputStream().read()) != -1)
                {
                    System.out.println( "ret =" + ret);
                }

                System.out.println("ret =" + ret);

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

  /*  private void localServerCreated()
    {
        LocalSocket sender;
        try
        {
            sender = new LocalSocket();
            sender.connect(new LocalSocketAddress(SOCKET_ADDRESS));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("------mRecorder configured--------");
        try
        {

            System.out.println("------mRecorder.start()--------");
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }

    }
*/
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
}
