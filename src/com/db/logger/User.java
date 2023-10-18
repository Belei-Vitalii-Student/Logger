package com.db.logger;

public class User {
    private Integer id;
    private String name;
    private Integer age;
    private String favoriteColor;

    User(Integer id, String name, Integer age, String favoriteColor) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.favoriteColor = favoriteColor;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

}
