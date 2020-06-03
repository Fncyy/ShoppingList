package hu.bme.aut.shoppinglisthomework.data;

import android.content.res.Resources;

import java.util.ArrayList;

public class StorageList {
    private static StorageList instance;

    public ArrayList<ShoppingList> storageList;

    private StorageList() {
        storageList = new ArrayList<>();
        //fillTestData();
    }

    public static StorageList getInstance() {
        if (instance == null)
            instance = new StorageList();

        return instance;
    }

    public void changeShoppingList(ShoppingList shoppingList, int position) {
        storageList.remove(position);
        storageList.add(shoppingList);
    }

    public void addShoppingList(ShoppingList shoppingList) {
        storageList.add(shoppingList);
    }

    private void fillTestData() {
        storageList.add(new ShoppingList("First List"));
        storageList.add(new ShoppingList("Second List"));
        storageList.add(new ShoppingList("Third List"));

        int item_num = 3;
        for (int i = 0; i < 3; i++) {
            int num = 0;
            while (num < item_num) {
                storageList.get(i).addShoppingItem(new ShoppingItem((i + 1) + ". list " + (num + 1) + ".item",
                        ((i + 1) * item_num * (num + 1)) % 2 == 0));
                num++;
            }
            item_num++;
        }
    }

    public int getIndex(String name) {
        for (int i = 0; i < storageList.size(); i++) {
            if (storageList.get(i).getName() == name) return i;
        }
        throw new Resources.NotFoundException();
    }

    public void swapList(int position, ShoppingList newShoppingList) {
        ArrayList<ShoppingItem> originalItems = StorageList.getInstance().storageList.get(position).getItems();
        for (int i = 0; i < newShoppingList.getItems().size(); i++) {
            for (ShoppingItem item : originalItems) {
                if (newShoppingList.getItems().get(i).getItemName().toUpperCase()
                        .equals(item.getItemName().toUpperCase()
                )) {
                    newShoppingList.getItems().get(i).setBoughStatus(item.getItemBought());
                }
            }
        }
        StorageList.getInstance().storageList.remove(position);
        StorageList.getInstance().storageList.add(position, newShoppingList);
    }
}
