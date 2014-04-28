/**
 * 
 */
package cs250.spring14.refill.notify;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.db.RxDBAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.widget.Toast;

/**
 * This Receiver class is used for sending notification to phone
 * http://stackoverflow.com/questions/9930683/android-notification-at-specific-date
 */
public class RxNotificationManager extends BroadcastReceiver{
	   public static final String SEND_NOTIFICATION = "cs250.spring14.refill.NOTIFY"; 

	   @Override
	    public void onReceive(Context context, Intent intent) {
	        // TODO Auto-generated method stub
		  /* Time now = new Time();
	       now.setToNow();
	       String time = FileHandler.timeFormat(now); */
		   System.out.println("onReceive hit!");

	       String action = intent.getAction();
	       if(SEND_NOTIFICATION.equals(action)) {
	           // here you call a service etc.
	    	   //make Notification
	    	   checkIfNeedsNotification(context);
	    	   
	    	   Toast.makeText(context, "RECEIVED NOTIFTIC", Toast.LENGTH_SHORT).show();
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
				   makeRefillAnnouncement(r);
			   }
			   myCal2.add(Calendar.DAY_OF_YEAR, 5);
			   boolean dayPlusOne = myCal.get(Calendar.YEAR) == myCal2.get(Calendar.YEAR) &&
		                  myCal.get(Calendar.DAY_OF_YEAR) == myCal2.get(Calendar.DAY_OF_YEAR);
			   if (dayPlusOne) {
				   makeNotificationShouldUpdateRx(r);
			   }   
		   }
		   rxAdap.close();
	   }
	   
	   private void makeNotificationShouldUpdateRx(RxItem r) {
		   System.out.println("Get your " + r.getName());
	   }
	   
	   private void makeRefillAnnouncement(RxItem r) {
		   System.out.println("Want to refill your " + r.getName());
	   }
}