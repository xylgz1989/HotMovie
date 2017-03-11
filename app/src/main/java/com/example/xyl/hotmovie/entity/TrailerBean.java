package com.example.xyl.hotmovie.entity;

/**
 * Movie trailer entity
 * Created by xyl on 2017/1/31 0031.
 */

public class TrailerBean {

//    "id": "571cdc48c3a3684e620018b8",
//            "iso_639_1": "en",
//            "iso_3166_1": "US",
//            "key": "i-80SGWfEjM",
//            "name": "Official Teaser Trailer",
//            "site": "YouTube",
//            "size": 1080,
//            "type": "Trailer"
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private int size;

    public static final String YOUTUBE = "YouTube";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
