package com.example.mitiendapro.stocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mitiendapro.R;
import com.example.mitiendapro.controller.StockManager;

import java.io.IOException;
import java.util.ArrayList;

public class NewStockAdapter extends RecyclerView.Adapter<StockItemViewHolder> {



    private ArrayList<StockItem> stockItems;
    private Context context;
    private  StockItemAdapter.StockItemIsClicked stockItemIsClicked;
    private  StockManager stockManager;

    public NewStockAdapter(ArrayList<StockItem> stockItems, Context context, StockItemAdapter.StockItemIsClicked stockItemIsClicked) {
        this.stockItems = stockItems;
        this.context = context;
        stockManager=new StockManager(context);
        this.stockItemIsClicked = stockItemIsClicked;};

    @NonNull
    @Override
    public StockItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_item,parent,false);
        return  new StockItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemViewHolder holder, int position) {
        holder.getStock_quantity().setText(String.valueOf(stockItems.get(position).getQuantity()));
        holder.getNarration().setText(stockItems.get(position).getNarration());
        try {
            holder.getPhoto().setImageBitmap(drawRoundShape(stockManager.decodeBitmap(stockItems.get(position).getUri())));
        } catch (IOException exception) {
            exception.printStackTrace();
            showToast(context.getString(R.string.file_not_found));
        }
        holder.getAdd().setOnClickListener((view)->{stockItemIsClicked.increaseStock(stockItems.get(position), holder.getStock_quantity());});
        holder.getMinus().setOnClickListener((view)->{stockItemIsClicked.reduceStock(stockItems.get(position));});
        holder.getEdit().setOnClickListener((view)->stockItemIsClicked.editStockQuantity(stockItems.get(position)));
        holder.getPhoto().setOnClickListener((view -> stockItemIsClicked.setStockImage(stockItems.get(position)) ));
    }

    @Override
    public int getItemCount() {
        return  stockItems.size();
    }
    //delete stock item
    public void deleteStockItem(StockItem stockItem){
        int index = stockItems.indexOf(stockItem);
        stockItems.remove(index);

        notifyItemRemoved(index);
    }
    //Modify quantity and image content
    public void modifyStockItem(StockItem oldStockItem,StockItem newStockItem,boolean isEditingImage) {
        int index = stockItems.indexOf(oldStockItem);

        if (isEditingImage) {
            //update the stock item in the adapter
            oldStockItem.setUri(newStockItem.getUri());
            oldStockItem.setStockItemFileName(newStockItem.getStockItemFileName());
            oldStockItem.setStockItemIdKey(newStockItem.getStockItemIdKey());
        } else {
            //update the stock item in the adapter
            oldStockItem.setQuantity(newStockItem.getQuantity());
            oldStockItem.setStockItemIdKey(newStockItem.getStockItemIdKey());
            oldStockItem.setStockItemFileName(newStockItem.getStockItemFileName());
        }
        notifyItemChanged(index);
    }
    public void addStockItem(StockItem stockItem){
        stockItems.add(stockItem);
        notifyItemInserted(stockItems.size()-1);
    }
    public Bitmap drawRoundShape(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getScaledWidth(c) / 2, bitmap.getScaledHeight(c) / 2, bitmap.getScaledWidth(c) / 2, paint);
        return circleBitmap;
    }
    public void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}


