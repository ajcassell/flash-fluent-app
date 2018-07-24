package ait.android.flashfluent.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface VocabItemDAO {

    @Query("SELECT * FROM vocabItem")
    List<VocabItem> getAll();

    @Query("SELECT * FROM vocabItem WHERE parentLanguage = :language AND parentCategory = :category")
    List<VocabItem> getAllForLanguageAndCategory(String language, String category);

    @Insert
    long insertVocabItem(VocabItem item);

    @Delete
    void delete(VocabItem item);

    @Delete
    void deleteAll(List<VocabItem> item);

    @Update
    void update(VocabItem item);

}
