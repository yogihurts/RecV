package internship.irina.com.recv;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static java.util.Collections.addAll;


/**
 * Created by Irina on 6/29/2017.
 */

public class RVAdapter extends RecyclerView.Adapter<ItemViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    List<Item> items;
    private final OnItemClickListener listener;

    public RVAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv, viewGroup, false);
        ItemViewHolder it = new ItemViewHolder(v);
        return it;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.txtStoreName.setText(items.get(i).name);
        itemViewHolder.txtStoreAddr.setText(items.get(i).address);
        itemViewHolder.bind(items.get(i), listener);


    }

    @Override
    public int getItemCount() {
        Log.d("ITEM COUNT", items.size() + "");
        return items.size();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}