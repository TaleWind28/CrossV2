package Communication;

import JsonMemories.JsonAccessedData;

public interface Values {
    @Override
    public String toString();
    public ServerMessage execute(JsonAccessedData data);
}
