package hu.bme.aut.shoppinglisthomework.adapter;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.shoppinglisthomework.R;
import hu.bme.aut.shoppinglisthomework.activities.DetailActivity;
import hu.bme.aut.shoppinglisthomework.activities.ListActivity;
import hu.bme.aut.shoppinglisthomework.data.ShoppingItem;
import hu.bme.aut.shoppinglisthomework.data.StorageList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ItemViewHolder> {

    private ArrayList<ShoppingItem> items;

    public DetailAdapter() {
        items = StorageList.getInstance().storageList.get(DetailActivity.listPos).getItems();
    }

    @NonNull
    @Override
    public DetailAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {
        final ShoppingItem item = items.get(position);
        holder.nameTextView.setText(item.getItemName());
        holder.boughtCheckBox.setOnCheckedChangeListener(null);
        if (item.getItemBought())
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.boughtCheckBox.setChecked(item.getItemBought());
        holder.boughtCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (final ShoppingItem shoppingItem : items) {
                    if (shoppingItem.getItemName() == item.getItemName()) {
                        shoppingItem.setBoughStatus(isChecked);
                        if (item.getItemBought())
                            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        else
                            holder.nameTextView.setPaintFlags(holder.nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                ListActivity.database.shoppingItemDao().update(shoppingItem);
                                return true;
                            }
                        }.execute();
                    }
                }
                StorageList.getInstance().storageList.get(DetailActivity.listPos).setItems(items);
            }
        });
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        StorageList.getInstance().storageList.get(DetailActivity.listPos).setItems(items);
        notifyItemRemoved(position);
        if (position < items.size()) {
            notifyItemRangeChanged(position, items.size() - position);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        CheckBox boughtCheckBox;
        TextView nameTextView;
        ImageButton removeButton;

        ShoppingItem item;

        ItemViewHolder(View itemView) {
            super(itemView);

            boughtCheckBox = itemView.findViewById(R.id.ShoppingItemBoughtCheckBox);
            nameTextView = itemView.findViewById(R.id.ShoppingItemNameTextView);
            removeButton = itemView.findViewById(R.id.ShoppingItemRemoveButton);

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeItem(items.indexOf(item));
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            ListActivity.database.shoppingItemDao().deleteItem(item);
                            return true;
                        }
                    }.execute();
                }
            });
        }
    }

    public void update(List<ShoppingItem> shoppingItems) {
        items.clear();
        items.addAll(shoppingItems);
        notifyDataSetChanged();
    }
}
