package com.image.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by mda on 12/15/13.
 */
public class UIMessage {

    public static void alert(Context context, int messageId){
        alert(context, context.getString(messageId));
    }

    public static void alert(Context context, CharSequence message) {
//        View layout = View.inflate(context, R.layout.toast, null);
//
//        TextView text = (TextView) layout.findViewById(R.id.text);
//        text.setText(message);

//        Toast toast = new Toast(context);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setView(layout);
        toast.show();
    }

    public static void info(Context context, int messageId){
        info(context, context.getString(messageId));
    }

    public static void info(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showFromBG(Context context, int messageId){
        showFromBG(context, context.getString(messageId));
    }

    public static void showFromBG(final Context ctx, final CharSequence message) {
        new Handler(ctx.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                alert(ctx, message);
            }
        });
    }
}
