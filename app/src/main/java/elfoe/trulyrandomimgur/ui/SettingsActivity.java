package elfoe.trulyrandomimgur.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import elfoe.trulyrandomimgur.R;

public class SettingsActivity extends AppCompatActivity {
    public static int MINIMUM_WIDTH;
    public static int MINIMUM_HEIGHT;
    public static int MINIMUM_BYTES;

    static Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_holder);


        Toolbar toolbar = (Toolbar) findViewById(R.id.preference_toolbar);
        setSupportActionBar(toolbar);
        getActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;
        getFragmentManager().beginTransaction().replace(R.id.frame, new GeneralPreferenceFragment()).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            switch (id) {
                case android.R.id.home: {
                    onBackPressed();
                    return true;
                }
                case R.id.reset:
                    Log.e("RESEt","settings");
//                    PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.pref_general, true);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    getFragmentManager().beginTransaction().replace(R.id.frame, new GeneralPreferenceFragment()).commit();
                    return true;
            }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.preferences_actions, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("maximum_urls_pref"));
            bindPreferenceSummaryToValue(findPreference("minimum_width_images"));
            bindPreferenceSummaryToValue(findPreference("minimum_height_images"));
            bindPreferenceSummaryToValue(findPreference("minimum_bytes"));

            bindPreferenceToBooleanListener(findPreference("show_url_per_sec_check"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);
        }
    }


    private static void bindPreferenceToBooleanListener(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceToListener);
        sBindPreferenceToListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
    }
    private static Preference.OnPreferenceChangeListener sBindPreferenceToListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            boolean checked = (Boolean) value;
            if (checked){
                TrulyRandom.urlsPerSec.setVisibility(View.VISIBLE);
            } else {
                TrulyRandom.urlsPerSec.setVisibility(View.GONE);
            }
            return true;
        }
    };



    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else if (preference.getKey().equalsIgnoreCase("minimum_bytes")) {

                preference.setSummary(bytesToString(stringValue));

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                if (!(stringValue.equalsIgnoreCase(""))) {
                    preference.setSummary(stringValue);
                } else {
                    //preference.setSummary(preference.set);
                    preference.setSummary("none");
                }
            }
            if (preference.getKey().equals("minimum_width_images")) {
                MINIMUM_WIDTH = parsePrefString(stringValue);
            } else if (preference.getKey().equals("minimum_height_images")) {
                MINIMUM_HEIGHT = parsePrefString(stringValue);
            } else if (preference.getKey().equals("minimum_bytes")) {
                MINIMUM_BYTES = parsePrefString(stringValue);
            }
            return true;
        }
    };
    static String bytesToString(String str){
        float bytes;
        if (str == null || str.equals("")){
            bytes = 0;
        } else {
            bytes = (float)Long.parseLong(str);
        }
        String retVal = "%.1f";
        if (bytes > 1073741824) {
            bytes = bytes/1073741824;
            retVal = String.format(retVal, bytes);
            retVal += " GB";
        } else if (bytes > 1048576) {
            bytes = bytes/1048576;
            retVal = String.format(retVal, bytes);
            retVal += " MB";
        } else if (bytes > 1024){
            bytes = bytes/1024;
            retVal = String.format(retVal, bytes);
            retVal += " kB";
        } else {
            retVal = String.format(retVal, bytes);
            retVal += " bytes";
        }
        return retVal;
    }

    static int parsePrefString(String str){
        if ((str == null) || (str == "")){
            return 0;
        }
        return Integer.parseInt(str);
    }
}
