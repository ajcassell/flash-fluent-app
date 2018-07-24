package ait.android.flashfluent.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ait.android.flashfluent.R;
import ait.android.flashfluent.FlashcardActivity;
import ait.android.flashfluent.data.AppDatabase;
import ait.android.flashfluent.data.VocabItem;
import ait.android.flashfluent.touch.VocabListTouchHelperAdapter;

public class VocabListRecyclerAdapter extends RecyclerView.Adapter<VocabListRecyclerAdapter.ViewHolder> implements VocabListTouchHelperAdapter {

    private static final long DISPLAY_LENGTH = 2000;
    private List<VocabItem> vocabItemList;
    private static Context context;

    public VocabListRecyclerAdapter(List<VocabItem> list, Context context) {
        vocabItemList = list;
        this.context = context;
    }

    public void deleteAllCustom(ArrayList<VocabItem> listToDelete) {

        for (VocabItem item: listToDelete) {
            for (int pos = 0; pos <vocabItemList.size(); pos++) {
                if (item.getVocabItemName().equals(vocabItemList.get(pos).getVocabItemName())) {
                    VocabItem deletedItem = vocabItemList.remove(pos);
                    notifyDataSetChanged();
                    pos--;
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_front, parent, false);
        return new ViewHolder(viewRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvPhrase.setText(vocabItemList.get(holder.getAdapterPosition()).getVocabItemName());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vocabItemList.get(holder.getAdapterPosition()).isDefault()) {
                    ((FlashcardActivity) context).editItem(vocabItemList.get(holder.getAdapterPosition()));
                } else {
                    ((FlashcardActivity)getContext()).showMessage("Cannot edit a default flashcard");
                }
            }
        });
        holder.btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FlashcardActivity)getContext()).speak(holder.tvPhrase.getText().toString()); // will speak whichever word/translation is shown
            }
        });

        // click on the phrase to get translated one for a few seconds
        holder.tvPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvPhrase.setText(vocabItemList.get(holder.getAdapterPosition()).getTranslatedName());
                // this then changes it back to original word after so many seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.tvPhrase.setText(vocabItemList.get(holder.getAdapterPosition()).getVocabItemName());
                    }
                }, DISPLAY_LENGTH);
            }
        });

    }

    @Override
    public int getItemCount() {
        return vocabItemList.size();
    }

    public void addItem(VocabItem item) {
        vocabItemList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void onItemDismiss(final int position) {
        final VocabItem itemToDelete = vocabItemList.get(position);

        if (!itemToDelete.isDefault()) {
            vocabItemList.remove(itemToDelete);
            notifyItemRemoved(position);

            new Thread() {
                @Override
                public void run() {
                    AppDatabase.getAppDatabase(context).vocabItemDao().delete(itemToDelete);
                }
            }.start();
        } else {
            ((FlashcardActivity)getContext()).showMessage("Cannot delete a default flashcard");
        }
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(vocabItemList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(vocabItemList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void updateItem(VocabItem item) {
        int editPos = findItemIndexByItemId(item.getVocabItemId());
        vocabItemList.set(editPos, item);
        notifyItemChanged(editPos);
    }

    private int findItemIndexByItemId(long itemId) {
        for (int i = 0; i < vocabItemList.size(); i++) {
            if (vocabItemList.get(i).getVocabItemId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvPhrase;
        private final Button btnEdit;
        private final Button btnSpeak;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvPhrase = itemView.findViewById(R.id.tvPhrase);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnSpeak = itemView.findViewById(R.id.btnSpeak);
        }
    }
}
