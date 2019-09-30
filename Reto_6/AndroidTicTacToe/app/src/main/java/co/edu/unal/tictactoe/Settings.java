package co.edu.unal.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Switch;

public class Settings extends PreferenceActivity {
    private int mColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Message
        final EditTextPreference victoryMessagePref = (EditTextPreference)
                findPreference("victory_message");
        String victoryMessage = prefs.getString("victory_message",
        getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary((CharSequence)victoryMessage);
        victoryMessagePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                victoryMessagePref.setSummary((CharSequence) newValue);
// Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("victory_message", newValue.toString());
                ed.commit();
                return true;
            }
        });

        
        //Difficulty
        final ListPreference difficultyLevelPref = (ListPreference) findPreference("difficulty_level");
        String difficulty = prefs.getString("difficulty_level",
        getResources().getString(R.string.difficulty_expert));
        difficultyLevelPref.setSummary((CharSequence) difficulty);
        difficultyLevelPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                difficultyLevelPref.setSummary((CharSequence) newValue);
// Since we are handling the pref, we must save it
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", newValue.toString());
                ed.commit();
                return true;
            }
        });


        final Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("board_color");
        dialogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // dialog code here
                mColor = prefs.getInt("board_color",Color.LTGRAY);
                new ColorPickerDialog(Settings.this, new UpdateColor(), mColor).show();
                return true;
            }
        });
        final SwitchPreference soundPreference = (SwitchPreference) getPreferenceScreen().findPreference("sound");

        final Preference resetPreference = (Preference) getPreferenceScreen().findPreference("reset_conf");
        resetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("difficulty_level", getResources().getString(R.string.difficulty_expert));
                ed.putString("victory_message", getResources().getString(R.string.result_human_wins));
                ed.putInt("board_color", Color.LTGRAY);
                ed.putBoolean("sound",true);
                ed.commit();
                // dialog code here
                difficultyLevelPref.setSummary((CharSequence) getResources().getString(R.string.difficulty_expert));
                victoryMessagePref.setSummary((CharSequence) getResources().getString(R.string.result_human_wins));
                soundPreference.setChecked(true);


                mColor = Color.LTGRAY;
                return true;
            }
        });
    }



    public class UpdateColor implements ColorPickerDialog.OnColorChangedListener {
        public void colorChanged(int color) {
            //ShowColor.setBackgroundColor(color);
            //show the color value
            final SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor ed = prefs.edit();
            ed.putInt("board_color", color);
            ed.commit();


        }
    }

}
