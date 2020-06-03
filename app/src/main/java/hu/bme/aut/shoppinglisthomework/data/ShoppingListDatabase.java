package hu.bme.aut.shoppinglisthomework.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(
        entities = {ShoppingItem.class},
        version = 1,
        exportSchema = false
)
public abstract class ShoppingListDatabase extends RoomDatabase {
    public abstract ShoppingItemDao shoppingItemDao();
}