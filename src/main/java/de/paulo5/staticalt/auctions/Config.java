package de.paulo5.staticalt.auctions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jdi.InvalidTypeException;
import org.codehaus.plexus.util.StringInputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Config {

    public JSONObject data;
    private File file = null;
    private Config parent = null;
    private String key;

    public Config(InputStream is) throws IOException, ParseException {
        JSONParser p = new JSONParser();
        data = (JSONObject) p.parse(new InputStreamReader(is));
    }

    public Config(String string) throws IOException, ParseException {
        this(new StringInputStream(string));
    }

    public Config(File file) throws IOException, ParseException {
        this(new FileInputStream(file));
        this.file = file;
    }

    public Config(JSONObject o) {
        data = Objects.requireNonNullElseGet(o, JSONObject::new);
    }

    private Config(Config parent, String key, JSONObject value) {
        this(value);
        this.parent = parent;
        this.key = key;
    }

    public Config() {
        this(new JSONObject());
    }

    @Override
    public String toString() {
        return "Config@" + toJSONString();
    }

    public String toJSONString() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    protected <T> Object objectToJASONObject(T value) {
        if (value instanceof Config) {
            return ((Config) value).toJSON();
        } else if (value instanceof ConfigObject) {
            Config objectSave = ((ConfigObject<?>) value).finalSave();
            if (objectSave == null) {
                return null;
            }
            return objectSave.toJSON();
        } else if (value instanceof Long) {
            return "" + value;
        }
        return value;
    }

    public <T> void set(String st, T o) {
        data.put(st, objectToJASONObject(o));
        save();
    }

    public boolean contains(String s) {
        return data.containsKey(s);
    }

    public void save() {
        if (parent != null) {
            parent.set(key, data);
        }
        if (file != null) {
            BufferedWriter w = null;
            try {
                w = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8));
                w.write(toJSONString());
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (w != null) {
                    try {
                        w.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    public File getFile() {
        if (parent != null) {
            return parent.getFile();
        }
        return file;
    }

    public <E> E get(String key, Class<E> type) {
        return JSONObjectToObject(key, type);
    }

    private <E> E JSONObjectToObject(String key, Class<E> type) {
        Object value = data.get(key);
        if (type == Long.class) {
            if (value == null) {
                return (E) Long.valueOf(0);
            }
            if (value instanceof String) {
                return (E) Long.valueOf(Long.parseLong(String.valueOf(value)));
            }
        } else if (type == Double.class) {
            if (value == null) {
                return (E) Double.valueOf(0);
            }
            if (value instanceof Long) {
                return (E) Double.valueOf((Long) value);
            }
            if (value instanceof String) {
                return (E) Double.valueOf((String) value);
            }
            if (value instanceof Float) {
                return (E) Double.valueOf((Float) value);
            }
        } else if (value instanceof JSONObject) {
            value = new Config(this, key, (JSONObject) value);
            if (((Config) value).contains("CLASS")) {
                return (E) ConfigObject.get((Config) value);
            }
            return (E) value;
        } else if (value instanceof Long) {
            value = Integer.valueOf(((Long) value).intValue());
        } else if (type == Config.class && value == null) {
            return (E) new Config(this, key, new JSONObject());
        } else if (type == Integer.class && value == null) {
            return (E) Integer.valueOf(0);
        }
        return (E) value;
    }

    public JSONObject toJSON() {
        return data;
    }

    public void remove(String name) {
        data.remove(name);
        save();
    }

    public void setArray(String key, List<?> list) {
        JSONArray array = new JSONArray();
        for (Object object : list) {
            array.add(objectToJASONObject(object));
        }
        set(key, array);
    }

    public void setInArray(String key, int index, Object object) {
        JSONArray array = get(key, JSONArray.class);
        if (array == null) {
            array = new JSONArray();
        }
        array.set(index, objectToJASONObject(object));
        set(key, array);
    }

    public void addToArray(String key, Object object) {
        JSONArray array = get(key, JSONArray.class);
        if (array == null) {
            array = new JSONArray();
        }
        array.add(objectToJASONObject(object));
        set(key, array);
    }

    public <T, E> ArrayList<E> getArray(String key, Class<E> cast) {
        ArrayList<E> list = new ArrayList<>();
        if (contains(key)) {
            for (Object o : get(key, JSONArray.class)) {
                E castO;
                if (cast == Config.class) {
                    castO = (E) new Config((JSONObject) o);
                } else if (ConfigObject.class.isAssignableFrom(cast)) {
                    Config c = new Config((JSONObject) o);
                    castO = (E) ConfigObject.get(c);
                } else {
                    castO = cast.cast(o);
                }
                list.add(castO);
            }
        }
        return list;
    }

    public void clearArray(String key) {
        JSONArray array = get(key, JSONArray.class);
        if (array != null) {
            array.clear();
            set(key, array);
        }
    }

    public List<String> keys() {
        List<String> keys = new ArrayList<>();
        for (Object o : data.keySet()) {
            if (o instanceof String) {
                keys.add((String) o);
            }
        }
        return keys;
    }

    public void clear() {
        data.clear();
        save();
    }

    public <T> boolean testSchema(Map<String, T> schema) throws InvalidTypeException {
        for (String key : schema.keySet()) {
            if (contains(key)) {
                T value = schema.get(key);
                Object inValue = get(key, Object.class);
                if (value instanceof Map) {
                    if (inValue.getClass() != Config.class) {
                        return false;
                    }
                    if (!((Config) inValue).testSchema((Map<String, T>) value)) {
                        return false;
                    }
                } else if (value instanceof Class) {
                    if (value == Number.class) {
                        if (!Arrays.asList(Integer.class, Long.class, Double.class).contains(inValue.getClass())) {
                            return false;
                        }
                    } else {
                        if (!inValue.getClass().equals(value)) {
                            return false;
                        }
                    }
                } else {
                    throw new InvalidTypeException("Value Type Can only be Class or Map");
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public abstract static class ConfigObject<T extends ConfigObject<T>> {

        private static HashMap<Class<? extends ConfigObject<?>>, ConfigObjectLoader<?>> loader = new HashMap<>();

        public ConfigObject() {
            loader.put((Class<ConfigObject<?>>) getClass(), loader());
        }

        public static Object get(Config c) {
            String clazzName = new String(Base64.getDecoder().decode(c.get("CLASS", String.class)));
            try {
                Class<?> clazz = Class.forName(clazzName);
                ConfigObjectLoader<?> loader = ConfigObject.loader.get(clazz);
                return loader.load(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public abstract Config save() throws Exception;

        private Config finalSave() {
            try {
                Config c = save();
                c.set("CLASS", new String(Base64.getEncoder().encode(getClass().getName().getBytes())));
                return c;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public abstract ConfigObjectLoader<T> loader();

        public interface ConfigObjectLoader<T extends ConfigObject<T>> {

            T load(Config c) throws Exception;

        }
    }
}
