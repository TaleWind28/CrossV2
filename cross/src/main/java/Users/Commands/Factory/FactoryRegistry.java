package Users.Commands.Factory;
import java.util.HashMap;
import java.util.Map;

import JsonMemories.JsonAccessedData;

public class FactoryRegistry {

    private static final Map<Integer, UserCommandFactory> factories = new HashMap<>();

    static {
        factories.put(0, new CredentialsFactory());
        factories.put(1, new OrderFactory());
        factories.put(2, new InternalServerFactory());
    }

    public static UserCommandFactory getFactory(int factoryCode) {
        if (!factories.containsKey(factoryCode))throw new IllegalArgumentException("Tipo di comando non supportato: " + factoryCode);
        UserCommandFactory factory = factories.get(factoryCode);
        return factory;
    }

    public static void updateFactoryData(int factoryCode,JsonAccessedData data, String otherinfo){
        FactoryRegistry.getFactory(factoryCode).setJsonDataStructure(data);
        FactoryRegistry.getFactory(factoryCode).additionalInfo(otherinfo);
        return;
    }

}
