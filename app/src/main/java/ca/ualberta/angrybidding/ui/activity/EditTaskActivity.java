package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;
import com.slouple.android.widget.image.CameraSelectorModule;
import com.slouple.android.widget.image.GallerySelectorModule;
import com.slouple.android.widget.image.ImageSelector;
import com.slouple.android.widget.image.ImageSlide;
import com.slouple.android.widget.image.ImageSlideListener;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;

/**
 * EditTask a task passed from intent
 */
public class EditTaskActivity extends AngryBiddingActivity {
    public final static int REQUEST_CODE = 1004;
    private ElasticSearchTask currentTask;
    private String currentID;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private ImageSelector imageSelector;
    private SubmitButton editSaveButton;

    protected void onCreate(Bundle savedInstanceState) {
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
        imageSelector = findViewById(R.id.editTaskImageSelector);
        editSaveButton = findViewById(R.id.editTaskSubmitButton);

        titleEditText.setText(currentTask.getTitle());
        descriptionEditText.setText(currentTask.getDescription());

        ArrayList<String> photos = currentTask.getPhotos();
        for (String string: photos) {
            imageSelector.addSlide(string);
        }

        CameraSelectorModule cameraSelectorModule = new CameraSelectorModule("ca.ualberta.angrybidding.fileprovider");
        imageSelector.addModule(cameraSelectorModule);

        GallerySelectorModule gallerySelectorModule = new GallerySelectorModule();
        imageSelector.addModule(gallerySelectorModule);

        imageSelector.addImageSlideListener(new ImageSlideListener() {
            @Override
            public void onAdd(ImageSlide imageSlide) {
                editSaveButton.enable();
            }

            @Override
            public void onRemove(ImageSlide imageSlide) {
                editSaveButton.enable();
                currentTask.getPhotos().remove(imageSlide.getUrl());
            }
        });


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

    /**
     * @return Title from EditText
     */
    public String getEditTitle() {
        titleEditText = findViewById(R.id.editTaskTitle);
        return titleEditText.getText().toString();
    }

    /**
     * @return Description from EditText
     */
    public String getEditDescription() {
        descriptionEditText = findViewById(R.id.editTaskDescription);
        return descriptionEditText.getText().toString();
    }

    /**
     * @return Can task be submitted for update
     */
    public boolean canSubmit() {
        String title = titleEditText.getText().toString();
        return title.length() >= 1;
    }

    /**
     * Handles submit for edit task button
     */
    public void onSubmit() {
        currentTask.setTitle(getEditTitle());
        currentTask.setDescription(getEditDescription());

        ArrayList<File> files = imageSelector.getCacheFiles();
        for (File file: files) {
            if (file != null) {
                try {
                    FileInputStream stream = new FileInputStream(file);
                    byte[] byteArray = IOUtils.toByteArray(stream);
                    String string = "data:image/jpg;base64,"+ Base64.encodeToString(byteArray, Base64.DEFAULT);
                    currentTask.getPhotos().add(string);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

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




