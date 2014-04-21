package cs250.spring14.refill;

import java.text.ParseException;
import java.util.Calendar;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

public class RxFragment extends Fragment {
	ListView rxList;
	ArrayAdapter<RxItem> rxAdap;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_rx, container, false);
        rxList = (ListView) rootView.findViewById(R.id.listView1);
        try {
			rxAdap = new RxWrapper(rootView.getContext(),0, MainActivity.rxAdapter.getAllRxs());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        rxList.setAdapter(rxAdap);
        rxList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//This is where we will add functionality to edit the Rx
				final RxItem rx = (RxItem) parent.getItemAtPosition(position);
				MainActivity.alertMessage(getActivity(),"Please select an action", "Would you like to Remove or View/Edit details for " + rx.getName() + "?", "Remove", 
						//Remove
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//Remove functionality must be added here
								MainActivity.rxAdapter.removeRx(rx.getId());
								String msg = "Removed from Prescriptions DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
								MainActivity.hAdapter.insertHis(new HistoryItem(rx.getName(),msg,"R"));
								onResume();
							}
						}, 
						"View/Edit",
						//Edit
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//Edit functionality must be added here
								
								MainActivity.getInstance().openAddOrEditRxDialog(MainActivity.getInstance(), rx);
								 //Toast.makeText(getActivity(),
											//"You selected to edit Rx " + rx.getName(), Toast.LENGTH_SHORT)
											//.show();
							}
						});
			}
        	
        });
        rxAdap.notifyDataSetChanged();
        return rootView;
    }
	
	//We will manually call this to ensure the Prescriptions view is always current
	@Override
	public void onResume() {
		super.onResume();
		rxAdap.clear();
		try {
			rxAdap.addAll(MainActivity.rxAdapter.getAllRxs());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
