package ait.android.flashfluent.touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class VocabListItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private VocabListTouchHelperAdapter vocabListTouchHelperAdapter;

    public VocabListItemTouchHelperCallback(VocabListTouchHelperAdapter vocabListTouchHelperAdapter) {
        this.vocabListTouchHelperAdapter = vocabListTouchHelperAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        vocabListTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        vocabListTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());

    }
}
