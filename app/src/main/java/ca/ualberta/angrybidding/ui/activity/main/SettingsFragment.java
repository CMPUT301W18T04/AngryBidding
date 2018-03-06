package ca.ualberta.angrybidding.ui.activity.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.slouple.android.AdvancedPreferenceFragment;
import com.slouple.android.SharedPreferenceChangeListener;

import ca.ualberta.angrybidding.R;

public class SettingsFragment extends AdvancedPreferenceFragment implements IMainFragment{
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    @Override
    public AppBarLayout getAppBarLayout(ViewGroup rootView, LayoutInflater inflater) {
        if(appBarLayout == null){
            appBarLayout = (AppBarLayout) inflater.inflate(R.layout.settings_fragment_toolbar, rootView, false);
        }
        return appBarLayout;

    }

    @Override
    public Toolbar getToolbar(ViewGroup rootView, LayoutInflater inflater){
        if(toolbar == null){
            toolbar = (Toolbar) getAppBarLayout(rootView, inflater).findViewById(R.id.settings_fragment_toolbar);
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
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_settings);
        triggerSharedPreferenceChangeListener("mapViewResolution", 4);

        //Zoom In Center
        addSharedPreferenceChangeListener(new SharedPreferenceChangeListener<Boolean>("testSwitch") {
            @Override
            public void onChange(Boolean value) {
                Log.d("Settings Fragment", "Switched!");
            }
        });
        triggerSharedPreferenceChangeListener("testSwitch", false);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
