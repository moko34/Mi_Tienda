package com.example.mitiendapro.stocks;

public class StockItemId {
    private long stockId;
    private String stockName;
    public StockItemId(long stockId, String stockName) {
        this.stockId = stockId;
        this.stockName = stockName;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
}


