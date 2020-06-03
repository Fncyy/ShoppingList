package hu.bme.aut.shoppinglisthomework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.shoppinglisthomework.R;
import hu.bme.aut.shoppinglisthomework.data.ShoppingList;
import hu.bme.aut.shoppinglisthomework.data.StorageList;
import hu.bme.aut.shoppinglisthomework.fragments.EditShoppingListDialogFragment;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private final List<String> listNames;
    private Context context;
    private int lastPosition = -1;

    private OnListSelectedListener onListSelectedListener;
    private EditShoppingListDialogFragment.EditShoppingListDialogListener onShoppingListEditedListener;

    public interface OnListSelectedListener {
        void onListSelected(String listName);
    }

    public ListAdapter(OnListSelectedListener onListSelectedListener,
                       EditShoppingListDialogFragment.EditShoppingListDialogListener onShoppingListEditedListener,
                       Context context) {
        this.onListSelectedListener = onListSelectedListener;
        this.onShoppingListEditedListener = onShoppingListEditedListener;
        this.context = context;
        listNames = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        String item = listNames.get(position);
        holder.nameTextView.setText(listNames.get(position));
        holder.item = item;
        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return listNames.size();
    }

    public void addList(String newList) {
        listNames.add(newList);
        notifyItemInserted(listNames.size() - 1);
    }

    public void removeList(int position) {
        listNames.remove(position);
        StorageList.getInstance().storageList.remove(position);
        notifyItemRemoved(position);
        if (position < listNames.size()) {
            notifyItemRangeChanged(position, listNames.size() - position);
        }
    }

    public void reload() {
        listNames.clear();
        for (ShoppingList list : StorageList.getInstance().storageList) {
            listNames.add(list.getName());
        }
        notifyDataSetChanged();
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        ImageButton removeButton;

        String item;

        ListViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.ListNameTextView);
            removeButton = itemView.findViewById(R.id.ListRemoveButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onListSelectedListener != null) {
                        onListSelectedListener.onListSelected(item);
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int idx = StorageList.getInstance().getIndex(item);
                    ShoppingList removed = StorageList.getInstance().storageList.get(idx);
                    removeList(idx);
                    onShoppingListEditedListener.onShoppingListEdited(removed, null);
                }
            });
        }
    }
}
