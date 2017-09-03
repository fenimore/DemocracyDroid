package com.workingagenda.democracydroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by fen on 3/15/17.
 */

public class MediaNotification extends Notification {
    private Context ctx;
    private NotificationManager mNotMng;

    public MediaNotification(Context ctx) {
        super();
        this.ctx = ctx;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotMng = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(ctx);
        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon=R.drawable.ic_mic_none_black_24dp;

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.notification);
        setListeners(contentView);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        mNotMng.notify(121212, notification);

    }

    public void setListeners(RemoteViews view) {

    }
}
