package ait.android.flashfluent.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import ait.android.flashfluent.CategoriesActivity;
import ait.android.flashfluent.R;
import ait.android.flashfluent.data.AppDatabaseLanguages;
import ait.android.flashfluent.data.Language;
import ait.android.flashfluent.touch.LanguageListTouchHelperAdapter;

public class LanguageListRecyclerAdapter extends RecyclerView.Adapter<LanguageListRecyclerAdapter.ViewHolder> implements LanguageListTouchHelperAdapter {

        private List<Language> languageList;
        private Context context;

        public LanguageListRecyclerAdapter(List<Language> languageList, Context context) {
            this.languageList = languageList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_row, parent, false);
            return new ViewHolder(viewRow);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            holder.tvLanguageName.setText(languageList.get(holder.getAdapterPosition()).getLanguageName());
        }

        @Override
        public int getItemCount() {
            return languageList.size();
        }

        public void addItem(Language lang) {
            languageList.add(lang);
            notifyDataSetChanged();
        }

        @Override
        public void onItemDismiss(final int position) {
            final Language languageToDelete = languageList.get(position);

            languageList.remove(languageToDelete);
            notifyItemRemoved(position);

            new Thread() {
                @Override
                public void run() {
                    AppDatabaseLanguages.getAppDatabaseLanguages(context).languageDao().delete(languageToDelete);
                }
            }.start();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(languageList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(languageList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        public void updateItem(Language lang) {
            int editPos = findItemIndexByItemId(lang.getLanguageId());
            languageList.set(editPos, lang);
            notifyItemChanged(editPos);
        }

        private int findItemIndexByItemId(long itemId) {
            for (int i = 0; i < languageList.size(); i++) {
                if (languageList.get(i).getLanguageId() == itemId) {
                    return i;
                }
            }
            return -1;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvLanguageName;

            public ViewHolder(View itemView) {
                super(itemView);
                tvLanguageName = itemView.findViewById(R.id.tvLanguageName);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent languageIntent = new Intent(v.getContext(), CategoriesActivity.class);
                        languageIntent.putExtra("Language", tvLanguageName.getText().toString());
                        languageIntent.putExtra("isNew", false);
                        v.getContext().startActivity(languageIntent);
                    }
                });
            }
        }
    }
