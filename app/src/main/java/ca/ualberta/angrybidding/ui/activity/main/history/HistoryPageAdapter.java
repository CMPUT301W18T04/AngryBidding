package ca.ualberta.angrybidding.ui.activity.main.history;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.ualberta.angrybidding.R;

/**
 * The adapter for the history fragment
 * And it will connect all the history fragments to the UI
 */
public class HistoryPageAdapter extends FragmentPagerAdapter {
    private TaskPostedFragment taskPostedFragment;
    private TaskProvidedFragment taskProvidedFragment;
    private Context context;

    /**
     * @param fm FragmentManager
     * @param context Context
     */
    public HistoryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        taskPostedFragment = new TaskPostedFragment();
        taskProvidedFragment = new TaskProvidedFragment();
    }

    /**
     * Links position with appropriate fragment
     * @param position
     * @return Fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return taskPostedFragment;
            case 1:
                return taskProvidedFragment;
            default:
                return null;
        }
    }

    /**
     * Links position with appropriate title
     * @param position
     * @return Page Title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.taskPosted);
            case 1:
                return context.getString(R.string.taskProvided);
            default:
                return null;
        }
    }

    /**
     * Number of history page fragments
     * @return
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * @return TaskPostedFragment
     */
    public TaskPostedFragment getTaskPostedFragment() {
        return taskPostedFragment;
    }

    /**
     * @return TaskProvidedFragment
     */
    public TaskProvidedFragment getTaskProvidedFragment() {
        return taskProvidedFragment;
    }
}
