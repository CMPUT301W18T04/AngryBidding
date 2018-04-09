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

import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;

/**
 * Displays and Updates User Info
 * Only Email Address and Phone Number can be edited
 */
public class EditUserProfileActivity extends AngryBiddingActivity {
    private TextView usernameTextView;
    private EditText emailAddressEditText;
    private EditText phoneNumberEditText;
    private SubmitButton submitButton;


    private String username;
    private ElasticSearchUser user;

    public static final int REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        getSupportActionBar().setTitle(R.string.editProfile);

        username = getIntent().getStringExtra("username");

        //Init views
        usernameTextView = findViewById(R.id.editUserProfileUsername);
        emailAddressEditText = findViewById(R.id.editUserProfileEmailAddress);
        phoneNumberEditText = findViewById(R.id.editUserProfilePhoneNumber);
        submitButton = findViewById(R.id.editUserProfileSubmitButton);


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
                    submitButton.enable();
                } else {
                    submitButton.disable();
                }
            }
        };
        emailAddressEditText.addTextChangedListener(watcher);
        phoneNumberEditText.addTextChangedListener(watcher);

        phoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitButton.triggerClick();
                    return true;
                }
                return false;
            }
        });

        submitButton.setButtonListener(new SubmitButtonListener() {
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

        enableInputs(false);
        ElasticSearchUser.getUserByUsername(this, username, new ElasticSearchUser.GetUserListener() {
            @Override
            public void onFound(ElasticSearchUser user) {
                EditUserProfileActivity.this.user = user;
                usernameTextView.setText(user.getUsername());
                emailAddressEditText.setText(user.getEmailAddress());
                phoneNumberEditText.setText(user.getPhoneNumber());
                enableInputs(true);
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }

    /**
     * Handles sign up
     */
    private void onSignUpButtonPressed() {
        enableInputs(false);
        final String emailAddress = emailAddressEditText.getText().toString();
        final String phoneNumber = phoneNumberEditText.getText().toString();

        user.setEmailAddress(emailAddress);
        user.setPhoneNumber(phoneNumber);

        ElasticSearchUser.updateUser(this, user, new UpdateResponseListener() {
            @Override
            public void onCreated(String id) {

            }

            @Override
            public void onUpdated(int version) {
                ElasticSearchUser.setMainUser(EditUserProfileActivity.this, user);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EditUserProfileActivity", error.getMessage(), error);
                submitButton.onError(R.string.errorOccurred);
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
        return user != null && emailAddressEditText.length() >= 5 && emailAddressEditText.getText().toString().contains("@");
    }

    /**
     * @param enabled Should input be enabled
     */
    public void enableInputs(boolean enabled) {
        emailAddressEditText.setEnabled(enabled);
        phoneNumberEditText.setEnabled(enabled);
    }
}
