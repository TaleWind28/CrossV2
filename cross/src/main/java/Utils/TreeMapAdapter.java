package Utils;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import Commands.Orders.Limitorder;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapAdapter {

    @FromJson
    public TreeMap<String, Limitorder> fromJson(Map<String, Limitorder> map) {
        return new TreeMap<>(map);
    }

    @ToJson
    public TreeMap<String, Limitorder> toJson(TreeMap<String, Limitorder> treeMap) {
        return treeMap;
    }
}
