package ca.ualberta.angrybidding.ui.activity.main;


import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Interface for MainActivity Fragments
 */
public interface IMainFragment {
    /**
     * Get the AppBarLayout of the fragment
     *
     * @param rootView
     * @param inflater
     * @return AppBarLayout of the fragment
     */
    AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater);

    /**
     * Gets the Toolbar of the fragment
     *
     * @param rootView
     * @param inflater
     * @return Toolbar of the fragment
     */
    Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater);

    /**
     * Calls when ActionBar is added in MainActivity for the fragment
     *
     * @param actionBar
     */
    void onActionBarAdded(ActionBar actionBar);

    /**
     * Return false if fragment should cover entire MainActivity including ActionBar area
     *
     * @return
     */
    boolean shouldOffsetForToolbar();

    /**
     * Calls when fragments becomes visible
     */
    void onVisible();

    /**
     * Calls when back is pressed while in current fragment
     *
     * @return Is back pressed handled
     */
    boolean onBackPressed();
}
