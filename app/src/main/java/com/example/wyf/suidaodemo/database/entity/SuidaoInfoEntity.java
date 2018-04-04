package com.example.wyf.suidaodemo.database.entity;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "suidaoinfo")
public class SuidaoInfoEntity implements Serializable {
    private static final long serialVersionUID = 4907537111840393328L;

    @DatabaseField(columnName = "id", dataType = DataType.INTEGER, generatedId = true)
    private int id;
    @DatabaseField(columnName = "line", dataType = DataType.STRING, canBeNull = false)
    private String line;
    @DatabaseField(columnName = "section", dataType = DataType.STRING, canBeNull = false)
    private String section;
    @DatabaseField(columnName = "tunnel", dataType = DataType.STRING, canBeNull = false)
    private String tunnel;
    @DatabaseField(columnName = "kilo", dataType = DataType.INTEGER, canBeNull = false)
    private int kilo;
    @DatabaseField(columnName = "meter", dataType = DataType.INTEGER, canBeNull = false)
    private int meter;
    @DatabaseField(columnName = "picpath", dataType = DataType.STRING, canBeNull = true)
    private String picpath;
    @DatabaseField(columnName = "piclatitude", dataType = DataType.STRING, canBeNull = true)
    private String piclatitude;
    @DatabaseField(columnName = "piclongitude", dataType = DataType.STRING, canBeNull = true)
    private String piclongitude;
    @DatabaseField(columnName = "createtime", dataType = DataType.STRING, canBeNull = false)
    private String createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTunnel() {
        return tunnel;
    }

    public void setTunnel(String tunnel) {
        this.tunnel = tunnel;
    }

    public int getKilo() {
        return kilo;
    }

    public void setKilo(int kilo) {
        this.kilo = kilo;
    }

    public int getMeter() {
        return meter;
    }

    public void setMeter(int meter) {
        this.meter = meter;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getPiclatitude() {
        return piclatitude;
    }

    public void setPiclatitude(String piclatitude) {
        this.piclatitude = piclatitude;
    }

    public String getPiclongitude() {
        return piclongitude;
    }

    public void setPiclongitude(String piclongitude) {
        this.piclongitude = piclongitude;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", line='" + line + '\'' +
                ", section='" + section + '\'' +
                ", tunnel='" + tunnel + '\'' +
                ", kilo=" + kilo +
                ", meter=" + meter +
                ", picpath='" + picpath + '\'' +
                ", piclatitude='" + piclatitude + '\'' +
                ", piclongitude='" + piclongitude + '\'' +
                ", createtime='" + createtime + '\'' +
                '}';
    }
}
