package com.example.xyl.hotmovie.entity;

import java.io.Serializable;

/**
 * movie info entry
 * Created by xyl on 2017/1/7 0007.
 */

public class MovieBean implements Serializable {
    /**上映时间*/
    private String firstShowTime;
    /**演员*/
    private String actors;
    /**电影名称*/
    private String movieName;
    /**电影介绍*/
    private String introduction;
    /**评分*/
    private float score;
    /**海报图片的地址*/
    private String posterUrl;
    /**电影ID*/
    private int movieId;
    /**在数据库内的ID*/
    private int insertId;

    public int getInsertId() {
        return insertId;
    }

    public void setInsertId(int insertId) {
        this.insertId = insertId;
    }

    public String getFirstShowTime() {
        return firstShowTime;
    }

    public void setFirstShowTime(String firstShowTime) {
        this.firstShowTime = firstShowTime;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        if(score < 0){
            score = 0;
        }
        this.score = score;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int id) {
        this.movieId = id;
    }

    @Override
    public String toString() {
        return "MovieBean{" +
                "firstShowTime='" + firstShowTime + '\'' +
                ", actors='" + actors + '\'' +
                ", movieName='" + movieName + '\'' +
                ", introduction='" + introduction + '\'' +
                ", score=" + score +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }
}
