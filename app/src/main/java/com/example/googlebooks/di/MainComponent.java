package com.example.googlebooks.di;

import android.content.Context;

import com.example.googlebooks.ui.BookSearchActivity;

import dagger.Component;

@Component(modules = {MainModule.class})
public interface MainComponent {
    Context context();

    void inject(BookSearchActivity bookSearchActivity);
}
