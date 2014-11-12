package com.palmer.thestoryteller.data;

import java.util.List;

/**
 * Created by Thom on 11/9/2014.
 */
public class Page {

    private long id;
    private long bookId;
    private String audioPath;
    private String imagePath;
    private List<Sound> soundList;

    public Page() {
    }

    public Page(long bookId, String imagePath) {
        this.bookId = bookId;
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Sound> getSoundList() {
        return soundList;
    }

    public void setSoundList(List<Sound> soundList) {
        this.soundList = soundList;
    }
}
