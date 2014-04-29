package cs250.spring14.refill;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cs250.spring14.refill.core.HistoryItem;
import cs250.spring14.refill.view.HistoryWrapper;
import cs250.spring14.refill.view.RefreshableFragment;

public class HistoryFragment extends Fragment implements RefreshableFragment {
  ListView historyList;
  ArrayAdapter<HistoryItem> hisAdap;
  private RefreshableFragment.OnCompleteListener mListener;

  @Override
  public RefreshableFragment.OnCompleteListener getmListener() {
    return mListener;
  }

  @Override
  public void setmListener(RefreshableFragment.OnCompleteListener mListener) {
    this.mListener = mListener;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      this.setmListener((RefreshableFragment.OnCompleteListener) activity);
      setRetainInstance(true);
    } catch (final ClassCastException e) {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    setRetainInstance(true);
    View rootView = inflater.inflate(R.layout.fragment_his, container, false);
    historyList = (ListView) rootView.findViewById(R.id.listView1);
    hisAdap = new HistoryWrapper(rootView.getContext(), 0, MainActivity.hAdapter.getHisForDisplay());
    historyList.setAdapter(hisAdap);
    // Disabling clicking on historyList for consistency: all functionality
    // Can be found in the Pharmacy/Doctor/Patient Overflow Menu
    /**
     * historyList.setOnItemClickListener(new OnItemClickListener() {
     * 
     * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
     *           // This is where we will add functionality to edit the // Dr./Pharmacy final
     *           HistoryItem hi = (HistoryItem) parent .getItemAtPosition(position); // Doctor if
     *           (hi.getH() == HistoryType.D) { MainActivity.alertMessage(getActivity(),
     *           "Please select an action", "Would you like to Remove or View/Edit details for Dr. "
     *           + hi.getOwner() + "?", "Remove", // Remove new DialogInterface.OnClickListener() {
     * @Override public void onClick(DialogInterface dialog, int which) { Doctor d =
     *           MainActivity.drAdapter .getDocByName(hi.getOwner()); if (d != null &&
     *           MainActivity.rxAdapter.existsRxWithDoc(Doctor .makeStringFromDoc(d))) {
     *           Toast.makeText( getActivity(),
     *           "Can't delete this Doctor because an Rx with this Doctor already exists!" ,
     *           Toast.LENGTH_SHORT).show(); } else if (MainActivity.drAdapter
     *           .removeDrByName(hi.getOwner())) { // We were able to remove the pharmacy String
     *           message = "Removed from Doctor DB on " + MainActivity.df .format(Calendar
     *           .getInstance() .getTime()); HistoryItem his = new HistoryItem(hi .getOwner(),
     *           message, "DD"); MainActivity.hAdapter.insertHis(his); // hisAdap.add(his); //
     *           hisAdap.notifyDataSetChanged(); Toast.makeText( getActivity(), "Deleted Doctor " +
     *           hi.getOwner() + " from the Doctor DB", Toast.LENGTH_SHORT).show();
     *           repopulateAdapter(); } else { // Doctor has already been deleted Toast.makeText(
     *           getActivity(),
     *           "Sorry, we couldn't delete this Doctor. Are you sure you haven't edited or already removed this Doctor?"
     *           , Toast.LENGTH_SHORT).show(); } } }, "View/Edit", // Edit new
     *           DialogInterface.OnClickListener() {
     * @Override public void onClick(DialogInterface dialog, int which) { Doctor dr =
     *           MainActivity.drAdapter .getDocByName(hi.getOwner()); if (dr != null) {
     *           Doctor.openEditDoctorDialog( getActivity(), dr, HistoryFragment.this);
     *           repopulateAdapter(); } else { Toast.makeText( getActivity(),
     *           "Sorry,  we couldn't edit this Doctor. Are you sure you haven't removed this Doctor or edited their name?"
     *           , Toast.LENGTH_SHORT).show(); } } }); } // Pharmacy else if (hi.getH() ==
     *           HistoryType.P) { MainActivity.alertMessage(getActivity(),
     *           "Please select an action",
     *           "Would you like to Remove or View/Edit details for Pharmacy " + hi.getOwner() +
     *           "?", "Remove", // Remove new DialogInterface.OnClickListener() {
     * @Override public void onClick(DialogInterface dialog, int which) { Pharmacy p =
     *           MainActivity.phAdapter .getPharmByName(hi.getOwner()); if (p != null &&
     *           MainActivity.rxAdapter.existsRxWithPharm(Pharmacy .makeStringFromPharm(p))) {
     *           Toast.makeText( getActivity(),
     *           "Can't delete this Pharmacy because an Rx with this Pharmacy already exists" ,
     *           Toast.LENGTH_SHORT).show(); } else if (MainActivity.phAdapter
     *           .removePhByName(hi.getOwner())) { // We were able to remove the pharmacy String
     *           message = "Removed from Pharmacy DB on " + MainActivity.df .format(Calendar
     *           .getInstance() .getTime()); HistoryItem his = new HistoryItem(hi .getOwner(),
     *           message, "PD"); MainActivity.hAdapter.insertHis(his); Toast.makeText(
     *           getActivity(), "Deleted Pharmacy " + hi.getOwner() + " from the Pharmacy DB",
     *           Toast.LENGTH_SHORT).show(); // hisAdap.add(his); // hisAdap.notifyDataSetChanged();
     *           repopulateAdapter(); } else { // Pharmacy has already been deleted Toast.makeText(
     *           getActivity(),
     *           "Sorry, we couldn't delete this pharmacy. Are you sure you haven't edited or already removed this Pharmacy?"
     *           , Toast.LENGTH_SHORT).show(); } } }, "View/Edit", // Edit new
     *           DialogInterface.OnClickListener() {
     * @Override public void onClick(DialogInterface dialog, int which) { Pharmacy ph =
     *           MainActivity.phAdapter .getPharmByName(hi.getOwner()); if (ph != null) {
     *           Pharmacy.openEditPharmacyDialog( getActivity(), ph, HistoryFragment.this);
     *           repopulateAdapter(); } else { Toast.makeText( getActivity(),
     *           "Sorry,  we couldn't edit this pharmacy. Are you sure you haven't removed this Pharmacy or edited its name?"
     *           , Toast.LENGTH_SHORT).show(); } } }); }
     * 
     *           }
     * 
     *           });
     */
    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    repopulateAdapter();
  }

  @Override
  public void repopulateAdapter() {
    if (hisAdap != null) {
      hisAdap.clear();
      hisAdap.addAll(MainActivity.hAdapter.getHisForDisplay());
    }
  }

}
