package com.codesaid.lib_network.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created By codesaid
 * On :2020-05-06 01:30
 * Package Name: com.codesaid.lib_network.cache
 * desc:
 */
public class CacheManager {

    public static <T> void save(String key, T body) {
        Cache cache = new Cache();
        cache.key = key;
        cache.data = toByteArray(body);

        CacheDatabase.get().getCache().save(cache);
    }

    public static Object getCache(String key) {
        Cache cache = CacheDatabase.get().getCache().getCache(key);
        if (cache != null && cache.data != null) {
            return toObject(cache.data);
        }
        return null;
    }

    private static Object toObject(byte[] data) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        try {
            bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(ois);
            return ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private static <T> byte[] toByteArray(T body) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(body);
            oos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new byte[0];
    }
}
