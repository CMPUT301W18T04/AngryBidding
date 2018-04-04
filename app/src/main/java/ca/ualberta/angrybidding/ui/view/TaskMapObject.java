package ca.ualberta.angrybidding.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.slouple.android.ColorHelper;
import com.slouple.android.Pointer;
import com.slouple.android.Units;
import com.slouple.android.input.TapDetector;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.map.LocationPoint;
import ca.ualberta.angrybidding.map.MapObject;
import ca.ualberta.angrybidding.map.MapObjectTouchListener;
import ca.ualberta.angrybidding.ui.activity.ViewTaskDetailActivity;

public class TaskMapObject extends MapObject {
    private FrameLayout container;
    private View barView;
    private CardView bubble;
    private TextView titleTextView;

    private ElasticSearchTask task;

    private boolean spawning = false;
    private boolean dying = false;

    public TaskMapObject(Context context, ElasticSearchTask task) {
        super(context, new LocationPoint(task.getLocationPoint().getLatitude(), task.getLocationPoint().getLongitude()));
        this.task = task;

        container = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.view_task_map_object, null, false);
        addView(container);
        bubble = container.findViewById(R.id.taskBubble);
        barView = container.findViewById(R.id.taskBar);
        titleTextView = container.findViewById(R.id.taskTitle);

        titleTextView.setText(task.getTitle());
        int seed = (int) task.getID().charAt(0) * (int) task.getID().charAt(1) * (int) task.getID().charAt(2);
        barView.setBackgroundColor(ColorHelper.intToRandomColor(seed, 0.8, 1.0, 0.9));

        setOffset(new Point(Units.dpToPX(75, getContext()), Units.dpToPX(50, getContext())));
    }

    @Override
    public void onMapViewPost() {
        updateLayout();
        super.onMapViewPost();

        //Bubble Click
        final MapObjectTouchListener bubbleListener = new MapObjectTouchListener(getContext(), this, bubble);
        new TapDetector(bubbleListener) {
            @Override
            public void onSingleTap(Pointer pointer) {
                viewTaskDetail();
            }

            @Override
            public void onDoubleTap(Pointer pointer) {
                hideTaskMapObject();
            }

            @Override
            public void onHold(Pointer pointer) {

            }
        };
        bubble.setOnTouchListener(bubbleListener);
        this.setVisibility(VISIBLE);
    }

    public void viewTaskDetail() {
        Intent intent = new Intent(getContext(), ViewTaskDetailActivity.class);
        intent.putExtra("id", task.getID());
        intent.putExtra("task", new Gson().toJson(task));
        getContext().startActivity(intent);
    }

    public void hideTaskMapObject() {
        setVisibility(INVISIBLE);
    }

    public void updateLayout() {
        //Min font size: 10 | Max font size: 25
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30 / Math.max(titleTextView.getText().length() / 3, 1) + 7);
    }

    public ElasticSearchTask getTask() {
        return this.task;
    }

    public void setSpawning(boolean spawning) {
        this.spawning = spawning;
    }

    public boolean isSpawning() {
        return spawning;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return dying;
    }


}
