package cs250.spring14.refill;

import java.text.ParseException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RxFragment extends Fragment {
	ListView rxList;
	ArrayAdapter<RxItem> rxAdap;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_rx, container, false);
        rxList = (ListView) rootView.findViewById(R.id.listView1);
        try {
			rxAdap = new ArrayAdapter<RxItem>(rootView.getContext(),android.R.layout.simple_list_item_1, MainActivity.rxAdapter.getAllRxs());
		} catch (ParseException e) {
			e.printStackTrace();
		}
        rxList.setAdapter(rxAdap);
        rxAdap.notifyDataSetChanged();
        return rootView;
    }
}
