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

public class UserProfileActivity extends AngryBiddingActivity {
    private TextView usernameTextView;
    private TextView emailAddressTextView;
    private TextView phoneNumberTextView;


    private String username;
    private ElasticSearchUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle(R.string.userProfile);

        username = getIntent().getStringExtra("username");

        //Init views
        usernameTextView = findViewById(R.id.userProfileUsername);
        emailAddressTextView = findViewById(R.id.userProfileEmailAddress);
        phoneNumberTextView = findViewById(R.id.userProfilePhoneNumber);

        ElasticSearchUser.getUserByUsername(this, username, new ElasticSearchUser.GetUserListener() {
            @Override
            public void onFound(ElasticSearchUser user) {
                UserProfileActivity.this.user = user;
                usernameTextView.setText(user.getUsername());
                emailAddressTextView.setText(user.getEmailAddress());
                phoneNumberTextView.setText(user.getPhoneNumber());
            }

            @Override
            public void onNotFound() {

            }

            @Override
            public void onError(VolleyError error) {

            }
        });
    }
}
