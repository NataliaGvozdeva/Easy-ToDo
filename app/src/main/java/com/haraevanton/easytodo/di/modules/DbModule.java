package com.haraevanton.easytodo.di.modules;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.haraevanton.easytodo.App;
import com.haraevanton.easytodo.room.AppDatabase;
import com.haraevanton.easytodo.room.TaskDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DbModule {
    @Provides
    @Singleton
    public Context provideAppContext() {
        return App.getContext();
    }

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    @Provides
    @Singleton
    public TaskDao provideTaskDao(AppDatabase appDatabase) {
        return appDatabase.taskDao();
    }
}
