package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.ui.activity.main.history.TaskView;

/*
This class is used to edit the tasks which are already created
 */
public class EditTaskActivity extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Intent intent = getIntent();

        loadInfo(currentTask);

        final Button editSaveButton = (Button) findViewById(R.id.editTaskSave);
        final Button editDeleteButton = (Button) findViewById(R.id.editTaskDelete);

        /*
        onClick action for save button
         */
        View.OnClickListener saveListener = new View.OnClickListener() {
            public void onClick(View view) {

                if (canSubmit()) {
                    currentTask.setTitle(getEditTitle());
                    currentTask.setDescription(getEditDescription());
                    finish();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Error on save button",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        /*
        onClick action for delete button
         */
        View.OnClickListener deleteListener = new View.OnClickListener() {
            public void onClick(View view) {

                if (canSubmit()) {
                    currentTask.deleteTask();
                    finish();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Error on delete button",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        editSaveButton.setOnClickListener(saveListener);
        editDeleteButton.setOnClickListener(deleteListener);

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




