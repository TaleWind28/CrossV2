package Utils;

import java.util.Comparator;

public class PriceComparator implements Comparator<String>{
    public int compare(String s1, String s2){
        int p1 = Integer.parseInt(s1.split(":")[1]);
        int p2 = Integer.parseInt(s2.split(":")[1]);
        return Integer.compare(p1, p2);
    }
}
