package com.example.aksha.newb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    ArrayList<String> titleList;
    ArrayList<String> costList;
    ArrayList<String> bitmapList;
    ArrayList<String> descriptionList;
    ArrayList<String> uidList;
    ArrayList<String> latiList;
    ArrayList<String> longList;

    class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewName;
        TextView textViewCost;

        public SearchViewHolder(final View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewListItemName);
            textViewCost = itemView.findViewById(R.id.textViewListItemCost);
            imageView = itemView.findViewById(R.id.imageViewListItem);

        }
    }

    public SearchAdapter(Context context, ArrayList<String> titleList, ArrayList<String> costList, ArrayList<String> bitmapList, ArrayList<String> descriptionList, ArrayList<String> uidList, ArrayList<String> latiList, ArrayList<String> longList) {
        this.context = context;
        this.titleList = titleList;
        this.costList = costList;
        this.bitmapList = bitmapList;
        this.descriptionList = descriptionList;
        this.uidList = uidList;
        this.latiList = latiList;
        this.longList = longList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {

        holder.textViewName.setText(titleList.get(position));
        holder.textViewCost.setText(costList.get(position));

        Picasso.with(context).load(bitmapList.get(position)).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MakerClickedLayout.class);
                intent.putExtra("Title", titleList.get(position));
                intent.putExtra("Cost", costList.get(position));
                intent.putExtra("Description", descriptionList.get(position));
                intent.putExtra("Bitmap", bitmapList.get(position));
                intent.putExtra("Latitude", latiList.get(position));
                intent.putExtra("Longitude", longList.get(position));
                intent.putExtra("Id", uidList.get(position));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}
