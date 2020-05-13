package com.example.bigproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {
    private List<Zametka> itemList;
    private Context context;

    public SolventRecyclerViewAdapter(Context context, List<Zametka> itemList,RecyclerView recyclerView) {
        this.itemList = itemList;
        this.context = context;
        if(itemList.size() == 0)
            recyclerView.setBackgroundResource(R.drawable.clean);
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.zametka_list, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(SolventViewHolders holder, int position) {

        if(LocalBase.chheckBitmap(itemList.get(position).getData()))
        {
            holder.countryPhoto.setImageResource(R.drawable.loading);
            holder.countryName.setVisibility(View.GONE);
        }
        else
        {
            holder.countryPhoto.setVisibility(View.GONE);
            holder.countryName.setText(itemList.get(position).getText());
        }

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
