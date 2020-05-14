package com.example.googlebooks.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlebooks.R;
import com.example.googlebooks.di.DaggerMainComponent;
import com.example.googlebooks.di.MainComponent;
import com.example.googlebooks.di.MainModule;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.data.BookRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

public class BookSearchActivity extends AppCompatActivity {
    private static final String TAG = "BookSearchActivity";

    private EditText etSearch;
    private TextView tvNoResults;
    private RecyclerView rvBookList;
    private BookSearchViewModel viewModel;

    @Inject
    BookRepository bookRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        MainComponent mainComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule(BookSearchActivity.this))
                .build();

        mainComponent.inject(this);

        BookSearchViewModel.BookSearchViewModelFactory factory =
                new BookSearchViewModel.BookSearchViewModelFactory(bookRepository);

        viewModel = new ViewModelProvider(this, factory).get(BookSearchViewModel.class);

        etSearch = findViewById(R.id.etSearch);
        tvNoResults = findViewById(R.id.tvNoResults);
        rvBookList = findViewById(R.id.rvBooks);
        Button btnSearch = findViewById(R.id.btnSearch);

        attachEditTextListener();

        btnSearch.setOnClickListener(v -> {
            startSearch();
        });
    }

    private void startSearch() {
        String query = etSearch.getText().toString().trim();
        if (query.length() == 0) {
            Toast.makeText(BookSearchActivity.this, "Please enter search text.", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard();
        viewModel.getBooks(query);
        viewModel.getBookLiveData().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                Log.d(TAG, "book data source: " + BookRepository.dataSource);

                if (BookRepository.dataSource == BookRepository.Source.NONE || books == null) {
                    return;
                }

                List<Book> noDuplicateList = new ArrayList<>(new HashSet<>(books));

                if (BookRepository.dataSource == BookRepository.Source.REMOTE) {
                    BookRepository.dataSource = BookRepository.Source.NONE;
                    viewModel.saveBooks(noDuplicateList, query);
                }

                if (noDuplicateList.size() > 0) {
                    tvNoResults.setVisibility(View.GONE);
                    rvBookList.setVisibility(View.VISIBLE);

                    rvBookList.setLayoutManager(new GridLayoutManager(BookSearchActivity.this, 2));
                    rvBookList.setAdapter(new BookSearchAdapter(noDuplicateList));

                    for (int i = 0; i < noDuplicateList.size(); i++) {
                        Log.d(TAG, "book: " + noDuplicateList.get(i).getTitle());
                    }
                } else {
                    tvNoResults.setVisibility(View.VISIBLE);
                    rvBookList.setVisibility(View.GONE);
                    Log.d(TAG, "book: No results found");
                }
            }
        });
    }

    private void attachEditTextListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch();
                    handled = true;
                }
                return handled;
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
