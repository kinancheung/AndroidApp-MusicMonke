package io.github.softeng3062021.team21.Models;

import java.util.List;

/**
 * This is the abstract class that implements IItem. It is the parent of the Song, Album and Podcast
 * class whose methods are inherited for populating and setting items in the database
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
abstract class Item implements IItem {

    protected String name;
    protected String description;
    protected long numHearts;
    protected List<String> images;
    protected String price;
    protected String audioSample;

    public Item() {
    }

    public Item(String name, String description, long numHearts, List<String> images, String price, String audioSample) {
        this.name = name;
        this.description = description;
        this.numHearts = numHearts;
        this.images = images;
        this.price = price;
        this.audioSample = audioSample;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getNumHearts() {
        return numHearts;
    }

    public void setNumHearts(long likes) {
        this.numHearts = likes;
    }

    public List<String> getImages() {
        return images;
    }

    public String getLogoImage() {
        return images.get(0);
    }

    public String getPrice() {
        return price;
    }

    public String getAudioSample() {
        return audioSample;
    }

    public abstract String getArtist() throws IncorrectSubTypeException;

    public abstract String getLength() throws IncorrectSubTypeException;

    public abstract List<String> getHosts() throws IncorrectSubTypeException;

    public abstract List<String> getSongs() throws IncorrectSubTypeException;

    public abstract long getNumEpisodes() throws IncorrectSubTypeException;

    public abstract int getViewType();

    public abstract String getCategory();

    public abstract String getId();

    public abstract String getCategoryCollection();
}
