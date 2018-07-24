package ait.android.flashfluent.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class VocabItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long vocabItemId;

    @ColumnInfo(name="vocab_item_name")
    private String vocabItemName;
    @ColumnInfo(name="translated_name")
    private String translatedName;
    @ColumnInfo(name="default")
    private boolean isDefault;
    @ColumnInfo(name="parentLanguage")
    private String parentLanguage;
    @ColumnInfo(name="parentCategory")
    private String parentCategory;

    public VocabItem(String vocabItemName, String translatedName, boolean isDefault, String parentLanguage, String parentCategory) {
        this.vocabItemName = vocabItemName;
        this.translatedName = translatedName;
        this.parentCategory = parentCategory;
        this.parentLanguage = parentLanguage;
        this.isDefault = isDefault;
    }

    public long getVocabItemId() {
        return vocabItemId;
    }

    public void setVocabItemId(long vocabItemId) {
        this.vocabItemId = vocabItemId;
    }

    public String getVocabItemName() {
        return vocabItemName;
    }

    public void setVocabItemName(String vocabItemName) {
        this.vocabItemName = vocabItemName;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getParentLanguage() {
        return parentLanguage;
    }

    public void setParentLanguage(String parentLanguage) {
        this.parentLanguage = parentLanguage;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }
}
