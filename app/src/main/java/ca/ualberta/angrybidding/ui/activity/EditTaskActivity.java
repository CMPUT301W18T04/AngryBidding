package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;

import java.math.BigDecimal;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;

/*
 * This class is used to edit the tasks which are already created
 */
public class EditTaskActivity extends AngryBiddingActivity{
    public final static int REQUEST_CODE = 1004;
    private ElasticSearchTask currentTask;
    private String currentID;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private SubmitButton editSaveButton;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        /*
         * get intent pack of ElasticSearchTask object and ID
         */
        Intent pack = getIntent();
        String taskJson = pack.getStringExtra("task");
        currentID = pack.getStringExtra("id");
        currentTask = new Gson().fromJson(taskJson, ElasticSearchTask.class);

        titleEditText = findViewById(R.id.editTaskTitle);
        descriptionEditText = findViewById(R.id.editTaskDescription);
        editSaveButton = findViewById(R.id.editTaskSubmitButton);

        titleEditText.setText(currentTask.getTitle());
        descriptionEditText.setText(currentTask.getDescription());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (canSubmit()) {
                    editSaveButton.enable();
                } else {
                    editSaveButton.disable();
                }
            }
        };
        titleEditText.addTextChangedListener(textWatcher);
        descriptionEditText.addTextChangedListener(textWatcher);

        /*
         * Listener for save button
         * Will save edited information after click
         */
        editSaveButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {
                EditTaskActivity.this.onSubmit();
            }

            @Override
            public void onDisabledClick() {

            }

            @Override
            public void onSubmitClick() {

            }
        });
    }

    public String getEditTitle(){
        titleEditText = findViewById(R.id.editTaskTitle);
        return titleEditText.getText().toString();
    }

    public String getEditDescription(){
        descriptionEditText = findViewById(R.id.editTaskDescription);
        return descriptionEditText.getText().toString();
    }

    public boolean canSubmit(){
        String title = titleEditText.getText().toString();
        return title.length() >= 1;
    }

    public void onSubmit() {
        currentTask.setTitle(getEditTitle());
        currentTask.setDescription(getEditDescription());
        ElasticSearchTask.updateTask(EditTaskActivity.this, currentID, currentTask, new UpdateResponseListener() {
            @Override
            public void onCreated(String id) {

            }

            @Override
            public void onUpdated(int version) {
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                editSaveButton.onError(R.string.errorOccurred);
            }
        });

    }

}




