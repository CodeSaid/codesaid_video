package com.codesaid.lib_network.cache;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

/**
 * Created By codesaid
 * On :2020-05-06 00:28
 * Package Name: com.codesaid.lib_network.cache
 * desc:
 */

@Entity(tableName = "cache")
public class Cache implements Serializable {

    @PrimaryKey
    @NonNull
    public String key;

    public byte[] data;
}
