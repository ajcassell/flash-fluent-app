package ait.android.flashfluent.data;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface LanguageDAO {

    @Query("SELECT * FROM language")
    List<Language> getAll();

    @Query("SELECT * FROM language WHERE language_name =:languageName")
    List<Language> getLanguageWithThisName(String languageName);

    @Insert
    long insertLanguage(Language lang);

    @Delete
    void delete(Language lang);

    @Delete
    void deleteAll(List<Language> lang);

    @Update
    void update(Language lang);

}