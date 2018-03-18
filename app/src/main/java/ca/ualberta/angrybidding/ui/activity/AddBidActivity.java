package ca.ualberta.angrybidding.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.slouple.android.widget.button.SubmitButton;
import com.slouple.android.widget.button.SubmitButtonListener;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.ElasticSearchUser;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;
import ca.ualberta.angrybidding.elasticsearch.UpdateResponseListener;
import ca.ualberta.angrybidding.ui.activity.main.AllTaskFragment;
import ca.ualberta.angrybidding.ui.activity.main.MainActivity;
import ca.ualberta.angrybidding.ui.view.TaskView;

public class AddBidActivity extends AngryBiddingActivity {
    public static final int REQUEST_CODE = 1003;
    private ElasticSearchTask elasticSearchTask;
    private User user;
    private String id;

    private EditText priceEditText;
    private SubmitButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bid);
        Intent i = getIntent();
        String taskJson = i.getStringExtra("task");
        id = i.getStringExtra("id");
        elasticSearchTask = new Gson().fromJson(taskJson, ElasticSearchTask.class);
        user = ElasticSearchUser.getMainUser(this);

        priceEditText = findViewById(R.id.addBidPrice);
        priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(canSubmit()){
                    submitButton.setEnabled(true);
                }else{
                    submitButton.setEnabled(false);
                }
            }
        });

        submitButton = findViewById(R.id.addBidSubmitButton);
        submitButton.setButtonListener(new SubmitButtonListener() {
            @Override
            public void onSubmit() {
                AddBidActivity.this.onSubmit();
            }

            @Override
            public void onDisabledClick() {

            }

            @Override
            public void onSubmitClick() {

            }
        });

    }

    public boolean canSubmit(){
        try{
            BigDecimal price = new BigDecimal(priceEditText.getText().toString());
            return price.compareTo(new BigDecimal("0.01")) >= 0;
        }catch(Throwable throwable){
            return false;
        }
    }

    public BigDecimal getPrice(){
        return new BigDecimal(priceEditText.getText().toString());
    }

    public void onSubmit(){
        BigDecimal price = getPrice();
        elasticSearchTask.getBids().add(new Bid(user, price));
        ElasticSearchTask.updateTask(this, id, elasticSearchTask, new UpdateResponseListener() {
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

            }
        });
    }
}
