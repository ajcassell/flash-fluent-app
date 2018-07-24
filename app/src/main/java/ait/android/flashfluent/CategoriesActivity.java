package ait.android.flashfluent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoriesActivity extends AppCompatActivity {

    TextView tvLanguageName;

    Button btnGreetings;
    Button btnPhrases;
    Button btnFoods;
    Button btnPlaces;
    Button btnRandom;

    ArrayList<String> defaultGreetings;
    ArrayList<String> defaultPhrases;
    ArrayList<String> defaultFoods;
    ArrayList<String> defaultPlaces;
    ArrayList<String> defaultRandom;

    boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        tvLanguageName = findViewById(R.id.tvLanguageName);
        Intent i = getIntent();
        final String language = i.getExtras().getString("Language");
        isNew = i.getExtras().getBoolean("isNew");
        tvLanguageName.setText(language);

        btnGreetings = findViewById(R.id.btnGreetings);
        btnPhrases = findViewById(R.id.btnPhrases);
        btnFoods = findViewById(R.id.btnFoods);
        btnPlaces = findViewById(R.id.btnPlaces);
        btnRandom = findViewById(R.id.btnRandom);

        defaultGreetings = new ArrayList<String>(Arrays.asList("Hello", "How are you?", "Good morning", "Good afternoon", "Good evening"));
        defaultPhrases = new ArrayList<String>(Arrays.asList("Thank you", "Please", "Excuse me", "My name is", "I am from"));
        defaultFoods = new ArrayList<String>(Arrays.asList("Water", "Coffee", "Banana", "Chicken", "Salad"));
        defaultPlaces = new ArrayList<String>(Arrays.asList("Bathroom", "Hospital", "Restaurant", "Police station", "Bus stop"));
        defaultRandom = new ArrayList<String>(Arrays.asList("Credit card", "ticket", "Student", "Taxi", "Cell phone"));

        setButtonActions(language);
    }

    // sets the button actions so that we start the flashcard activity and send the language, isNew, and category
    private void setButtonActions(final String language) {

        btnGreetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flashcardIntent = new Intent(CategoriesActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra(getString(R.string.language), language);
                flashcardIntent.putExtra(getString(R.string.category), getString(R.string.greetings));
                flashcardIntent.putExtra(getString(R.string.isNew), isNew);
                flashcardIntent.putStringArrayListExtra(getString(R.string.list), defaultGreetings);
                CategoriesActivity.this.startActivity(flashcardIntent);
            }
        });

        btnPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flashcardIntent = new Intent(CategoriesActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra(getString(R.string.language), language);
                flashcardIntent.putExtra(getString(R.string.category), getString(R.string.phrases));
                flashcardIntent.putExtra(getString(R.string.isNew), isNew);
                flashcardIntent.putStringArrayListExtra(getString(R.string.list), defaultPhrases);
                CategoriesActivity.this.startActivity(flashcardIntent);
            }
        });

        btnFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flashcardIntent = new Intent(CategoriesActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra(getString(R.string.language), language);
                flashcardIntent.putExtra(getString(R.string.category), getString(R.string.foods));
                flashcardIntent.putExtra(getString(R.string.isNew), isNew);
                flashcardIntent.putStringArrayListExtra(getString(R.string.list), defaultFoods);
                CategoriesActivity.this.startActivity(flashcardIntent);
            }
        });

        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flashcardIntent = new Intent(CategoriesActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra(getString(R.string.language), language);
                flashcardIntent.putExtra(getString(R.string.category), getString(R.string.places));
                flashcardIntent.putExtra(getString(R.string.isNew), isNew);
                flashcardIntent.putStringArrayListExtra(getString(R.string.list), defaultPlaces);
                CategoriesActivity.this.startActivity(flashcardIntent);
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent flashcardIntent = new Intent(CategoriesActivity.this, FlashcardActivity.class);
                flashcardIntent.putExtra(getString(R.string.language), language);
                flashcardIntent.putExtra(getString(R.string.category), getString(R.string.random));
                flashcardIntent.putExtra(getString(R.string.isNew), isNew);
                flashcardIntent.putStringArrayListExtra(getString(R.string.list), defaultRandom);
                CategoriesActivity.this.startActivity(flashcardIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_map:
                Intent mapIntent = new Intent(CategoriesActivity.this, LanguageSelectionActivity.class);
                CategoriesActivity.this.startActivity(mapIntent);
                CategoriesActivity.this.finish();
                break;
            case R.id.action_info:
                showInfoDialog();
        }
        return true;
    }

    private void showInfoDialog() {
        new InfoDialog().show(getSupportFragmentManager(), getString(R.string.info_dialog));
    }
}
