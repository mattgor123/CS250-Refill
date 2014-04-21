package cs250.spring14.refill;

import java.util.Calendar;

import cs250.spring14.refill.HistoryItem.HistoryType;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryFragment extends Fragment {
	ListView historyList;
	ArrayAdapter<HistoryItem> hisAdap;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_his, container, false);
        historyList = (ListView) rootView.findViewById(R.id.listView1);
        hisAdap = new HistoryWrapper(rootView.getContext(),0, MainActivity.hAdapter.getAllHis());
        historyList.setAdapter(hisAdap);
        historyList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//This is where we will add functionality to edit the Dr./Pharmacy
				final HistoryItem hi = (HistoryItem) parent.getItemAtPosition(position);
				//Doctor
				if (hi.getH() == HistoryType.D) {
					MainActivity.alertMessage(getActivity(),"Please select an action", "Would you like to Remove or View/Edit details for Dr. " + hi.getOwner() + "?", "Remove", 
							//Remove
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (MainActivity.drAdapter.removeDrByName(hi.getOwner())) {
										//We were able to remove the pharmacy
										String message = "Removed from Doctor DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
		                				MainActivity.hAdapter.insertHis(new HistoryItem(hi.getOwner(),message,"DD"));
		                				onResume();
									}
									else {
										//Pharmacy has already been deleted
										Toast.makeText(getActivity(),"Sorry, we couldn't delete this Doctor. Are you sure you haven't edited or already removed this Doctor?", Toast.LENGTH_SHORT).show();
									}
								}
							}, 
							"View/Edit",
							//Edit
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//Edit Dr. functionality must be added here
								}
							});
				}
				//Pharmacy
				else if (hi.getH() == HistoryType.P){
					MainActivity.alertMessage(getActivity(),"Please select an action", "Would you like to Remove or View/Edit details for Pharmacy " + hi.getOwner() + "?", "Remove", 
							//Remove
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (MainActivity.phAdapter.removePhByName(hi.getOwner())) {
										//We were able to remove the pharmacy
										String message = "Removed from Pharmacy DB on " + MainActivity.df.format(Calendar.getInstance().getTime());
		                				MainActivity.hAdapter.insertHis(new HistoryItem(hi.getOwner(),message,"PD"));
		                				onResume();
									}
									else {
										//Pharmacy has already been deleted
										Toast.makeText(getActivity(),"Sorry, we couldn't delete this pharmacy. Are you sure you haven't edited or already removed this Pharmacy?", Toast.LENGTH_SHORT).show();
									}
								}
							}, 
							"View/Edit",
							//Edit
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									//Edit pharmacy functionality must be added here
								}
							});
				}
				
			}
        	
        });

        hisAdap.notifyDataSetChanged();
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
		hisAdap.clear();
		hisAdap.addAll(MainActivity.hAdapter.getAllHis());
	}

}
