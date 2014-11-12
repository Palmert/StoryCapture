package com.palmer.thestoryteller.data;

import java.util.List;

/**
 * Created by Thom on 11/9/2014.
 */
public class Book {

    private long id;
    private String imagePath;
    private List<Page> pageList;

    public Book(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Page> getPageList() {
        return pageList;
    }

    public void setPageList(List<Page> pageList) {
        this.pageList = pageList;
    }
}
