package com.example.aksha.newb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    ArrayList<String> titleList;
    ArrayList<String> costList;
    ArrayList<String> bitmapList;

    class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewName;
        TextView textViewCost;

        public SearchViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewListItemName);
            textViewCost = itemView.findViewById(R.id.textViewListItemCost);
            imageView = itemView.findViewById(R.id.imageViewListItem);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> titleList, ArrayList<String> costList, ArrayList<String> bitmapList) {
        this.context = context;
        this.titleList = titleList;
        this.costList = costList;
        this.bitmapList = bitmapList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {

        holder.textViewName.setText(titleList.get(position));
        holder.textViewCost.setText(costList.get(position));

        byte[] encodeByte = Base64.decode(bitmapList.get(position), Base64.DEFAULT);
        Bitmap imageB = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        holder.imageView.setImageBitmap(imageB);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}
