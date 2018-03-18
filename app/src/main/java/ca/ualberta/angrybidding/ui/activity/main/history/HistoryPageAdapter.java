package ca.ualberta.angrybidding.ui.activity.main.history;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.ualberta.angrybidding.R;

/*
* This class is the adapter for the history fragment
* And it will connect all the history fragments to the UI
*/
public class HistoryPageAdapter extends FragmentPagerAdapter {
    private TaskPostedFragment taskPostedFragment;
    private TaskProvidedFragment taskProvidedFragment;
    private Context context;

    /*
     * This method connects the pager to the UI
     */
    public HistoryPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        taskPostedFragment = new TaskPostedFragment();
        taskProvidedFragment = new TaskProvidedFragment();
    }

    /*
     * This method returns task posted fragment and task provided fragment on need
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

    /*
     * This method returns the titles for the two pagers
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

    @Override
    public int getCount() {
        return 2;
    }

    public TaskPostedFragment getTaskPostedFragment() {
        return taskPostedFragment;
    }

    public TaskProvidedFragment getTaskProvidedFragment() {
        return taskProvidedFragment;
    }
}
