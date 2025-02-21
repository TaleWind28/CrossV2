package JsonUtils.Orderbook;

import java.util.Map;

import Commands.Orders.Limitorder;
import Utils.OrderSorting;


public class OrderClass {
    Map<OrderSorting,Limitorder> askMap;
    Map<OrderSorting,Limitorder> bidMap;
    public OrderClass(Map<OrderSorting,Limitorder> askMap, Map<OrderSorting,Limitorder>bidMap){
        this.askMap = askMap;
        this.bidMap = bidMap;
    }
}
