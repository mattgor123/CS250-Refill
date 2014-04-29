package cs250.spring14.refill.view;

/**
 * Interface extended by the many fragments we use that must stay refreshed upon changes
 * 
 * @See RxFragment, HistoryFragment, DoctorFragment, PatientFragment, PharmacyFragment,
 *      ScheduleFragment
 */
public interface RefreshableFragment {
  /**
   * To communicate with MainActivity (to refresh view)
   * http://stackoverflow.com/questions/15121373/returning
   * -string-from-dialog-fragment-back-to-activity
   */
  public static interface OnCompleteListener {
    public abstract void onComplete(boolean b);
  }

  public OnCompleteListener getmListener();

  public void setmListener(OnCompleteListener mListener);

  /**
   * To ensure that the Fragment properly displays the most up-to-date information
   */
  void repopulateAdapter();
}
