package hu.bme.aut.shoppinglisthomework.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.shoppinglisthomework.R;
import hu.bme.aut.shoppinglisthomework.adapter.ListAdapter;
import hu.bme.aut.shoppinglisthomework.data.ShoppingItem;
import hu.bme.aut.shoppinglisthomework.data.ShoppingList;
import hu.bme.aut.shoppinglisthomework.data.ShoppingListDatabase;
import hu.bme.aut.shoppinglisthomework.data.StorageList;
import hu.bme.aut.shoppinglisthomework.fragments.EditShoppingListDialogFragment;

public class ListActivity extends AppCompatActivity
        implements ListAdapter.OnListSelectedListener,
        EditShoppingListDialogFragment.EditShoppingListDialogListener {

    private RecyclerView recyclerView;
    public static ListAdapter adapter;

    public static ShoppingListDatabase database;
    private static String DATABASE_NAME = "shopping-list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        database = Room.databaseBuilder(
                getApplicationContext(),
                ShoppingListDatabase.class,
                DATABASE_NAME
        ).build();

        initFab();
        initRecyclerView();
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EditShoppingListDialogFragment().show(getSupportFragmentManager(), EditShoppingListDialogFragment.TAG);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.MainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter(this, this, this);
        loadItemsInBackground();

        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_change_night_mode) {
            if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<ShoppingItem>>() {

            @Override
            protected List<ShoppingItem> doInBackground(Void... voids) {
                return database.shoppingItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<ShoppingItem> shoppingItems) {
                ArrayList<ShoppingList> lists = new ArrayList<>();
                boolean inserted;
                for (ShoppingItem item : shoppingItems) {
                    inserted = false;
                    int idx = -1;
                    for (int i = 0; i < lists.size() && !inserted; i++) {
                        if (lists.get(i).getName().equals(item.getList())) {
                            inserted = true;
                            idx = i;
                        }
                    }
                    if (!inserted) {
                        idx = lists.size();
                        lists.add(new ShoppingList(item.getList()));
                        adapter.addList(lists.get(idx).getName());
                    }
                    lists.get(idx).addShoppingItem(item);
                }
                StorageList.getInstance().storageList = lists;
            }
        }.execute();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListSelected(String listName) {
        Context context = ListActivity.this;
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EditShoppingListDialogFragment.LIST_POS, StorageList.getInstance().getIndex(listName));
        context.startActivity(intent);
    }

    @Override
    public void onShoppingListCreated(final ShoppingList newList) {
        adapter.addList(newList.getName());
        StorageList.getInstance().addShoppingList(newList);
        adapter.notifyDataSetChanged();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                for (ShoppingItem item : newList.getItems()) {
                    item.setList(newList.getName());
                    item.id = database.shoppingItemDao().insert(item);
                }
                return true;
            }
        }.execute();

    }

    @Override
    public void onShoppingListEdited(final ShoppingList oldList, @Nullable ShoppingList editedList) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                for (ShoppingItem item : oldList.getItems()) {
                    database.shoppingItemDao().deleteItem(item);
                }
                return true;
            }
        }.execute();
    }
}
