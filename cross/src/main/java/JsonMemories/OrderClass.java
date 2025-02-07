package JsonMemories;

import java.util.TreeMap;

import Commands.Orders.Limitorder;


public class OrderClass {
    TreeMap<String,Limitorder> askMap;
    TreeMap<String,Limitorder>bidMap;
    public OrderClass(TreeMap<String,Limitorder> askMap, TreeMap<String,Limitorder>bidMap){
        this.askMap = askMap;
        this.bidMap = bidMap;
    }
}
