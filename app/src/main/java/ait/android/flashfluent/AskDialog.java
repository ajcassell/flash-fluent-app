package ait.android.flashfluent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import ait.android.flashfluent.data.AppDatabaseLanguages;
import ait.android.flashfluent.data.Language;

public class AskDialog extends DialogFragment {

    public interface LanguageHandler {
        void onNewLanguageCreated(final String languageName);
    }

    private LanguageHandler languageHandler;
    private String country;
    private Context context;
    private TextView tvQuestion;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof LanguageHandler) {
            languageHandler = (LanguageHandler) context;
        } else {
            throw new RuntimeException(getString(R.string.languagehandler_interface_not_implemented));
        }
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Continue?");

        country = getArguments().getString("Country");
        final String languageToAdd = MapActivity.countryLanguage.get(country);

        final View view = getActivity().getLayoutInflater().inflate(R.layout.ask_dialog, null, false);

        tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);

        String message = "Do you want to learn this language of " + country + ": " + languageToAdd + "?";
        tvQuestion.setText(message);

        builder.setView(view);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) { // adding the new language
                new Thread() {
                    @Override
                    public void run() {
                        AppDatabaseLanguages.getAppDatabaseLanguages(getActivity()).languageDao().insertLanguage(new Language(languageToAdd, false));
                        dismiss();
                    }
                }.start();
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }


}
