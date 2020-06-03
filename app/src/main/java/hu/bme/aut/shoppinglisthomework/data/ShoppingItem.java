package hu.bme.aut.shoppinglisthomework.data;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "shoppingitem")
public class ShoppingItem {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @ColumnInfo(name = "name")
    private String itemName;

    @ColumnInfo(name = "is_bought")
    private boolean itemBought;

    @ColumnInfo(name = "listName")
    private String list;

    @Ignore
    ShoppingItem(String itemName) {
        this.itemName = itemName;
        this.itemBought = false;
    }

    @Ignore
    ShoppingItem(String itemName, boolean itemBought) {
        this.itemName = itemName;
        this.itemBought = itemBought;
    }

    @Ignore
    ShoppingItem(String itemName, String list) {
        this.itemName = itemName;
        this.list = list;
    }

    ShoppingItem(String itemName, boolean itemBought, String list) {
        this.itemName = itemName;
        this.itemBought = itemBought;
        this.list = list;
    }

    @Ignore
    ShoppingItem(ShoppingItem item) {
        this.id = item.id;
        this.itemName = item.itemName;
        this.itemBought = item.itemBought;
        this.list = item.list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getList() {
        return list;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean getItemBought() {
        return itemBought;
    }

    public void setBoughStatus(boolean itemBought) {
        this.itemBought = itemBought;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ShoppingItem s = (ShoppingItem) obj;
        return itemName.toUpperCase().equals(s.getItemName().toUpperCase());
    }
}