package internship.irina.com.recv;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Irina on 6/29/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {
    //CardView cv;
    @BindView(R.id.cv) CardView cv;
    //TextView text_card;
    @BindView(R.id.txtStoreName) TextView txtStoreName;
    @BindView(R.id.txtStoreAddr) TextView txtStoreAddr;


    ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        //cv = (CardView)itemView.findViewById(R.id.cv);
        //text_card = (TextView)itemView.findViewById(text_card);
    }

    public void bind(final Item item, final RVAdapter.OnItemClickListener listener) {
        txtStoreName.setText(item.name);
        txtStoreAddr.setText(item.address);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                listener.onItemClick(item);
            }
        });
    }
}