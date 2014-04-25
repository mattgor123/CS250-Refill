package cs250.spring14.refill.view;

//Code adapted from http://stackoverflow.com/questions/20412379/viewpager-update-fragment-on-swipe
public interface RefreshableFragment {
	// To communicate with MainActivity (to refresh view)
	// Code adapted from
	// http://stackoverflow.com/questions/15121373/returning-string-from-dialog-fragment-back-to-activity
	public static interface OnCompleteListener {
		public abstract void onComplete(boolean b);
	}

	public OnCompleteListener getmListener();

	public void setmListener(OnCompleteListener mListener);

	void repopulateAdapter();
}
