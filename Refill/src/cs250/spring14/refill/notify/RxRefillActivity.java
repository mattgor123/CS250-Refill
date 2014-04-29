package cs250.spring14.refill.notify;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.core.Pharmacy;
import cs250.spring14.refill.core.RxItem;
import cs250.spring14.refill.db.HistoryDBAdapter;
import cs250.spring14.refill.db.RxDBAdapter;

/**
 * The Activity that will be started upon clicking on the 'Refill RxItem today!' notification
 */
public class RxRefillActivity extends Activity {

  /**
   * Method to create the activity
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    final Context ctx = this;
    // We start the intent with an ID for the Rx
    final long id = getIntent().getLongExtra("id", 500);
    setContentView(R.layout.activity_rx_refill);
    Button yes = (Button) findViewById(R.id.yes);
    final RxDBAdapter rxAdap;
    if (MainActivity.rxAdapter == null) {
      rxAdap = new RxDBAdapter(getApplicationContext());
    } else {
      rxAdap = MainActivity.rxAdapter;
    }
    final HistoryDBAdapter hisAdap;
    if (MainActivity.hAdapter == null) {
      hisAdap = new HistoryDBAdapter(getApplicationContext());
    } else {
      hisAdap = MainActivity.hAdapter;
    }
    rxAdap.open();
    // Here we use the ID we got from the extra. Will cause NullPointers
    // If the intent's extra was not populated correctly or the Rx has
    // been removed between the time a notification was created and
    // we try to refill it.
    final RxItem r = rxAdap.getRxFromRow(id);
    if (r == null) {
      rxAdap.close();
      finish();
    }
    // Let's help the user notify their pharmacy!
    yes.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        Date today = Calendar.getInstance().getTime();
        if (rxAdap.updateRxRefillDate(id, today)) {
          Toast.makeText(getApplicationContext(),
              "Successfully updated refill date to " + MainActivity.df.format(today), Toast.LENGTH_SHORT).show();
          hisAdap.open();
          hisAdap.insertHis(new HistoryItem(r.getName(),
              "Updated Last Refill date to " + MainActivity.df.format(today), "R"));
          hisAdap.close();
        } else {
          Toast.makeText(getApplicationContext(), "Something went wrong updating your RX, sorry!", Toast.LENGTH_SHORT)
              .show();
        }
        rxAdap.close();
        final Pharmacy ph = r.getPharmacy();
        // Now, let's ask them if they want to call or e-mail the
        // pharmacy, or neither.
        MainActivity.alertMessage(ctx, "Please select an action", "Would you like to Call or E-mail " + ph.getName()
            + "?", "Call",
        // Call
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                // Remove functionality must be added here
                if (ph != null) {
                  // Populate the Dialer with Phone #
                  Intent intent = new Intent(Intent.ACTION_DIAL);
                  intent.setData(Uri.parse("tel:" + ph.getPhone()));
                  startActivity(intent);
                  finish();
                }
              }
            }, "E-mail",
            // Edit
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if (ph != null) {
                  // Populate the E-mail intent with E-mail
                  // address
                  Toast.makeText(getApplicationContext(), "Emailing " + ph.getName(), Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent(Intent.ACTION_SENDTO);
                  String uriText;
                  if (r.getRxNumb().equals(MainActivity.DEFAULT_RX_NUMBER)) {
                    uriText =
                        "mailto:" + Uri.encode(ph.getEmail()) + "?subject="
                            + Uri.encode("About refilling my " + r.getName() + " Prescription") + "&body="
                            + Uri.encode("Dear " + ph.getName() + ",\n\n");
                  } else {
                    uriText =
                        "mailto:" + Uri.encode(ph.getEmail()) + "?subject="
                            + Uri.encode("About refilling Rx " + r.getName() + "-#" + r.getRxNumb()) + "&body="
                            + Uri.encode("Dear " + ph.getName() + ",\n\n");
                  }
                  intent.setData(Uri.parse(uriText));
                  startActivity(intent);
                  finish();
                }
              }
            });
      }
    });
    Button no = (Button) findViewById(R.id.no);
    // Increment nextRefillDate by 1 by adding 1 to the lastRefillDate of the RX
    no.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        rxAdap.open();
        Date lrd = r.getLastRefill();
        Calendar cal = Calendar.getInstance();
        cal.setTime(lrd);
        cal.add(Calendar.DATE, 1);
        Date updated = cal.getTime();
        if (rxAdap.updateRxRefillDate(id, updated)) {
          Toast.makeText(getApplicationContext(), "Incremented next refill by one", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(getApplicationContext(), "Something went wrong updating your RX, sorry!", Toast.LENGTH_SHORT)
              .show();
        }
        rxAdap.close();
        if (MainActivity.getInstance() == null) {
          Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
          startActivity(resultIntent);
        } else {
          Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
          resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(resultIntent);
        }
        finish();
      }
    });
  }
}
