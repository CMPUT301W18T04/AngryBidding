package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.ImageHelper;
import com.slouple.android.ResultRequest;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;
import com.slouple.android.widget.image.CameraSelectorModule;
import com.slouple.android.widget.image.GallerySelectorModule;
import com.slouple.android.widget.image.ImageSelector;
import com.slouple.util.Hash;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.Task;
import ca.ualberta.angrybidding.TaskCache;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.AddResponseListener;
import ca.ualberta.angrybidding.map.LocationPoint;

public class AddTaskActivity extends AngryBiddingActivity {

    public static final int REQUEST_CODE = 1001;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView pickLocationTextView;
    private LocationPoint locationPoint;
    private ImageSelector imageSelector;
    private SubmitButton submitButton;


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


        addResultRequest(new ResultRequest(PickLocationActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                LocationPoint locationPoint = new Gson().fromJson(intent.getStringExtra("locationPoint"), LocationPoint.class);
                pickLocationTextView.setText(locationPoint.getLatitude() + " " + locationPoint.getLongitude());
                AddTaskActivity.this.locationPoint = locationPoint;
            }

            @Override
            public void onCancel(Intent intent) {

            }
        });

        pickLocationTextView = findViewById(R.id.addTaskPickLocation);
        pickLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddTaskActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, PickLocationActivity.REQUEST_CODE);
            }
        });

        imageSelector = findViewById(R.id.addTaskImageSelector);

        CameraSelectorModule cameraSelectorModule = new CameraSelectorModule("ca.ualberta.angrybidding.fileprovider");
        imageSelector.addModule(cameraSelectorModule);

        GallerySelectorModule gallerySelectorModule = new GallerySelectorModule();
        imageSelector.addModule(gallerySelectorModule);

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

        final Task task = new Task(user, title, description, locationPoint);
        ArrayList<File> cacheFiles = imageSelector.getCacheFiles();
        ArrayList<File> imageFiles = new ArrayList<>();
        File imageFilesDir = new File(getCacheDir(), "AddImage");
        imageFilesDir.mkdirs();

        for (int i = 0; i < cacheFiles.size(); i++) {
            File cacheFile = cacheFiles.get(i);
            String fileName = String.valueOf(i) + "." + ImageHelper.getFileExtensionFromMimeType(ImageHelper.getMimeType(cacheFile));
            File imageFile = new File(imageFilesDir, fileName);
            try {
                ImageHelper.limitBitmapFile(cacheFile, imageFile, new Point(140, 140));
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageFiles.add(imageFile);
        }

        for (File file : imageFiles) {
            try {
                FileInputStream stream = new FileInputStream(file);
                byte[] byteArray = IOUtils.toByteArray(stream);
                String string = "data:image/jpg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
                task.getPhotos().add(string);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        ElasticSearchTask.addTask(this, task, new AddResponseListener() {
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
                Log.e("AddTaskActivity", error.getMessage(), error);
                ArrayList<ElasticSearchTask> tasks = TaskCache.readFromFile(AddTaskActivity.this);
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }

                String id = Hash.getHash(String.valueOf(Calendar.getInstance().getTimeInMillis()).getBytes(), Hash.SHA_256).substring(0, 40);
                ElasticSearchTask newTask = new ElasticSearchTask(id, task.getUser(), task.getTitle(), task.getDescription(), task.getLocationPoint(), null);
                newTask.getPhotos().addAll(task.getPhotos());
                tasks.add(newTask);
                TaskCache.saveToFile(AddTaskActivity.this, tasks);

                Intent intent = new Intent();
                intent.putExtra("task", new Gson().toJson(newTask));
                intent.putExtra("offline", true);
                AddTaskActivity.this.setResult(RESULT_OK, intent);
                finish();
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
        pickLocationTextView.setEnabled(false);
        imageSelector.setEnabled(false);
    }
}