package ca.ualberta.angrybidding.ui.activity.main;


import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public interface IMainFragment {
    AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater);
    Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater);
    void onActionBarAdded(ActionBar actionBar);

    boolean shouldOffsetForToolbar();
    void onVisible();
    boolean onBackPressed();
}
