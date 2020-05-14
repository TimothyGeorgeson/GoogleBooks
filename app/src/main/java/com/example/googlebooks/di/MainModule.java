package com.example.googlebooks.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {
    private final Context context;

    public MainModule (Context context) {
        this.context = context;
    }

    @Provides
    public Context context() {
        return context;
    }
}
