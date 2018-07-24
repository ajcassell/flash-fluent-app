package ait.android.flashfluent.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Language.class}, version=1)
public abstract class AppDatabaseLanguages extends RoomDatabase {

    private static AppDatabaseLanguages INSTANCE;

    public abstract LanguageDAO languageDao();

    public static AppDatabaseLanguages getAppDatabaseLanguages(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabaseLanguages.class, "language-database").build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}