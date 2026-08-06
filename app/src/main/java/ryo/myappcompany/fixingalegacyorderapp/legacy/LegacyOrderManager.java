package ryo.myappcompany.fixingalegacyorderapp.legacy;

import java.util.ArrayList;
import java.util.List;

public class LegacyOrderManager {
    private static LegacyOrderManager instance;
    private List<StockUpdateListener> listeners = new ArrayList<>();
    private int currentStock = 3;

    private LegacyOrderManager() {}

    public static LegacyOrderManager getInstance() {
        if (instance == null) {
            instance = new LegacyOrderManager();
        }
        return instance;
    }

    public interface StockUpdateListener {
        void onStockUpdated(int newStock);
    }

    public interface PurchaseCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void addStockListener(StockUpdateListener listener) {
        listeners.add(listener);
        // リスナー登録直後に最新の在庫を通知する仕様
        listener.onStockUpdated(currentStock);
    }

    // 商品購入処理（既存のレガシーAPI通信をスレッドでシミュレート）
    public void purchaseItem(String itemId, PurchaseCallback callback) {
        new Thread(() -> {
            try {
                // 通信遅延のシミュレート
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (currentStock > 0) {
                currentStock--;
                for (StockUpdateListener l : listeners) {
                    l.onStockUpdated(currentStock);
                }
                callback.onSuccess();
            } else {
                callback.onFailure("在庫切れです。");
            }
        }).start();
    }
}
