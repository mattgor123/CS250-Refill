package cs250.spring14.refill;

import java.util.Calendar;
import java.util.Locale;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cs250.spring14.refill.core.HistoryItem;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class LoginActivity extends Activity {

  private static String credentials;
  private static SharedPreferences prefs;
  public static final String RESULT_STRING = "result";
  private static final String firstRunKey = "refill.firstrun";
  private static final String credKey = "refill.credentials";
  public static final String nextKey = "refill.next";
  protected static final int FAILED = 10;
  protected static final int KILLED = 15;
  /**
   * The default email to populate the email field with.
   */
  public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  // Values for email and password at the time of the login attempt.
  private String mEmail;
  private String mPassword;

  // UI references.
  private EditText mEmailView;
  private EditText mPasswordView;
  private View mLoginFormView;
  private View mLoginStatusView;
  private TextView mLoginStatusMessageView;
  public static CheckedTextView checkBox;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActionBar mActionBar = getActionBar();
    mActionBar.hide();
    // Get the shared preferences with the log-in details
    prefs = this.getSharedPreferences("refill", Context.MODE_PRIVATE);
    credentials = prefs.getString(credKey, "");
    boolean firstRun = prefs.getBoolean(firstRunKey, true);
    if (firstRun) {
      // Make the first run dialog
      prefs.edit().putBoolean(firstRunKey, false).commit();
      makeFirstRunDialog();
    }
    setContentView(R.layout.activity_login);
    checkBox = (CheckedTextView) findViewById(R.id.checkedTextView1);
    checkBox.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ((CheckedTextView) v).toggle();
      }
    });

    // Set up the login form.
    mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
    mEmailView = (EditText) findViewById(R.id.email);
    mEmailView.setText(mEmail);

    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

    mLoginFormView = findViewById(R.id.login_form);
    mLoginStatusView = findViewById(R.id.login_status);
    mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

    findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });
  }

  public void makeFirstRunDialog() {
    new AlertDialog.Builder(this)
        .setTitle("Welcome to Refill!")
        .setIcon(R.drawable.ic_launcher)
        .setMessage(
            "It seems as though this is your first time launching Refill! Please enter the credentials you would like associated with your account. Please note that we do not store this data anywhere, so if you forget, you will have to clear Refill's data or Reinstall the app to allow you to recreate your credentials. This means you will lose all of your prescription information")
        .setNeutralButton("I understand", null).show();
  }

  @Override
  public void onBackPressed() {
    Intent returnIntent = new Intent();
    setResult(KILLED, returnIntent);
    finish();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.login, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_forgot_password:
        Toast
            .makeText(
                this,
                "Your Refill account details are not stored anywhere but your phone. If you forgot your account details, please clear Refill's Data from Application Settings and create a new user. Thank you.",
                Toast.LENGTH_LONG).show();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Attempts to sign in or register the account specified by the login form. If there are form
   * errors (invalid email, missing fields, etc.), the errors are presented and no actual login
   * attempt is made.
   */
  public void attemptLogin() {
    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    mEmail = mEmailView.getText().toString();
    mPassword = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password.
    if (TextUtils.isEmpty(mPassword)) {
      mPasswordView.setError(getString(R.string.error_field_required));
      focusView = mPasswordView;
      cancel = true;
    }
    // Check for a valid email address.
    if (TextUtils.isEmpty(mEmail)) {
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!mEmail.contains("@")) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
      showProgress(true);
      mAuthTask = new UserLoginTask();
      mAuthTask.execute((Void) null);
    }
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  private void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginStatusView.setVisibility(View.VISIBLE);
      mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(
          new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
          });

      mLoginFormView.setVisibility(View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(
          new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
              mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
          });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
    @Override
    protected Integer doInBackground(Void... params) {
      // This doesn't really do anything, but it looks cool so I felt like
      // keeping it
      try {
        // Wait for a few seconds to get the loading screen
        Thread.sleep(500);
      } catch (InterruptedException e) {
        return Activity.RESULT_CANCELED;
      }
      if (credentials.length() == 0) {
        // We are creating a NEW user
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
          // Valid e-mail, check Password
          if (mPassword.length() > 0) {
            // Save the credentials
            String credentials = mEmail + ":" + mPassword;
            if (prefs.edit().putString(credKey, credentials).commit()
                && prefs.edit().putBoolean(nextKey, checkBox.isChecked()).commit()) {
              MainActivity.hAdapter.insertHis(new HistoryItem(mEmail, "User Created on "
                  + MainActivity.df.format(Calendar.getInstance().getTime()), "U"));
              return Activity.RESULT_FIRST_USER;
            }
          }
        }
      } else {
        // We are trying to log in
        String[] pieces = credentials.split(":");
        if (pieces[0].toLowerCase(Locale.getDefault()).equals(mEmail.toLowerCase(Locale.getDefault()))) {
          // Account exists, return true if the password matches.
          if (pieces[1].equals(mPassword)) {
            int i =
                (prefs.edit().putBoolean(nextKey, checkBox.isChecked()).commit() ? Activity.RESULT_OK
                    : Activity.RESULT_CANCELED);
            return i;
          }
        }
      }
      // If we hit here, it means our credentials didn't match but we DO
      // have
      // some saved credentials.
      return FAILED;
    }

    @Override
    protected void onPostExecute(final Integer success) {
      mAuthTask = null;
      showProgress(false);
      if (success == Activity.RESULT_OK) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_STRING, mEmail);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
      } else if (success == Activity.RESULT_FIRST_USER) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESULT_STRING, mEmail);
        setResult(Activity.RESULT_FIRST_USER, returnIntent);
        finish();
      } else if (success == FAILED) {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      } else {

      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }

  }
}
