package cs250.spring14.refill;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoryFragment extends Fragment {
	ListView historyList;
	ArrayAdapter<HistoryItem> hisAdap;
	private ArrayList<HistoryItem> array;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_his, container, false);
        historyList = (ListView) rootView.findViewById(R.id.listView1);
        array = MainActivity.hAdapter.getAllHis();
        hisAdap = new HistoryWrapper(rootView.getContext(),0, array);
        historyList.setAdapter(hisAdap);
        hisAdap.notifyDataSetChanged();
        return rootView;
    }
	
	@Override
	public void onResume(){
		super.onResume();
		hisAdap.clear();
		array = MainActivity.hAdapter.getAllHis();
		Collections.reverse(array);
		hisAdap.addAll(array);
	}

}
