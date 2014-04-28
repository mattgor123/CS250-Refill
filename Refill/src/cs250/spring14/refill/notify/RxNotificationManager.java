/**
 * 
 */
package cs250.spring14.refill.notify;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.db.RxDBAdapter;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

/**
 * This Receiver class is used for sending notification to phone
 * http://stackoverflow.com/questions/9930683/android-notification-at-specific-date
 */
public class RxNotificationManager extends BroadcastReceiver{
	   public static final String SEND_NOTIFICATION = "cs250.spring14.refill.NOTIFY"; 
	   private static final int WARNING_NOTIFICATION_ID = 99;
	   private static final int REFILL_NOTIFICATION_ID = 100;
	   public static final String countKey = "refill.numdays";
	   public static int numDays = 5;
		
	   @Override
	    public void onReceive(Context context, Intent intent) {
	       String action = intent.getAction();
	       SharedPreferences prefs;
	       prefs = context.getSharedPreferences("refill", Context.MODE_PRIVATE);
	       if (prefs!=null) {
	    	   numDays = prefs.getInt(countKey, 5);
	       }
	       if(SEND_NOTIFICATION.equals(action)) {
	           // here you call a service etc.
	    	   //make Notification
	    	   doNotificationLogic(context, numDays);
	    }
	 }
	   
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
		   for (RxItem r : rxs) {
			   Date d = r.getNextRefillDate();
			   Calendar myCal = Calendar.getInstance();
			   Calendar myCal2 = Calendar.getInstance();
			   myCal.setTime(d);
			   boolean sameDay = myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR) &&
		                  myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
			   if (sameDay) {
				   makeRefillAnnouncement(context, r);
			   }
			   myCal2.add(Calendar.DAY_OF_YEAR, numdays);
			   boolean shouldNotify = myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR) &&
		                  myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
			   if (shouldNotify) {
				   makeNotificationShouldUpdateRx(context, r);
			   }   
		   }
		   rxAdap.close();
	   }
	   
		public void makeNotificationShouldUpdateRx(Context c, RxItem r) {
			Intent resultIntent = new Intent(c, MainActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(c); //Null pointers for days?
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
	   
	   private void makeRefillAnnouncement(Context c, RxItem r) {
		   Intent resultIntent = new Intent(c, RxRefillActivity.class);
		   resultIntent.putExtra("id",r.getId());
		   resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
			stackBuilder.addParentStack(RxRefillActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(c); //Null pointers for days?
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