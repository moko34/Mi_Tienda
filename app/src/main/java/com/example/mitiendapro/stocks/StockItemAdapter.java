package com.example.mitiendapro.stocks;

import android.widget.TextView;

public class StockItemAdapter {
    public interface StockItemIsClicked{
        //increase stock item quantity
        void increaseStock(StockItem stockItem, TextView textView);
        // reduce stock item quantity
        void reduceStock(StockItem stockItem);
        //change stock item imageUri
        void setStockImage(StockItem stockItem);
        //change quantity to preferred value
        void editStockQuantity(StockItem stockItem);
    }
}
