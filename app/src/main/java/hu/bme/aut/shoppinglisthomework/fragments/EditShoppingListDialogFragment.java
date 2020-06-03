package hu.bme.aut.shoppinglisthomework.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import hu.bme.aut.shoppinglisthomework.R;
import hu.bme.aut.shoppinglisthomework.data.ShoppingList;
import hu.bme.aut.shoppinglisthomework.data.StorageList;

public class EditShoppingListDialogFragment extends DialogFragment {

    public static final String TAG = "EditShoppingListDialogFragment";

    public static String LIST_POS = "list_pos";

    private int listPos;

    private EditText etName;
    private EditText etDetails;

    private ShoppingList oldList;
    private String name;
    private String details;

    public interface EditShoppingListDialogListener {
        void onShoppingListCreated(ShoppingList newList);
        void onShoppingListEdited(ShoppingList oldList,@Nullable ShoppingList editedList);
    }

    private EditShoppingListDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = getActivity();
        if (activity instanceof EditShoppingListDialogListener) {
            listener = (EditShoppingListDialogListener) activity;
        } else {
            throw new RuntimeException("Activity must implement the EditShoppingListDialogListener interface!");
        }
        listPos = getActivity().getIntent().getIntExtra(LIST_POS, -1);
        if (listPos != -1) {
            name = StorageList.getInstance().storageList.get(listPos).getName();
            details = StorageList.getInstance().storageList.get(listPos).getItemsForEdit();
            oldList = new ShoppingList(StorageList.getInstance().storageList.get(listPos));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.edit_shopping_list)
                .setView(getContentView())
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (isValid()) {
                            if (listPos == -1) {
                                listener.onShoppingListCreated(getShoppingList());
                                dialog.dismiss();
                            } else {
                                StorageList.getInstance().swapList(listPos, getShoppingList());
                                listener.onShoppingListEdited(oldList, getShoppingList());
                                dialog.dismiss();
                            }
                        } else {
                            etName.requestFocus();
                            etName.setError(getText(R.string.dialog_error));
                        }
                    }
                });
            }
        });
        return dialog;
    }

    private View getContentView() {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_shopping_list, null);
        etName = contentView.findViewById(R.id.editName);
        etDetails = contentView.findViewById(R.id.editDetail);
        etName.setText(name);
        etName.requestFocus();
        etDetails.setText(details);
        return contentView;
    }

    private boolean isValid() {
        return etName.getText().length() > 0;
    }

    private ShoppingList getShoppingList() {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(etName.getText().toString());
        shoppingList.FillItems(etDetails.getText().toString());
        return shoppingList;
    }
}