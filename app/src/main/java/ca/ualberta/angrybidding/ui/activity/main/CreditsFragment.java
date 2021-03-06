package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.slouple.android.AdvancedFragment;

import ca.ualberta.angrybidding.R;

public class CreditsFragment extends AdvancedFragment implements IMainFragment{
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if(appBarLayout == null){
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.credits_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater){
        if(toolbar == null){
            toolbar = (Toolbar) getAppBarLayout(rootView, inflater).findViewById(R.id.credits_fragment_toolbar);
        }
        return toolbar;
    }

    @Override
    public void onActionBarAdded(ActionBar actionBar){

    }

    @Override
    public boolean shouldOffsetForToolbar() {
        return true;
    }

    @Override
    public void onVisible() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_credits, container, false);
        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
