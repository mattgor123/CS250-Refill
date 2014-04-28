package cs250.spring14.refill.notify;

import java.util.Calendar;
import java.util.Date;

import cs250.spring14.refill.MainActivity;
import cs250.spring14.refill.R;
import cs250.spring14.refill.db.RxDBAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class RxRefillActivity extends Activity {	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		final long id = getIntent().getLongExtra("id", 500);
		setContentView(R.layout.activity_rx_refill);
		Button yes = (Button) findViewById(R.id.yes);
		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RxDBAdapter rxAdap;
				if (MainActivity.rxAdapter == null) {
				 rxAdap = new RxDBAdapter(getApplicationContext());
				}
				else {
					rxAdap = MainActivity.rxAdapter;
				}
				rxAdap.open();
				Date today = Calendar.getInstance().getTime();
				if (rxAdap.updateRxRefillDate(id,today)) {
					Toast.makeText(getApplicationContext(), "Successfully updated refill date to " + MainActivity.df.format(today), Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "Something went wrong updating your RX, sorry!", Toast.LENGTH_SHORT).show();
				}
				rxAdap.close();
				if(MainActivity.getInstance() == null) {
					Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(resultIntent);
				}
				else {
					Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
					resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(resultIntent);
				}
				finish();
			}
			
		});
		Button no = (Button) findViewById(R.id.no);
		no.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RxDBAdapter rxAdap;
				if (MainActivity.rxAdapter == null) {
				 rxAdap = new RxDBAdapter(getApplicationContext());
				}
				else {
					rxAdap = MainActivity.rxAdapter;
				}
				Date lrd = rxAdap.getLastRefillDate(id);
				Calendar cal = Calendar.getInstance();
				cal.setTime(lrd);
				cal.add(Calendar.DATE, 1);
				Date updated = cal.getTime();
				if (rxAdap.updateRxRefillDate(id, updated)) {
					Toast.makeText(getApplicationContext(), "Incremented next refill by one", Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(getApplicationContext(), "Something went wrong updating your RX, sorry!", Toast.LENGTH_SHORT).show();
				}
				rxAdap.close();
				if(MainActivity.getInstance() == null) {
					Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(resultIntent);
				}
				else {
					Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
					resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(resultIntent);
				}
				finish();
			}
		});
	}
}
