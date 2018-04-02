package ca.ualberta.angrybidding.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;
import com.slouple.android.widget.filter.UsernameTextWatcher;

import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;

/**
 * Sign Up with username, password and email address
 */
public class SignUpActivity extends AngryBiddingActivity {
    private TextView usernameTextView;
    private TextView passwordTextView;
    private TextView emailAddressTextView;
    private SubmitButton signUpButton;

    public static final int SUCCESS_REQUEST = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle(R.string.createAccount);

        //Init views
        usernameTextView = (EditText) findViewById(R.id.signUpUsername);
        usernameTextView.addTextChangedListener(new UsernameTextWatcher());

        passwordTextView = (EditText) findViewById(R.id.signUpPassword);
        signUpButton = findViewById(R.id.signUpSubmitButton);

        emailAddressTextView = findViewById(R.id.signUpEmailAddress);

        //Submit watcher
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (canSubmit()) {
                    signUpButton.enable();
                } else {
                    signUpButton.disable();
                }
            }
        };
        usernameTextView.addTextChangedListener(watcher);
        passwordTextView.addTextChangedListener(watcher);
        emailAddressTextView.addTextChangedListener(watcher);

        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUpButton.triggerClick();
                    return true;
                }
                return false;
            }
        });

        signUpButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {
                onSignUpButtonPressed();
            }

            @Override
            public void onDisabledClick() {

            }

            @Override
            public void onSubmitClick() {

            }
        });
    }

    /**
     * Handles sign up
     */
    private void onSignUpButtonPressed() {
        enableInputs(false);
        final String username = usernameTextView.getText().toString();
        final String password = passwordTextView.getText().toString();
        final String emailAddress = emailAddressTextView.getText().toString();

        ElasticSearchUser.signUp(this, username, password, emailAddress, new ElasticSearchUser.UserSignUpListener() {
            @Override
            public void onSuccess(ElasticSearchUser user) {
                ElasticSearchUser.setMainUser(SignUpActivity.this, user);
                enableInputs(true);

                setResult(RESULT_OK);
                SignUpActivity.this.finish();
            }

            @Override
            public void onDuplicate() {
                signUpButton.onError(R.string.usernameOrEmailAddressTaken);
                enableInputs(true);
            }

            @Override
            public void onError(VolleyError error) {
                signUpButton.onError(R.string.errorOccurred);
                Log.e("SignUpActivity", "signUp: " + error);
                enableInputs(true);
            }
        });
    }

    /**
     * Confirms exit before closing
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(com.slouple.R.string.cancel)
                .setMessage(com.slouple.R.string.confirmCancel)
                .setPositiveButton(com.slouple.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                })
                .setNegativeButton(com.slouple.R.string.no, null)
                .show();
    }

    /**
     * @return Are inputs valid for sign up
     */
    private boolean canSubmit() {
        return usernameTextView.length() >= 4 && passwordTextView.length() >= 8 &&
                emailAddressTextView.length() >= 5 && emailAddressTextView.getText().toString().contains("@");
    }

    /**
     * @param enabled Should input be enabled
     */
    public void enableInputs(boolean enabled) {
        usernameTextView.setEnabled(enabled);
        passwordTextView.setEnabled(enabled);
        emailAddressTextView.setEnabled(enabled);
    }
}
