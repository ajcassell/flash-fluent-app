package ait.android.flashfluent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ait.android.flashfluent.adapter.VocabListRecyclerAdapter;
import ait.android.flashfluent.data.AppDatabase;
import ait.android.flashfluent.data.AppDatabaseLanguages;
import ait.android.flashfluent.data.Language;
import ait.android.flashfluent.data.VocabItem;
import ait.android.flashfluent.touch.VocabListItemTouchHelperCallback;

public class FlashcardActivity extends AppCompatActivity implements AddOrEditDialog.ItemHandler, TextToSpeech.OnInitListener {

    public static final String KEY_CAT = "KEY_CAT";
    public static final String KEY_LANG = "KEY_LANG";
    public static final String TAG = "FlashcardActivity";
    private Context context;
    private VocabListRecyclerAdapter vocabListRecyclerAdapter;
    private ArrayList<String> defaultWords;

    public static String buttonPressed; // can be "new" or "edit"
    public final static String KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT";
    private TextView tvLanguage;
    private TextView tvCategory;
    private Button btnAdd;
    private Button btnDeleteCustom;
    private TextView tvPhrase;

    String language;
    String category;
    boolean isNew;
    private String[][][] translatedDefaultWords;
    // has order [lang][cat][word]
    // has order FOODS, PLACES, GREETINGS, PHRASES, RANDOM
    // has order FRENCH, GERMAN, HUNGARIAN, ITALIAN, SPANISH
    // has the word order that's already determined earlier

    private TextToSpeech tts;
    private android.speech.SpeechRecognizer sr;

    public Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        RecyclerView recyclerView = findViewById(R.id.recyclerFlashcardList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent i = getIntent();
        language = i.getExtras().getString(getString(R.string.language));
        category = i.getExtras().getString(getString(R.string.category));

        isNew = i.getExtras().getBoolean(getString(R.string.isNew));
        defaultWords = i.getExtras().getStringArrayList(getString(R.string.list));

        populateTranslatedDefaultWordsMatrix();

        initVocabItems(recyclerView);

        tvLanguage = findViewById(R.id.tvLanguage);
        tvCategory = findViewById(R.id.tvCategory);
        tvLanguage.setText(language);
        tvCategory.setText(category);

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPressed = getString(R.string.add);
                showAddOrEditDialog();
            }
        });

        btnDeleteCustom = findViewById(R.id.btnDeleteCustom);
        btnDeleteCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllCustom();
            }
        });

        tts = new TextToSpeech(this, this);

        tvPhrase = findViewById(R.id.tvPhrase);

        requestNeededPermission();
    }

    private void populateTranslatedDefaultWordsMatrix() {
        // has order FRENCH, GERMAN, HUNGARIAN, ITALIAN, SPANISH
        // has order FOODS, PLACES, GREETINGS, PHRASES, RANDOM
        translatedDefaultWords = new String[][][]
                {{{"Eau", "Café", "Banane", "Poulet", "Salade"},
                        {"Salle de bains", "Hôpital", "Restaurant", "Police", "Arrêt de bus"},
                        {"Bonjour", "Comment allez-vous?", "Bonjour", "Bonjour", "Bonsoir"},
                        {"Merci", "S'il vous plaît", "Excusez-moi", "Je m'appelle", "Je viens de"},
                        {"Carte de crédit", "Billet", "Étudiant", "Taxi", "Téléphone portable"}},

                        {{"Wasser", "Kaffee", "Banane", "Hähnchen", "Salat"},
                                {"Badezimmer", "Krankenhaus", "Restaurant", "Hähnchen", "Bushaltestelle"},
                                {"Hallo, Guten Tag", "Wie geht es Ihnen?", "Guten Morgen", "Guten Nachmittag", "Guten Abend"},
                                {"Vielen Dank", "Bitte", "Entschuldigen Sie", "Ich bin", "Ich komme aus"},
                                {"Kreditkarte", "Fahrkarte", "Schüler", "Taxi", "Handy"}},
                        {{"Víz", "Kávé", "Banán", "Csirke", "Saláta"},
                                {"Fürdőszoba", "Kórház", "Étterem", "Rendőrség", "Buszmegálló"},
                                {"Szia", "Hogy vagy?", "Jó reggelt", "Jó napot", "Jó estét"},
                                {"Köszönöm", "Kérem", "Bocsánat", "Én vagyok", "___ vagyok"},
                                {"Hitelkártya", "Jegy", "Diák", "Taxi", "Mobiltelefon"}},
                        {{"Acqua", "Caffè", "Banana", "Pollo", "Insalata"},
                                {"Bagno", "Ospedale", "Ristorante", "Polizia", "Fermata dell'autobus"},
                                {"Ciao", "Come stai?", "Buongiorno", "Buon pomeriggio", "Buonasera"},
                                {"Grazie", "Per favore", "Scusami", "Mi chiamo", "Sono di"},
                                {"Carta di credito", "Biglietto", "Alunno", "Taxi", "Cellulare"}},
                        {{"Agua", "Café", "Plátano", "Pollo", "Ensalada"},
                                {"Baño", "Hospital", "Restaurante", "Policía", "Parada de autobús"},
                                {"Hola", "¿Cómo estás?", "Buenos días", "Buenas tardes", "Buenas tardes"},
                                {"Gracias", "Por favor", "Disculpe", "Me llamo", "Soy de"},
                                {"Tarjeta de crédito", "Boleto", "Estudiante", "Taxi", "Teléfono móvil"}}};

    }

    public void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(FlashcardActivity.this, "I need it for camera", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
        } else {
            sr = android.speech.SpeechRecognizer.createSpeechRecognizer(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(FlashcardActivity.this, R.string.record_granted, Toast.LENGTH_SHORT).show();
                    sr = android.speech.SpeechRecognizer.createSpeechRecognizer(this);
                } else {
                    Toast.makeText(FlashcardActivity.this, R.string.record_not_granted, Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (sr != null) {
                sr.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            Locale locale = getLocale();

            int result = tts.setLanguage(locale);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            } else { //
            }
        } else {
            Intent installIntent = new Intent();
            installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
            startActivity(installIntent);
        }
    }

    private Locale getLocale() {
        Locale loc;
        switch (language) {
            case "Chinese":
                loc = Locale.CHINESE;
                break;
            case "French":
                loc = Locale.FRENCH;
                break;
            case "German":
                loc = Locale.GERMAN;
                break;
            case "Italian":
                loc = Locale.ITALIAN;
                break;
            case "Japanese":
                loc = Locale.JAPANESE;
                break;
            case "Korean":
                loc = Locale.KOREAN;
                break;
            default:
                loc = Locale.ENGLISH;
                break;
        }
        return loc;
    }

    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    // loop through the vocab in this lang/cat and delete the ones that are CUSTOM (NOT default)
    private void deleteAllCustom() {
        new Thread() {
            @Override
            public void run() {
                final List<VocabItem> list = AppDatabase.getAppDatabase(FlashcardActivity.this).vocabItemDao().getAllForLanguageAndCategory(language, category);
                final ArrayList<VocabItem> listToDelete = new ArrayList<VocabItem>();
                for (final VocabItem item : list) {
                    if (!item.isDefault()) {
                        listToDelete.add(item);
                    }
                }
                AppDatabase.getAppDatabase(FlashcardActivity.this).vocabItemDao().deleteAll(listToDelete);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vocabListRecyclerAdapter.deleteAllCustom(listToDelete);
                    }
                });
            }
        }.start();
    }

    private void initVocabItems(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                // this is where the necessary flashcards show up
                final List<VocabItem> items = AppDatabase.getAppDatabase(FlashcardActivity.this).vocabItemDao().getAllForLanguageAndCategory(language, category);
                Language tempLang = AppDatabaseLanguages.getAppDatabaseLanguages(FlashcardActivity.this).languageDao().getLanguageWithThisName(language).get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vocabListRecyclerAdapter = new VocabListRecyclerAdapter(items, FlashcardActivity.this);
                        recyclerView.setAdapter(vocabListRecyclerAdapter);
                        ItemTouchHelper.Callback callback = new VocabListItemTouchHelperCallback(vocabListRecyclerAdapter);
                        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
                        touchHelper.attachToRecyclerView(recyclerView);
                    }
                });
                if (!tempLang.isDefaultMadeForThisCategory(category) && tempLang.isDefault()) {
                    addDefaultWords(language, category);
                    tempLang.setDefaultMadeForThisCategory(category);
                    AppDatabaseLanguages.getAppDatabaseLanguages(FlashcardActivity.this).languageDao().update(tempLang);
                }
            }
        }.start();
    }

    public void editItem(VocabItem item) {
        AddOrEditDialog dialog = new AddOrEditDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEM_TO_EDIT, item);
        bundle.putString(KEY_CAT, category);
        bundle.putString(KEY_LANG, language);
        dialog.setArguments(bundle);
        buttonPressed = getString(R.string.edit);
        dialog.show(getSupportFragmentManager(), getString(R.string.add_or_edit_dialog));
    }

    private void showAddOrEditDialog() {
        AddOrEditDialog addOrEditDialog = new AddOrEditDialog();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CAT, category);
        bundle.putString(KEY_LANG, language);
        addOrEditDialog.setArguments(bundle);
        addOrEditDialog.show(getSupportFragmentManager(), getString(R.string.add_or_edit_dialog));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                Intent mapIntent = new Intent(FlashcardActivity.this, LanguageSelectionActivity.class);
                FlashcardActivity.this.startActivity(mapIntent);
                FlashcardActivity.this.finish();
                break;
            case R.id.action_info:
                showInfoDialog();
                break;

        }
        return true;
    }

    private void showInfoDialog() {
        new InfoDialog().show(getSupportFragmentManager(), getString(R.string.info_dialog));

    }

    @Override
    // a problematic method: SOMETIMES NULL POINTER EXCEPTION BUT GOES AWAY IF YOU REINSTALL APP
    public void onNewItemCreated(final String name, final String translatedName, final boolean isDefault, final String language, final String category) {
        new Thread() {
            @Override
            public void run() {
                final VocabItem newItem = new VocabItem(name, translatedName, isDefault, language, category);

                long id = AppDatabase.getAppDatabase(FlashcardActivity.this).vocabItemDao().insertVocabItem(newItem);
                newItem.setVocabItemId(id);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vocabListRecyclerAdapter.addItem(newItem);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onItemUpdated(final VocabItem item) {
        new Thread() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(FlashcardActivity.this).vocabItemDao().update(item);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vocabListRecyclerAdapter.updateItem(item);
                    }
                });
            }
        }.start();
    }

    public void showMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_flashcard), message, Snackbar.LENGTH_LONG).show();
    }

    private void addDefaultWords(String language, String category) {
        int categoryIndex = getCategoryIndex(category);
        int languageIndex = getLanguageIndex(language);

        // loop through default words of this category
        for (int i = 0; i < defaultWords.size(); i++) {
            String name = defaultWords.get(i);
            String translatedName = translatedDefaultWords[languageIndex][categoryIndex][i];
            onNewItemCreated(name, translatedName, true, language, category);
        }
    }

    private int getLanguageIndex(String language) {
        int languageIndex = 0;
        switch (language) {
            case "French":
                languageIndex = 0;
                break;
            case "German":
                languageIndex = 1;
                break;
            case "Hungarian":
                languageIndex = 2;
                break;
            case "Italian":
                languageIndex = 3;
                break;
            case "Spanish":
                languageIndex = 4;
                break;
        }
        return languageIndex;
    }

    private int getCategoryIndex(String category) {
        int categoryIndex = 0;
        switch (category) {
            case "Foods":
                categoryIndex = 0;
                break;
            case "Places":
                categoryIndex = 1;
                break;
            case "Greetings":
                categoryIndex = 2;
                break;
            case "Phrases":
                categoryIndex = 3;
                break;
            case "Random":
                categoryIndex = 4;
                break;
        }
        return categoryIndex;
    }

}
