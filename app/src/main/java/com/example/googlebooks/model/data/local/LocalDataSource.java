package com.example.googlebooks.model.data.local;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.data.BookRepository;
import com.example.googlebooks.model.data.local.room.BookDao;
import com.example.googlebooks.model.data.local.room.BookDatabase;
import com.example.googlebooks.utils.CacheManager;

import java.util.List;

import javax.inject.Inject;

public class LocalDataSource {
    private BookDao bookDao;
    private CacheManager cacheManager;

    @Inject
    public LocalDataSource(Context context) {
        BookDatabase bookDatabase = BookDatabase.getDatabase(context);
        bookDao = bookDatabase.bookDao();
        cacheManager = new CacheManager(context);
    }

    public void saveBooks(final List<Book> books, String query) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.saveBooks(books);
            cacheManager.updateCache(query, System.currentTimeMillis());
        });
    }

    public LiveData<List<Book>> getBooks(String query) {
        return bookDao.getBooks(query);
    }

    public void removeBooks(String query) {
        BookDatabase.databaseWriteExecutor.execute(() -> {
            bookDao.removeBooks(query);
        });
    }

    public void isCacheValid(String query, BookRepository.CheckCacheCallback cacheCallback) {
        cacheCallback.cacheStatus(cacheManager.isCacheValid(query));
    }
}
