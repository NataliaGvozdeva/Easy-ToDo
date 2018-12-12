package com.haraevanton.easytodo.di.modules;

import com.haraevanton.easytodo.mvp.model.TaskRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {
    @Provides
    @Singleton
    public TaskRepository provideRepository() {
        return TaskRepository.get();
    }
}
