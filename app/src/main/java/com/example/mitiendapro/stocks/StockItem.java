package com.example.mitiendapro.stocks;

import android.net.Uri;

public class StockItem {
    private Uri uri;
    private long id;
    private String narration;
    private int quantity;
    private long stockItemIdKey;
    private String stockItemFileName;

    public StockItem(Uri uri, String narration, int quantity,long stockItemIdKey,String stockItemFileName) {
        this.uri = uri;
        this.narration = narration;
        this.quantity = quantity;
        this.id=id;
        this.stockItemIdKey=stockItemIdKey;
        this.stockItemFileName=stockItemFileName;
    }

    public Uri getUri() {
        return uri;
    }

    public String getNarration() {
        return narration;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getId() {
        return id;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStockItemIdKey() {
        return stockItemIdKey;
    }

    public void setStockItemIdKey(long stockItemIdKey) {
        this.stockItemIdKey = stockItemIdKey;
    }

    public String getStockItemFileName() {
        return stockItemFileName;
    }

    public void setStockItemFileName(String stockItemFileName) {
        this.stockItemFileName = stockItemFileName;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}


