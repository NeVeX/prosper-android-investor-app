package com.mark.prosper.invest.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mark.prosper.invest.R;
import com.mark.prosper.invest.api.model.ListingVO;
import com.mark.prosper.invest.api.model.SearchListingVO;
import com.mark.prosper.invest.constant.ApplicationConstants;
import com.mark.prosper.invest.util.UIUtil;

import java.util.List;

/**
 * Created by NeVeX on 9/26/2015.
 */
public class ListingResultEntryAdapter extends ArrayAdapter<ListingVO> {
    private final Context context;
    private static final String TAG = ApplicationConstants.LOG_TAG;
//    private final List<ListingVO> listings;

    public ListingResultEntryAdapter(Context context, List<ListingVO> listings) {
        super(context, -1, listings);
        this.context = context;
//        this.listings = listings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        if ( convertView != null )
        {
            rowView = convertView;
        }
        else {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.listing_entry, parent, false);
        }

        ListingVO listing = super.getItem(position);
        Log.e(TAG, "Adding listing to view: "+listing.getListingNumber() + " for item "+position);
        TextView prosperRatingText = (TextView) rowView.findViewById(R.id.listing_entry_prosper_rating);
        prosperRatingText.setText(listing.getProsperRating());

        Integer colorId = UIUtil.getResourceColorIdBasedOnProsperRating(listing.getProsperRating());
        if ( colorId != null)
        {
            rowView.setBackgroundColor(rowView.getResources().getColor(colorId));
        }

        TextView listingTitleText = (TextView) rowView.findViewById(R.id.listing_entry_listing_title);
        listingTitleText.setText(listing.getListingTitle());

        TextView estimatedReturnText = (TextView) rowView.findViewById(R.id.listing_entry_estimated_return);
        estimatedReturnText.setText(UIUtil.getPercentageString(listing.getEstimatedReturn()));

        TextView loanAmountText = (TextView) rowView.findViewById(R.id.listing_entry_loan_amount);
        loanAmountText.setText(UIUtil.getMoneyString(listing.getListingAmount(), false) + " Loan");

        TextView percentFunded = (TextView) rowView.findViewById(R.id.listing_entry_percent_funded);
        percentFunded.setText(UIUtil.getPercentageString(listing.getPercentFunded(), false) + " Funded");

//            TextView loanTerm = (TextView) rowView.findViewById(R.id.listing_entry_loan_term_years);
//            loanTerm.setText(listing.getLoanTerm() + " Years");

            return rowView;
//        }
    }
}