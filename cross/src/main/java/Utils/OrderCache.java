package Utils;

import java.util.ArrayList;

import Commands.Orders.Limitorder;

public class OrderCache {
    private ArrayList<Limitorder> orderCache = new ArrayList<>();
    private int size = 0;
    public void setOrderCache(ArrayList<Limitorder> orderCache) {
        this.orderCache = orderCache;
        this.size = orderCache.size();
    }

    public void addOrder(Limitorder ord){
        this.orderCache.add(ord);
        this.size++;
        return;
    }

    public Limitorder removeOrder(){
        return this.orderCache.remove(0);
    }
    public int getSize() {
        return size;
    }

}
