package com.example.wyf.suidaodemo.database.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // 数据库名称
    private static final String DATABASE_NAME = "suidaodemo.db";

    // 本类的单例实例
    private static DatabaseHelper instance;

    // 存储APP中所有的DAO对象的Map集合
    private Map<String, Dao> daos = new HashMap<>();

    // 获取本类单例对象的方法
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context);
                }
            }
        }
        return instance;
    }

    // 私有化构造方法
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    // 根据传入的DAO的路径获取到这个DAO的单例对象（要么从daos这个Map中获取，要么新创建一个并存入daos）
    @Override
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }


//    public DatabaseHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
//        super(context, databaseName, factory, databaseVersion);
//    }

    /**
     * 创建数据库
     *
     * @param database
     * @param connectionSource
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SuidaoInfoEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 数据库更新
     *
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SuidaoInfoEntity.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}