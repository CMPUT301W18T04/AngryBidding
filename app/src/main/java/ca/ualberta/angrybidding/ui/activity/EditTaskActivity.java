package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.slouple.android.widget.button.SubmitButton;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.ui.view.TaskView;

/*
This class is used to edit the tasks which are already created
 */
public class EditTaskActivity extends AngryBiddingActivity{
    public final static int REQUEST_CODE = 1004;
    private ElasticSearchTask currentTask;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        currentTask = getIntent().


        final SubmitButton editSaveButton = findViewById(R.id.editTaskSave);
        editSaveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                currentTask.setTitle(getEditTitle());
                currentTask.setDescription(getEditDescription());
                ElasticSearchTask.updateTask(this, currentID, currentTask, new UpdateResponseListener() {
                    @Override
                    public void onCreated(String id) {

                    }

                    @Override
                    public void onUpdated(int version) {
                        finish();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        editSaveButton.onError(R.string.errorOccurred);
                    }
                });

            }
        });

        /*
         * onClick action for delete button
         */
        final Button editDeleteButton = (Button) findViewById(R.id.editTaskDelete);
        editDeleteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(canSubmit()){
                    currentTask.delete()
                    finish();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Error on save button",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadInfo(currentTask);

    }

    /*
    load info(existed title and description for current task)
     */
    private void loadInfo(ElasticSearchTask task){
        EditText editTitle = findViewById(R.id.editTaskTitle);
        EditText editDescription = findViewById(R.id.editTaskDescription);

        editTitle.setText(task.getTitle());
        editDescription.setText(task.getDescription());
    }

    private boolean canSubmit(){
        return true;
    }

    public String getEditTitle(){
        EditText newTitle = findViewById(R.id.editTaskTitle);
        return newTitle.getText().toString();
    }

    public String getEditDescription(){
        EditText newDescription = findViewById(R.id.editTaskDescription);
        return newDescription.getText().toString();
    }

}




