package com.cloud.dao.model;

import javax.persistence.*;

public class Location {
    @Id
    private Long id;

    private String prefectural;

    private String county;

    @Column(name = "community_namw")
    private String communityNamw;

    private String longitude;

    private String latitude;

    @Column(name = "gaode_name")
    private String gaodeName;

    private String level;

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return prefectural
     */
    public String getPrefectural() {
        return prefectural;
    }

    /**
     * @param prefectural
     */
    public void setPrefectural(String prefectural) {
        this.prefectural = prefectural;
    }

    /**
     * @return county
     */
    public String getCounty() {
        return county;
    }

    /**
     * @param county
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * @return community_namw
     */
    public String getCommunityNamw() {
        return communityNamw;
    }

    /**
     * @param communityNamw
     */
    public void setCommunityNamw(String communityNamw) {
        this.communityNamw = communityNamw;
    }

    /**
     * @return longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * @return gaode_name
     */
    public String getGaodeName() {
        return gaodeName;
    }

    /**
     * @param gaodeName
     */
    public void setGaodeName(String gaodeName) {
        this.gaodeName = gaodeName;
    }

    /**
     * @return level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public void setLevel(String level) {
        this.level = level;
    }
}