package cs250.spring14.refill.notify;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.db.RxDBAdapter;

/**
 * This Receiver class is used to send notification to phone every day at noon (if conditions are
 * met) Code adapted from
 * http://stackoverflow.com/questions/9930683/android-notification-at-specific-date
 */
public class RxNotificationManager extends BroadcastReceiver {
  public static final String SEND_NOTIFICATION = "cs250.spring14.refill.NOTIFY";
  private static final int WARNING_NOTIFICATION_ID = 99;
  private static final int REFILL_NOTIFICATION_ID = 100;
  public static final String countKey = "refill.numdays";
  public static int numDays = 5;

  /**
   * Method where we perform the logic that should be performed when our BroadCast receiver is hit
   * We update our numDays here to ensure that the logic is always performed correctly
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    SharedPreferences prefs;
    prefs = context.getSharedPreferences("refill", Context.MODE_PRIVATE);
    if (prefs != null) {
      numDays = prefs.getInt(countKey, 5);
    }
    if (SEND_NOTIFICATION.equals(action)) {
      // Here we do the logic to see if we should actually send a notification
      doNotificationLogic(context, numDays);
    }
  }

  /**
   * Method to perform the notification logic
   * 
   * @param context The context passed to onReceive and in turn this method
   * @param numdays The number of days before a Refill required to send the 'Refill soon'
   *        notification
   */
  private void doNotificationLogic(Context context, int numdays) {
    RxDBAdapter rxAdap = new RxDBAdapter(context);
    rxAdap.open();
    ArrayList<RxItem> rxs = new ArrayList<RxItem>();
    try {
      rxs = rxAdap.getAllRxs();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Iterate through our list of RX's
    for (RxItem r : rxs) {
      Date d = r.getNextRefillDate();
      Calendar myCal = Calendar.getInstance();
      Calendar myCal2 = Calendar.getInstance();
      myCal.setTime(d);
      // If the year & day are the same, it's the same day & we should ask user if they
      // would like us to help them contact the pharmacy
      boolean sameDay =
          myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR)
              && myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
      if (sameDay) {
        makeRefillAnnouncement(context, r);
      }
      // Now we see if we should send the 'you're running low' notification
      myCal2.add(Calendar.DAY_OF_YEAR, numdays);
      boolean shouldNotify =
          myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR)
              && myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
      if (shouldNotify) {
        makeNotificationShouldUpdateRx(context, r);
      }
    }
    rxAdap.close();
  }

  /**
   * Method to make the notification that they're running low
   * 
   * @param c the context passed by the doNotificationLogic method
   * @param r the rxitem for which we should notify the user
   */
  public void makeNotificationShouldUpdateRx(Context c, RxItem r) {
    Intent resultIntent = new Intent(c, MainActivity.class);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
    stackBuilder.addParentStack(MainActivity.class);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c);
    mBuilder.setSmallIcon(R.drawable.ic_notify);
    mBuilder.setContentTitle(r.getName() + " running low:");
    mBuilder.setContentText("On " + MainActivity.df.format(r.getNextRefillDate()) + " make sure you refill!");
    mBuilder.setAutoCancel(true);
    mBuilder.setLights(0xff00ff00, 300, 1000);
    mBuilder.setContentIntent(resultPendingIntent);
    int mNotificationId = WARNING_NOTIFICATION_ID;

    NotificationManager mNotifyManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotifyManager.notify(mNotificationId, mBuilder.build());
  }

  /**
   * Method to send the user a notification that they need to refill a certain prescription
   * 
   * @param c the context passed by the doNotificationLogic() method
   * @param r the RxItem that needs to be refilled
   */
  private void makeRefillAnnouncement(Context c, RxItem r) {
    Intent resultIntent = new Intent(c, RxRefillActivity.class);
    resultIntent.putExtra("id", r.getId());
    resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
    stackBuilder.addParentStack(RxRefillActivity.class);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(c);
    mBuilder.setSmallIcon(R.drawable.ic_notify);
    mBuilder.setContentTitle("Refill " + r.getName() + " today!");
    mBuilder.setContentText("Click to refill or dismiss this notification");
    mBuilder.setAutoCancel(true);
    mBuilder.setLights(0xffff0000, 300, 1000);
    mBuilder.setContentIntent(resultPendingIntent);
    int mNotificationId = REFILL_NOTIFICATION_ID;
    NotificationManager mNotifyManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotifyManager.notify(mNotificationId, mBuilder.build());
  }
}
