package ait.android.flashfluent.touch;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class LanguageListTouchHelperCallback extends ItemTouchHelper.Callback {

    private LanguageListTouchHelperAdapter languageListTouchHelperAdapter;

    public LanguageListTouchHelperCallback(LanguageListTouchHelperAdapter languageListTouchHelperAdapter) {
        this.languageListTouchHelperAdapter = languageListTouchHelperAdapter;
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
        languageListTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        languageListTouchHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}