package com.company.survey;

public class FormModel {
    String name,image,latitude,longitude,hole;

    public FormModel(String name, String image, String latitude, String longitude, String hole) {
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hole = hole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getHole() {
        return hole;
    }

    public void setHole(String hole) {
        this.hole = hole;
    }
}
