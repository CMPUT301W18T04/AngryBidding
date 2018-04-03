package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;

public class AddTaskActivity extends AngryBiddingActivity {

    public static final int REQUEST_CODE = 1001;

    private SubmitButton submitButton;
    private EditText titleEditText;
    private EditText descriptionEditText;


    /**
     * Color for ActionBar and status bar
     *
     * @return Color resource ID
     */
    @Override
    protected int getColorID() {
        return R.color.colorAccentDark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        getSupportActionBar().setTitle(getResources().getString(R.string.addTask));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAccent)));

        titleEditText = findViewById(R.id.addTaskTitle);
        descriptionEditText = findViewById(R.id.addTaskDescription);
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
        titleEditText.addTextChangedListener(watcher);
        descriptionEditText.addTextChangedListener(watcher);

        submitButton = findViewById(R.id.addTaskSubmitButton);
        submitButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {
                onSubmitButtonPressed();
            }

            @Override
            public void onDisabledClick() {

            }

            @Override
            public void onSubmitClick() {

            }
        });

        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submitButton.triggerClick();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @return Can task be submitted
     */
    private boolean canSubmit() {
        String title = titleEditText.getText().toString();
        return title.length() >= 1;
    }

    /**
     * Handles submit
     */
    private void onSubmitButtonPressed() {
        enableInputs(false);
        final String title = titleEditText.getText().toString();
        final String description = descriptionEditText.getText().toString();
        final User user = new User(getElasticSearchUser().getUsername());
        ElasticSearchTask.addTask(this, new Task(user, title, description), new AddResponseListener() {
            @Override
            public void onCreated(String id) {
                submitButton.onSuccess();
                Intent intent = new Intent();
                ElasticSearchTask task = new ElasticSearchTask(id, user, title, description, null, null);
                intent.putExtra("task", new Gson().toJson(task));
                AddTaskActivity.this.setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                submitButton.onError(R.string.errorOccurred);
                enableInputs(true);
                Log.e("AddTaskActivity", error.getMessage(), error);
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * @param enabled Should inputs be enabled
     */
    public void enableInputs(boolean enabled) {
        titleEditText.setEnabled(enabled);
        descriptionEditText.setEnabled(enabled);
    }
}