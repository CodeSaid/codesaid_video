package com.codesaid.lib_network.cache;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.codesaid.lib_base.AppGlobals;

/**
 * Created By codesaid
 * On :2020-05-06 00:10
 * Package Name: com.codesaid.lib_network.cache
 * desc:
 */

@Database(entities = {Cache.class}, version = 1, exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {

    private static final CacheDatabase DATABASE;

    static {

        /**
         * 该方法表示创建一个内存数据库，即数据只存在于内存当中，当 app进程被 kill。数据随之丢失
         */
        ////Room.inMemoryDatabaseBuilder();

        DATABASE = Room
                .databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "codesaid_video_cache")
                //是否允许在主线程进行查询
                //.allowMainThreadQueries()
                //数据库创建和打开后的回调
                //.addCallback()
                //设置查询的线程池
                //.setQueryExecutor()
                //.openHelperFactory()
                //room的日志模式
                //.setJournalMode()
                //数据库升级异常之后的回滚
                //.fallbackToDestructiveMigration()
                //数据库升级异常后根据指定版本进行回滚
                //.fallbackToDestructiveMigrationFrom()
                // .addMigrations(CacheDatabase.sMigration)
                .build();
    }

    public static CacheDatabase get() {
        return DATABASE;
    }
}
