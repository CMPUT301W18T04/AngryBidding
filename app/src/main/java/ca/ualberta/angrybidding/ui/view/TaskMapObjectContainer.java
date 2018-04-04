package ca.ualberta.angrybidding.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import java.util.ArrayList;

import ca.ualberta.angrybidding.ElasticSearchTask;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.map.MapObject;
import ca.ualberta.angrybidding.map.MapObjectContainer;

public class TaskMapObjectContainer extends MapObjectContainer {
    private int maxMapPostCount;
    private ArrayList<String> finishedPosts = new ArrayList<>();

    public TaskMapObjectContainer(Context context) {
        this(context, null);
    }

    public TaskMapObjectContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskMapObjectContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs == null) {
            init(20);
        } else {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TaskMapObjectContainer, defStyle, 0);
            int maxMapPostCount = ta.getInt(R.styleable.TaskMapObjectContainer_maxTaskMapObjectCount, 20);
            ta.recycle();
            init(maxMapPostCount);
        }
    }

    private void init(int maxMapPostCount) {
        this.maxMapPostCount = maxMapPostCount;
    }

    public TaskMapObjectContainer(Context context, int mapViewID) {
        super(context, mapViewID);
        init(40);
    }

    @Deprecated
    @Override
    public void addView(MapObject mapObject) {
        if (mapObject instanceof TaskMapObject) {
            addView((TaskMapObject) mapObject);
        }
    }

    public void addView(final TaskMapObject taskMapObject) {
        //Check if in finished posts
        if(finishedPosts.contains(taskMapObject.getTask().getID())){
            return;
        }
        //Check for the same entry
        for (int i = 0; i < getChildCount(); i++) {
            TaskMapObject child = (TaskMapObject) getChildAt(i);
            if (child.getTask().getID().equals(taskMapObject.getTask().getID())) {
                super.addView(taskMapObject);   //Removes memory leak
                removeView(taskMapObject);
                return;
            }
        }
        //Check if max amount of posts is reached
        if (getChildCount() >= maxMapPostCount) {
            for (int i = 0; i < getChildCount(); i++) {
                final TaskMapObject child = (TaskMapObject) getChildAt(i);
                if (!child.isDying()) {
                    child.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    child.setDying(false);
                                    removeView(child);
                                }
                            });
                    child.setDying(true);
                    break;
                }
            }
        }
        super.addView(taskMapObject);
        taskMapObject.setAlpha(0f);
        taskMapObject.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        taskMapObject.setSpawning(false);
                    }
                });
        taskMapObject.setSpawning(true);
    }

    @Deprecated
    @Override
    public void removeView(MapObject mapObject) {
        if (mapObject instanceof TaskMapObject) {
            removeView((TaskMapObject) mapObject);
        }
    }

    public void removeView(TaskMapObject taskMapObject) {
        super.removeView(taskMapObject);
    }

    public void addTask(final ElasticSearchTask task){
        ((Activity)getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TaskMapObject view = new TaskMapObject(getContext(), task){
                    @Override
                    public void hideTaskMapObject(){
                        super.hideTaskMapObject();
                        finishedPosts.add(getTask().getID());
                    }
                };
                addView(view);
            }
        });
    }

    public ArrayList<String> getFinishedPosts(){
        return finishedPosts;
    }
}
