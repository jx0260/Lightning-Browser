package net.chinaedu.screenrecorder;

import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by qinyun on 2015/12/2.
 */
public class ScreenRecorderExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Context myContext;
    private final Class<?> myActivityClass;

    public ScreenRecorderExceptionHandler(Context context, Class<?> c) {
        myContext = context;
        myActivityClass = c;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        if(myContext != null){
            myContext = null;
        }

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
//        Intent intent = new Intent(myContext, myActivityClass);
        String s = stackTrace.toString();
        Log.e("uncaughtException:", s);
        //you can use this String to know what caused the exception and in which Activity
//        intent.putExtra("uncaughtException", "Exception is: " + stackTrace.toString());
//        intent.putExtra("stacktrace", s);
//        intent.putExtra("state", AppContext.START_MODE_NORMAL);
//        myContext.startActivity(intent);
        //for restarting the Activity
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }
}
