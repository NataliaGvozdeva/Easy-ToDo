package com.haraevanton.tasks09.di;

import com.haraevanton.tasks09.di.modules.RepositoryModule;
import com.haraevanton.tasks09.mvp.presenters.MainActivityPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RepositoryModule.class})
public interface AppComponent {
    void injectPresenter(MainActivityPresenter mainActivityPresenter);

}
