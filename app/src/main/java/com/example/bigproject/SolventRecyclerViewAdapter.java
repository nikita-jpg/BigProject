package com.example.bigproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.util.List;

public class SolventRecyclerViewAdapter  extends RecyclerView.Adapter<SolventViewHolders> {
    protected List<Zametka> itemList;
    private Context context;
    private RecyclerView recyclerView;

    public SolventRecyclerViewAdapter(Context context, List<Zametka> itemList,RecyclerView recyclerView) {
        this.itemList = itemList;
        this.context = context;
        this.recyclerView = recyclerView;

        if(itemList.size() == 0)
            recyclerView.setBackgroundResource(R.drawable.clean);
        else
            recyclerView.setBackgroundResource(R.drawable.black_background);
    }

    @Override
    public SolventViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.zametka_list, null);
        SolventViewHolders rcv = new SolventViewHolders(layoutView,context);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final SolventViewHolders holder, int position) {

        holder.zametka=itemList.get(position);
        if(LocalBase.chheckBitmap(itemList.get(position).getData()))
        {
            holder.countryPhoto.setImageResource(R.drawable.loading);
            holder.countryName.setVisibility(View.GONE);
            final int finPosition = position;
            final android.os.Handler handler = new Handler()
            {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    holder.countryPhoto.setImageBitmap((Bitmap) msg.obj);
                }
            };

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = LocalBase.getBitmap(itemList.get(finPosition).getData());
                        Message message = new Message();
                        message.obj = bitmap;
                        handler.sendMessage(message);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        else
        {
            holder.countryPhoto.setVisibility(View.GONE);
            holder.countryName.setText(itemList.get(position).getName());
        }

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public void notifyDataSetChangedMyMethod() {
        if(itemList.size() == 0)
            recyclerView.setBackgroundResource(R.drawable.clean);
        else
            recyclerView.setBackgroundResource(R.drawable.black_background);
    }

    public void setItemList(List<Zametka> itemList)
    {
        this.itemList = itemList;
    }

}
