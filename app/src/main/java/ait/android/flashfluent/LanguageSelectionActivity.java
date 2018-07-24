package ait.android.flashfluent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ait.android.flashfluent.adapter.LanguageListRecyclerAdapter;
import ait.android.flashfluent.data.AppDatabaseLanguages;
import ait.android.flashfluent.data.Language;
import ait.android.flashfluent.touch.LanguageListTouchHelperCallback;

public class LanguageSelectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button btnEnter;
    private LanguageListRecyclerAdapter languageListRecyclerAdapter;
    static boolean isNewLanguage = false;
    private Spinner spinner;
    boolean open = false;
    private Context context;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        open = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        btnEnter = findViewById(R.id.btnEnter);
        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.recyclerLanguageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Can only enter new languages
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        isNewLanguage = true;
                        List<Language> list = AppDatabaseLanguages.getAppDatabaseLanguages(LanguageSelectionActivity.this).languageDao().getAll();
                        // determines if this is a language already in the database
                        for (Language lang: list) {
                            if (lang.getLanguageName().equals(spinner.getSelectedItem().toString())) {
                                isNewLanguage = false;
                            }
                        }
                        if (isNewLanguage) {
                            // this "isNewLanguage" also accounts for the fact that it is NOT DEFAULT!
                            onNewItemCreated(spinner.getSelectedItem().toString(), false);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView errorText = (TextView)spinner.getSelectedView();
                                    errorText.setError("");
                                    errorText.setTextColor(Color.RED);
                                }
                            });
                        }
                    }
                }.start();
            }
        });
    }

    private void initLanguageItems(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<Language> languages = AppDatabaseLanguages.getAppDatabaseLanguages(LanguageSelectionActivity.this).languageDao().getAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        languageListRecyclerAdapter = new LanguageListRecyclerAdapter(languages, LanguageSelectionActivity.this);
                        recyclerView.setAdapter(languageListRecyclerAdapter);

                        ItemTouchHelper.Callback callback = new LanguageListTouchHelperCallback(languageListRecyclerAdapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerView);

                        // add default languages on first run
                        if (isFirstRun()) {
                            onNewItemCreated(getString(R.string.spanish), true);
                            onNewItemCreated(getString(R.string.french), true);
                            onNewItemCreated(getString(R.string.german), true);
                            onNewItemCreated(getString(R.string.italian), true);
                            onNewItemCreated(getString(R.string.hungarian), true);

                            saveThatItWasStarted();
                        }
                    }
                });
            }
        }.start();
    }

    public boolean isFirstRun() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_first),true)) {
        }
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.key_first),true);
    }

    public void saveThatItWasStarted() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(getString(R.string.key_first),false);
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initLanguageItems(recyclerView);
    }

    //@Override
    public void onNewItemCreated(final String languageName, final boolean isDefault) {
        new Thread() {
            @Override
            public void run() {
                final Language newLanguage = new Language(languageName, isDefault);

                long id = AppDatabaseLanguages.getAppDatabaseLanguages(LanguageSelectionActivity.this).languageDao().insertLanguage(newLanguage);
                newLanguage.setLanguageId(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        languageListRecyclerAdapter.addItem(newLanguage);
                    }
                });
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_info:
                showInfoDialog();
                break;
            case R.id.action_map:
                Intent mapIntent = new Intent(LanguageSelectionActivity.this, MapActivity.class);
                LanguageSelectionActivity.this.startActivity(mapIntent);
                break;
        }
        return true;
    }

    private void showInfoDialog() {
        new InfoDialog().show(getSupportFragmentManager(), getString(R.string.info_dialog));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
