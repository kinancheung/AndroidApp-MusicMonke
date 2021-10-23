package io.github.softeng3062021.team21.Models;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

/**
 * This class extends Item and is responsible for handling all information pertaining a Song.
 * This includes the Artist and Length of Song.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class Song extends Item {

    public final static int SONG_VIEW_TYPE = 0;

    public final static String SONG = "Songs";
    public final static String SONG_COLLECTION = "songs";

    @DocumentId
    private String id;
    private String artist;
    private String length;

    public Song() {
    }

    public Song(String artist, String description, List<String> images, String length, String name, long numHearts, String price, String audioSample) {
        super(name, description, numHearts, images, price, audioSample);
        this.artist = artist;
        this.length = length;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getLength() {
        return length;
    }

    @Override
    public List<String> getHosts() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Song does not implement the 'getHost' method");
    }

    @Override
    public List<String> getSongs() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Song does not implement the 'getSongs' method");
    }

    @Override
    public long getNumEpisodes() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Song does not implement the 'getNumEpisodes' method");
    }

    @Override
    public int getViewType() {
        return SONG_VIEW_TYPE;
    }

    @Override
    public String getCategory() {
        String category = SONG;
        return category.substring(0, category.length() - 1);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getCategoryCollection() {
        return SONG_COLLECTION;
    }
}
