package io.github.softeng3062021.team21.Models;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

/**
 * This class extends Item and is responsible for handling all information pertaining an Album.
 * This includes the Artist and List of Songs.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class Album extends Item {

    public final static int ALBUM_VIEW_TYPE = 1;

    public final static String ALBUM = "Albums";
    public final static String ALBUM_COLLECTION = "albums";

    @DocumentId
    private String id;
    private String artist;
    private List<String> songs;

    public Album() {
    }

    public Album(String artist, String description, List<String> images, String name, long numHearts, List<String> songs, String price, String audioSample) {
        super(name, description, numHearts, images, price, audioSample);
        this.artist = artist;
        this.songs = songs;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public List<String> getSongs() {
        return songs;
    }

    @Override
    public List<String> getHosts() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Album does not implement the 'getHost' method");
    }

    @Override
    public long getNumEpisodes() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Album does not implement the 'getNumEpisodes' method");
    }

    @Override
    public String getLength() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Album does not implement the 'getLength' method");
    }

    @Override
    public int getViewType() {
        return ALBUM_VIEW_TYPE;
    }

    @Override
    public String getCategory() {
        String category = ALBUM;
        return category.substring(0, category.length() - 1);
    }
    @Override
    public String getId(){
        return this.id;
    }
    @Override
    public String getCategoryCollection() {
        return ALBUM_COLLECTION;
    }
}
