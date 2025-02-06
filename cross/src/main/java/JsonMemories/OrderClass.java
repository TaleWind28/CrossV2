package JsonMemories;

import java.util.TreeMap;
import Users.Commands.Order;

public class OrderClass {
    TreeMap<String,Order> askMap;
    TreeMap<String,Order>bidMap;
    public OrderClass(TreeMap<String,Order> askMap, TreeMap<String,Order>bidMap){
        this.askMap = askMap;
        this.bidMap = bidMap;
    }
}
