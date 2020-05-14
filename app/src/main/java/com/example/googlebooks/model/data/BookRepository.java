package com.example.googlebooks.model.data;

import androidx.lifecycle.LiveData;

import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.data.local.LocalDataSource;
import com.example.googlebooks.model.data.remote.RemoteDataSource;

import java.util.List;

import javax.inject.Inject;

public class BookRepository {
    private LiveData<List<Book>> bookList;
    private RemoteDataSource remoteDataSource;
    private LocalDataSource localDataSource;
    public static Source dataSource = Source.NONE;

    @Inject
    public BookRepository(RemoteDataSource remoteDataSource, LocalDataSource localDataSource) {
        this.remoteDataSource = remoteDataSource;
        this.localDataSource = localDataSource;
    }

    public LiveData<List<Book>> getBooks(String query) {
        checkCache(query, new CheckCacheCallback() {
            @Override
            public void cacheStatus(boolean isCacheValid) {
                if (isCacheValid) {
                    //load from local source
                    dataSource = Source.LOCAL;
                    bookList = localDataSource.getBooks(query);
                } else {
                    //load from remote source
                    dataSource = Source.REMOTE;
                    bookList = remoteDataSource.getBooks(query);
                }
            }
        });

        return bookList;
    }

    public void saveBooks(List<Book> books, String query) {
        localDataSource.saveBooks(books, query);
    }

    private void checkCache(String query, CheckCacheCallback cacheCallback) {
        localDataSource.isCacheValid(query, cacheCallback);
    }

    public interface CheckCacheCallback {
        void cacheStatus(boolean isCacheStale);
    }

    public enum Source {
        NONE, LOCAL, REMOTE
    }
}
