package JsonMemories;

import java.util.Map;

import Commands.Orders.Limitorder;


public class OrderClass {
    Map<String,Limitorder> askMap;
    Map<String,Limitorder>bidMap;
    public OrderClass(Map<String,Limitorder> askMap, Map<String,Limitorder>bidMap){
        this.askMap = askMap;
        this.bidMap = bidMap;
    }
}
