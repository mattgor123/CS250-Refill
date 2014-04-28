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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * This Receiver class is used for sending notification to phone
 * http://stackoverflow.com/questions/9930683/android-notification-at-specific-date
 */
public class RxNotificationManager extends BroadcastReceiver{
	   public static final String SEND_NOTIFICATION = "cs250.spring14.refill.NOTIFY"; 

	   @Override
	    public void onReceive(Context context, Intent intent) {
	       String action = intent.getAction();
	       if(SEND_NOTIFICATION.equals(action)) {
	           // here you call a service etc.
	    	   //make Notification
	    	   checkIfNeedsNotification(context);
	    }
	 }
	   
	   private void checkIfNeedsNotification(Context context) {
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
			   myCal2.add(Calendar.DAY_OF_YEAR, 5);
			   boolean dayPlusOne = myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR) &&
		                  myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
			   if (dayPlusOne) {
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
				    mBuilder.setDefaults(Notification.DEFAULT_ALL);
				    	mBuilder.setContentIntent(resultPendingIntent);
			int mNotificationId = 100;
			
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
				    mBuilder.setContentIntent(resultPendingIntent);
			int mNotificationId = 99;
			
			NotificationManager mNotifyManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotifyManager.notify(mNotificationId, mBuilder.build());
		
		   
	   }
}