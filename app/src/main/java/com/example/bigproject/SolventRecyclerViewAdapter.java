package com.example.bigproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.InputStream;
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

        if(!itemList.get(position).getUri().equals(""))//Если в заметке есть Uri, то картинку будем подгружать по нему
        {
            holder.countryName.setVisibility(View.GONE);
            final InputStream imageStream;
            try {
                imageStream = context.getContentResolver().openInputStream(Uri.parse(itemList.get(position).getUri()));
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                holder.countryPhoto.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(LocalBase.chheckBitmap(itemList.get(position).getData()))//Если Uri нет, то проверяем есть ли в базе картинка
        {
            holder.countryName.setVisibility(View.GONE);
            holder.countryPhoto.setImageResource(R.drawable.loading);
            final int finPosition = position;
            final android.os.Handler handler = new Handler()
                {
                    @Override
                    public void handleMessage(@NonNull Message msg) { super.handleMessage(msg);
                       holder.countryPhoto.setImageBitmap((Bitmap) msg.obj); }
                };

            Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bitmap = LocalBase.getBitmap(itemList.get(finPosition).getData());
                            Message message = new Message();
                            message.obj = bitmap;
                            handler.sendMessage(message);
                        } catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            thread.start();

        }
        else//Если картинки нет, то показываем текст
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
