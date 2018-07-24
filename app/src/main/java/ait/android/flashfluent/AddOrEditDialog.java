package ait.android.flashfluent;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ait.android.flashfluent.data.VocabItem;

public class AddOrEditDialog extends DialogFragment {

    public interface ItemHandler {
        void onNewItemCreated(final String name, final String translatedName, final boolean isDefault, final String parentLanguage, final String parentCategory);
        void onItemUpdated(VocabItem item);
    }

    private ItemHandler itemHandler;
    private String language;
    private String category;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ItemHandler) {
            itemHandler = (ItemHandler) context;
        } else {
            throw new RuntimeException(getString(R.string.itemhandler_interface_not_implemented));
        }
    }

    public Context getContext() {
        return context;
    }

    private EditText etName;
    private EditText etTranslatedName;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String choice;
        switch(FlashcardActivity.buttonPressed) {
            case "Add":
                choice = "Add";
                break;
            case "Edit":
                choice = "Edit";
                break;
            default:
                choice = "";
                break;
        }

        category = getArguments().getString(FlashcardActivity.KEY_CAT);
        language = getArguments().getString(FlashcardActivity.KEY_LANG);

        builder.setTitle(choice);

        View newItem = getActivity().getLayoutInflater().inflate(R.layout.add_or_edit_dialog, null, false);
        etName = newItem.findViewById(R.id.etName);
        etTranslatedName = newItem.findViewById(R.id.etTranslatedName);

        int numArgs = getArguments().size();
        if (numArgs > 2) { // means editing: KEY_EDIT, KEY_CAT, and KEY_LANG
            VocabItem itemToEdit = (VocabItem) getArguments().getSerializable(FlashcardActivity.KEY_ITEM_TO_EDIT);
            etName.setText(itemToEdit.getVocabItemName());
            etTranslatedName.setText(itemToEdit.getTranslatedName());
        }

        builder.setView(newItem);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        final AlertDialog alertDialog = (AlertDialog)getDialog();

        if (alertDialog != null) {
            Button positiveButton = (Button)alertDialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // in edit or new mode
                    String newName = "";
                    String newTranslatedName = "";

                    if (!TextUtils.isEmpty(etName.getText())) {
                        newName = etName.getText().toString();

                        if (!TextUtils.isEmpty(etTranslatedName.getText())) {
                            newTranslatedName = etTranslatedName.getText().toString();

                            if (!TextUtils.isEmpty(etName.getText())) {

                                if (getArguments() != null && getArguments().containsKey(FlashcardActivity.KEY_ITEM_TO_EDIT)) {

                                    VocabItem itemToEdit = (VocabItem) getArguments().getSerializable(FlashcardActivity.KEY_ITEM_TO_EDIT);
                                    itemToEdit.setVocabItemName(newName);
                                    itemToEdit.setTranslatedName(newTranslatedName);
                                    itemToEdit.setDefault(false);
                                    itemToEdit.setParentCategory(category);
                                    itemToEdit.setParentLanguage(language);
                                    itemHandler.onItemUpdated(itemToEdit);
                                } else {
                                    itemHandler.onNewItemCreated(newName, newTranslatedName, false, language, category);
                                }
                                alertDialog.dismiss();
                            } else {
                                etName.setError("This field cannot be empty"); // had an error about not attached to a context when I tried to extract these strings
                            }
                        } else {
                            etTranslatedName.setError("This field cannot be empty");
                        }
                    } else {
                        etName.setError("This field cannot be empty");
                    }
                }
            });
        }
    }
}
