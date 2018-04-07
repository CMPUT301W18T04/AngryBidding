package ca.ualberta.angrybidding.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
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
import com.slouple.android.widget.image.ImageSlide;
import com.slouple.android.widget.image.ImageSlideListener;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.map.LocationPoint;

/**
 * EditTask a task passed from intent
 */
public class EditTaskActivity extends AngryBiddingActivity {
    public final static int REQUEST_CODE = 1004;
    private ElasticSearchTask task;
    private String currentID;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private ImageSelector imageSelector;
    private SubmitButton editSaveButton;
    private TextView pickLocationTextView;
    private LocationPoint locationPoint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        /*
         * get intent pack of ElasticSearchTask object and ID
         */
        Intent pack = getIntent();
        String taskJson = pack.getStringExtra("task");
        currentID = pack.getStringExtra("id");
        task = new Gson().fromJson(taskJson, ElasticSearchTask.class);

        titleEditText = findViewById(R.id.editTaskTitle);
        descriptionEditText = findViewById(R.id.editTaskDescription);
        imageSelector = findViewById(R.id.editTaskImageSelector);
        editSaveButton = findViewById(R.id.editTaskSubmitButton);

        titleEditText.setText(task.getTitle());
        descriptionEditText.setText(task.getDescription());

        addResultRequest(new ResultRequest(PickLocationActivity.REQUEST_CODE) {
            @Override
            public void onResult(Intent intent) {
                LocationPoint locationPoint = new Gson().fromJson(intent.getStringExtra("locationPoint"), LocationPoint.class);
                pickLocationTextView.setText(locationPoint.getLatitude() + " " + locationPoint.getLongitude());
                EditTaskActivity.this.locationPoint = locationPoint;
                checkSubmit();
            }

            @Override
            public void onCancel(Intent intent) {

            }
        });

        pickLocationTextView = findViewById(R.id.editTaskPickLocation);
        pickLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditTaskActivity.this, PickLocationActivity.class);
                startActivityForResult(intent, PickLocationActivity.REQUEST_CODE);
            }
        });
        pickLocationTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(EditTaskActivity.this)
                        .setTitle(R.string.removeLocation)
                        .setMessage(R.string.confirmRemoveLocation)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pickLocationTextView.setText(R.string.pickLocation);
                                task.setLocationPoint(null);
                                locationPoint = null;
                                checkSubmit();
                            }

                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
                return true;
            }
        });

        if (task.getLocationPoint() != null) {
            pickLocationTextView.setText(task.getLocationPoint().getLatitude() + " " + task.getLocationPoint().getLongitude());
        }

        ArrayList<String> photos = task.getPhotos();
        for (String string : photos) {
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
                checkSubmit();
                task.getPhotos().remove(imageSlide.getUrl());
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
                checkSubmit();
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

    public void checkSubmit() {
        if (canSubmit()) {
            editSaveButton.enable();
        } else {
            editSaveButton.disable();
        }
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

    public void enableInputs(boolean enable) {
        titleEditText.setEnabled(enable);
        descriptionEditText.setEnabled(enable);
        pickLocationTextView.setEnabled(enable);
        imageSelector.setEnabled(enable);
    }

    /**
     * Handles submit for edit task button
     */
    public void onSubmit() {
        enableInputs(false);

        task.setTitle(getEditTitle());
        task.setDescription(getEditDescription());
        if (locationPoint != null) {
            task.setLocationPoint(this.locationPoint);
        }
        ArrayList<File> cacheFiles = imageSelector.getCacheFiles();
        ArrayList<File> imageFiles = new ArrayList<>();
        File imageFilesDir = new File(getCacheDir(), "EditImage");
        imageFilesDir.mkdirs();

        for (int i = 0; i < cacheFiles.size(); i++) {
            File cacheFile = cacheFiles.get(i);
            if (cacheFile == null) {
                continue;
            }
            String mimeType = ImageHelper.getMimeType(cacheFile);
            if (mimeType != null) {
                String fileName = String.valueOf(i) + "." + ImageHelper.getFileExtensionFromMimeType(mimeType);
                File imageFile = new File(imageFilesDir, fileName);
                try {
                    ImageHelper.limitBitmapFile(cacheFile, imageFile, new Point(140, 140));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageFiles.add(imageFile);
            }
        }

        for (File file : imageFiles) {
            if (file != null) {
                try {
                    FileInputStream stream = new FileInputStream(file);
                    byte[] byteArray = IOUtils.toByteArray(stream);
                    String string = "data:image/jpg;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT);
                    task.getPhotos().add(string);
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ElasticSearchTask.updateTask(EditTaskActivity.this, currentID, task, new UpdateResponseListener() {
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
                enableInputs(true);
            }
        });

    }

}




