package com.haraevanton.easytodo.di;

import com.haraevanton.easytodo.di.modules.DbModule;
import com.haraevanton.easytodo.di.modules.RepositoryModule;
import com.haraevanton.easytodo.mvp.model.TaskRepository;
import com.haraevanton.easytodo.mvp.presenters.MainActivityPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class, DbModule.class})
public interface AppComponent {
    void injectPresenter(MainActivityPresenter mainActivityPresenter);

    void injectRepository(TaskRepository taskRepository);

}
