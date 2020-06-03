package hu.bme.aut.shoppinglisthomework.data;

import java.util.ArrayList;

public class ShoppingList {
    private String listName;
    private ArrayList<ShoppingItem> items;

    public ShoppingList(String listName) {
        this.listName = listName;
        items = new ArrayList<ShoppingItem>();
    }

    public ShoppingList() {
        items = new ArrayList<ShoppingItem>();
    }

    public ShoppingList(ShoppingList list) {
        this.listName = list.listName;
        items = new ArrayList<>();
        for (ShoppingItem item : list.items) {
            items.add(new ShoppingItem(item));
        }
    }

    public ArrayList<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingItem> list) {
        items = list;
    }

    public void addShoppingItem(ShoppingItem shoppingItem) {
        items.add(shoppingItem);
    }

    public String getName() {
        return listName;
    }

    public void setName(String listName) {
        this.listName = listName;
    }

    public String getItemsForEdit() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i != 0)
                sb.append("\n");
            sb.append(items.get(i).getItemName());
        }
        return sb.toString();
    }

    public void FillItems(String raw) {
        String[] array = raw.split("\n");
        ArrayList<ShoppingItem> list = new ArrayList<>();
        for (String s : array) {
            String str = s.trim();
            if (!str.isEmpty()) {
                int idx = items.indexOf(str);
                if (idx != -1)
                    list.add(new ShoppingItem(str, items.get(idx).getItemBought(), this.getName()));
                else
                    list.add(new ShoppingItem(str));
            }
        }
        items.clear();
        items.addAll(list);
    }
}
