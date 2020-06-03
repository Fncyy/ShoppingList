package hu.bme.aut.shoppinglisthomework.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ShoppingItemDao {
    @Query("SELECT * FROM shoppingitem")
    List<ShoppingItem> getAll();

    @Query("DELETE FROM shoppingitem WHERE listName = :listName")
    void deleteList(String listName);

    @Query("DELETE FROM shoppingitem WHERE id = :id")
    void delete(long id);

    @Insert
    long insert(ShoppingItem shoppingItems);

    @Update
    void update(ShoppingItem shoppingItem);

    @Delete
    void deleteItem(ShoppingItem shoppingItem);
}