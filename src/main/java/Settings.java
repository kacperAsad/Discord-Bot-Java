import MapIO.MapReader;
import MapIO.MapSaver;

import java.util.HashMap;

public class Settings {

    public static HashMap<String, String> keyValue = new HashMap<>();

    public static String getValue(String key){
        return keyValue.getOrDefault(key, "");
    }
    public static void pushValue(String key, String value){
        keyValue.put(key, value);
    }
    public static void removeKey(String key){
        keyValue.remove(key);
    }
    public static boolean contains(String key){
        return keyValue.containsKey(key);
    }
    public static boolean saveToFile(String fileName){
        return MapSaver.saveMapInFile(fileName, keyValue);
    }
    public static boolean readFromFile(String filename){
        var map = MapReader.loadMapWithFile(filename);
        return map != null;
    }
}
