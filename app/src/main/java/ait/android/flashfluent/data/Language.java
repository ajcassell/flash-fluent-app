package ait.android.flashfluent.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Language {

    @PrimaryKey(autoGenerate = true)
    private long languageId;

    @ColumnInfo(name="language_name")
    private String languageName;

    @ColumnInfo(name="is_default")
    private boolean isDefault;

    @ColumnInfo(name="all_default_made")
    private boolean allDefaultMade;
    @ColumnInfo(name="food_default_made")
    private boolean foodDefaultMade;
    @ColumnInfo(name="places_default_made")
    private boolean placesDefaultMade;
    @ColumnInfo(name="phrases_default_made")
    private boolean phrasesDefaultMade;
    @ColumnInfo(name="greetings_default_made")
    private boolean greetingsDefaultMade;
    @ColumnInfo(name="random_default_made")
    private boolean randomDefaultMade;

    public Language(String languageName, boolean isDefault) {
        this.languageName = languageName;
        this.foodDefaultMade = false;
        this.placesDefaultMade = false;
        this.greetingsDefaultMade = false;
        this.phrasesDefaultMade = false;
        this.randomDefaultMade = false;
        this.isDefault = isDefault;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(long languageId) {
        this.languageId = languageId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isDefaultMadeForThisCategory(String category) {
        boolean returnValue = false;
        switch(category) {
            case "Foods":
                returnValue = isFoodDefaultMade();
                break;
            case "Places":
                returnValue = isPlacesDefaultMade();
                break;
            case "Greetings":
                returnValue = isGreetingsDefaultMade();
                break;
            case "Phrases":
                returnValue = isPhrasesDefaultMade();
                break;
            case "Random":
                returnValue = isRandomDefaultMade();
                break;
            case "Default":
                returnValue = false;
                break;
        }
        return returnValue;
    }

    public void setDefaultMadeForThisCategory(String category) {
        switch(category) {
            case "Foods":
                setFoodDefaultMade(true);
                break;
            case "Places":
                setPlacesDefaultMade(true);
                break;
            case "Greetings":
                setGreetingsDefaultMade(true);
                break;
            case "Phrases":
                setPhrasesDefaultMade(true);
                break;
            case "Random":
                setRandomDefaultMade(true);
                break;
            case "Default":// ????
                break;
        }
    }


    public boolean isFoodDefaultMade() {
        return foodDefaultMade;
    }

    public void setFoodDefaultMade(boolean foodDefaultMade) {
        this.foodDefaultMade = foodDefaultMade;
    }

    public boolean isPlacesDefaultMade() {
        return placesDefaultMade;
    }

    public void setPlacesDefaultMade(boolean placesDefaultMade) {
        this.placesDefaultMade = placesDefaultMade;
    }

    public boolean isPhrasesDefaultMade() {
        return phrasesDefaultMade;
    }

    public void setPhrasesDefaultMade(boolean phrasesDefaultMade) {
        this.phrasesDefaultMade = phrasesDefaultMade;
    }

    public boolean isGreetingsDefaultMade() {
        return greetingsDefaultMade;
    }

    public void setGreetingsDefaultMade(boolean greetingsDefaultMade) {
        this.greetingsDefaultMade = greetingsDefaultMade;
    }

    public boolean isRandomDefaultMade() {
        return randomDefaultMade;
    }

    public void setRandomDefaultMade(boolean randomDefaultMade) {
        this.randomDefaultMade = randomDefaultMade;
    }

    public boolean isAllDefaultMade() {
        return allDefaultMade;
    }

    public void setAllDefaultMade(boolean allDefaultMade) {
        this.allDefaultMade = allDefaultMade;
    }


}
