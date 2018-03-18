package ca.ualberta.angrybidding.ui.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ca.ualberta.angrybidding.Bid;
import ca.ualberta.angrybidding.R;
import ca.ualberta.angrybidding.User;


public class BidAdapter extends ArrayAdapter<Bid> {
    public BidAdapter(Activity context, ArrayList<Bid> bids) {
        super(context, 0, bids);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.view_bid, parent, false);
        }
        Bid bid = getItem(position);


        TextView userTextView = (TextView) listItemView.findViewById(R.id.bidUser);
        userTextView.setText(bid.getUser().getUsername());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.bidPrice);
        dateTextView.setText(String.valueOf(bid.getPrice()));

        return listItemView;
    }
}
