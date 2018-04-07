package ca.ualberta.angrybidding.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.VolleyError;

import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;

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
