package com.example.googlebooks.model.data.local.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.googlebooks.model.Book;

import java.util.List;

@Dao
public interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveBooks(List<Book> books);

    @Query("SELECT * FROM Book WHERE UPPER(`query`) = UPPER(:query)")
    LiveData<List<Book>> getBooks(String query);

    @Query("DELETE FROM Book WHERE UPPER(`query`) = UPPER(:query)")
    void removeBooks(String query);
}
