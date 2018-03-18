package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.slouple.android.ResultRequest;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;
import com.slouple.android.widget.filter.UsernameTextWatcher;

import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.main.MainActivity;

public class LoginActivity extends AngryBiddingActivity {

    private SubmitButton loginButton;
    private TextView usernameTextView;
    private TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(getResources().getString(R.string.login));

        usernameTextView = findViewById(R.id.loginUsernameTextView);
        usernameTextView.addTextChangedListener(new UsernameTextWatcher());
        passwordTextView = findViewById(R.id.loginPasswordTextView);
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
                    loginButton.enable();
                } else {
                    loginButton.disable();
                }
            }
        };
        usernameTextView.addTextChangedListener(watcher);
        passwordTextView.addTextChangedListener(watcher);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {
                onLoginButtonPressed();
            }

            @Override
            public void onDisabledClick() {

            }

            @Override
            public void onSubmitClick() {

            }
        });

        passwordTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.triggerClick();
                    return true;
                }
                return false;
            }
        });

        TextView createAccountTextView = (TextView) findViewById(R.id.createAccountTextView);
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, SignUpActivity.SUCCESS_REQUEST);
            }
        });

        addResultRequest(new ResultRequest(SignUpActivity.SUCCESS_REQUEST) {
            @Override
            public void onResult(Intent data) {
                setResult(RESULT_OK);
                startActivity(MainActivity.class);
                finish();
            }

            @Override
            public void onCancel(Intent data) {

            }
        });
    }

    private boolean canSubmit() {
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        return username.length() >= 4 && password.length() >= 8;
    }

    private void onLoginButtonPressed() {
        enableInputs(false);
        String username = usernameTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        ElasticSearchUser.login(this, username, password, new ElasticSearchUser.UserLoginListener() {
            @Override
            public void onSuccess(ElasticSearchUser user) {
                ElasticSearchUser.setMainUser(LoginActivity.this, user);
                loginButton.onSuccess();
                startActivity(MainActivity.class);
                LoginActivity.this.finish();
            }

            @Override
            public void onFailure() {
                loginButton.onError(R.string.invalidUsernameOrPassword);
                enableInputs(true);
            }

            @Override
            public void onError(VolleyError error) {
                loginButton.onError(R.string.errorOccurred);
                Log.e("LoginActivity", error.getMessage(), error);
                enableInputs(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void enableInputs(boolean enabled) {
        usernameTextView.setEnabled(enabled);
        passwordTextView.setEnabled(enabled);
    }
}