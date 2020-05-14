package com.example.googlebooks.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.data.BookRepository;

import java.util.List;

public class BookSearchViewModel extends ViewModel {
    private LiveData<List<Book>> bookList;
    private BookRepository bookRepository;

    public BookSearchViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    void getBooks(String query) {
        bookList = bookRepository.getBooks(query);
    }

    LiveData<List<Book>> getBookLiveData() {
        return bookList;
    }

    void saveBooks(List<Book> books, String query) {
        bookRepository.saveBooks(books, query);
    }

    public static class BookSearchViewModelFactory implements ViewModelProvider.Factory {

        private final BookRepository bookRepository;

        BookSearchViewModelFactory(BookRepository bookRepository) {
            this.bookRepository = bookRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(BookRepository.class).newInstance(bookRepository);
            } catch (Exception e) {
                e.printStackTrace();
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
