package com.example.wyf.suidaodemo.database.dao;

import android.content.Context;

import com.example.wyf.suidaodemo.database.entity.SuidaoInfoEntity;
import com.example.wyf.suidaodemo.database.ormlite.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SuidaoInfoDao {

    private Dao<SuidaoInfoEntity, Integer> dao;

    public SuidaoInfoDao(Context context) {
        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(SuidaoInfoEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 向表中添加一条数据
    public void insert(SuidaoInfoEntity data) {
        try {
            dao.create(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 删除表中的一条数据
    public void delete(SuidaoInfoEntity data) {
        try {
            dao.delete(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 修改表中的一条数据
    public void update(SuidaoInfoEntity data) {
        try {
            dao.update(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 查询表中的所有数据
    public List<SuidaoInfoEntity> getAll() {
        List<SuidaoInfoEntity> suidaoInfoEntities = null;
        try {
            suidaoInfoEntities = dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suidaoInfoEntities;
    }

    // 根据id查询数据
    public SuidaoInfoEntity queryById(int id) {
        SuidaoInfoEntity suidaoInfoEntity = null;
        try {
            suidaoInfoEntity = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suidaoInfoEntity;
    }

    public Map<Integer, Map<String, String>> getAllPoint() {
        Map<Integer, Map<String, String>> points = new HashMap<>();
        Map<String, String> latLngMap = new HashMap<>(4, 0.5F);
        try {
            GenericRawResults<String[]> rawResults =
                    dao.queryRaw("select id,piclatitude,piclongitude from suidaoinfo");
            for (String[] resultArray : rawResults) {
                latLngMap.put(resultArray[1], resultArray[2]);
                points.put(Integer.valueOf(resultArray[0]), latLngMap);
            }
            return points;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getImagePathById(String id) {
        try {
            String[] path = {""};
            GenericRawResults<String[]> imagesPath =
                    dao.queryRaw("select picpath from suidaoinfo where id=" + id);
            for (String[] result : imagesPath) {
                path = result;
            }
            return path[0];
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public List<String> getFolderNameByDay() {
        List<String> folderNames = new ArrayList<>();
        try {
            GenericRawResults<String[]> times =
                    dao.queryRaw("select distinct date(createtime) from suidaoinfo " +
                            "order by date(createtime) desc");
            for (String[] time : times) {
                folderNames.add(time[0].replace("-", "."));
            }
            return folderNames;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folderNames;
    }

    public Map<Integer, String> getFolderNameByConstruction(String time) {
        Map<Integer, String> folderNames = new TreeMap<>();
        try {
            GenericRawResults<String[]> results =
                    dao.queryRaw("select id,tunnel from suidaoinfo where date(createtime)=" +
                            "date('" + time + "') order by id asc");
            for (String[] result : results) {
                folderNames.put(Integer.parseInt(result[0]), result[1]);
            }
            return folderNames;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folderNames;
    }

    public int updateImagesPathById(String newImagesPath, String id) {
        try {
            return dao.updateRaw("update suidaoinfo set picpath=? where id=?", newImagesPath, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
