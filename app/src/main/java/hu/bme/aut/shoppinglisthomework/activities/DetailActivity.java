package hu.bme.aut.shoppinglisthomework.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hu.bme.aut.shoppinglisthomework.R;
import hu.bme.aut.shoppinglisthomework.adapter.DetailAdapter;
import hu.bme.aut.shoppinglisthomework.data.ShoppingItem;
import hu.bme.aut.shoppinglisthomework.data.ShoppingList;
import hu.bme.aut.shoppinglisthomework.data.StorageList;
import hu.bme.aut.shoppinglisthomework.fragments.EditShoppingListDialogFragment;

public class DetailActivity extends AppCompatActivity implements EditShoppingListDialogFragment.EditShoppingListDialogListener {

    private RecyclerView recyclerView;
    private DetailAdapter adapter;

    public static int listPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listPos = getIntent().getIntExtra(EditShoppingListDialogFragment.LIST_POS, -1);

        initFab();
        initRecyclerView();
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle arguments = new Bundle();
                arguments.putInt(EditShoppingListDialogFragment.LIST_POS, listPos);
                EditShoppingListDialogFragment fragment = new EditShoppingListDialogFragment();
                fragment.setArguments(arguments);
                fragment.show(getSupportFragmentManager(), EditShoppingListDialogFragment.TAG);
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.MainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.detail_untick_all:
                for (ShoppingItem shoppingItem : StorageList.getInstance().storageList.get(listPos).getItems()) {
                    shoppingItem.setBoughStatus(false);
                }
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        for (ShoppingItem shoppingItem : StorageList.getInstance().storageList.get(listPos).getItems()) {
                            ListActivity.database.shoppingItemDao().update(shoppingItem);
                        }
                        return true;
                    }
                }.execute();
                adapter.notifyDataSetChanged();
                break;
            case R.id.detail_send_list:
                StringBuilder builder = new StringBuilder();
                builder.append(StorageList.getInstance().storageList.get(listPos).getName() + ":\n");
                boolean first = true;
                for (ShoppingItem shoppingItem : StorageList.getInstance().storageList.get(listPos).getItems()) {
                    if (!shoppingItem.getItemBought()) {
                        if (!first)
                            builder.append("\n");
                        builder.append(shoppingItem.getItemName());
                        first = false;
                    }
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onShoppingListCreated(final ShoppingList newList) {
        adapter.update(newList.getItems());
        ListActivity.adapter.reload();
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                for (ShoppingItem item : newList.getItems()) {
                    item.setList(newList.getName());
                    item.id = ListActivity.database.shoppingItemDao().insert(item);
                }
                return true;
            }
        }.execute();
    }

    @Override
    public void onShoppingListEdited(final ShoppingList oldList, @Nullable final ShoppingList editedList) {
        new AsyncTask<Void, Void, ShoppingList>() {

            @Override
            protected ShoppingList doInBackground(Void... voids) {
                for (ShoppingItem item : oldList.getItems()) {
                    ListActivity.database.shoppingItemDao().delete(item.id);
                }
                if (editedList != null) {
                    for (ShoppingItem item : editedList.getItems()) {
                        item.setList(editedList.getName());
                        item.id = ListActivity.database.shoppingItemDao().insert(item);
                    }
                }
                return editedList;
            }
        }.execute();
        adapter.update(StorageList.getInstance().storageList.get(listPos).getItems());
    }

    @Override
    public void onBackPressed() {
        ListActivity.adapter.reload();
        super.onBackPressed();
    }
}
