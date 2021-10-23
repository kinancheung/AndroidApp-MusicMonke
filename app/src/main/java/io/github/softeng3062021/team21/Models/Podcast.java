package io.github.softeng3062021.team21.Models;

import com.google.firebase.firestore.DocumentId;

import java.util.List;

/**
 * This class extends Item and is responsible for handling all information pertaining a Podcast.
 * This includes the Hosts and Number of Episodes.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public class Podcast extends Item {

    public final static int PODCAST_VIEW_TYPE = 2;

    public final static String PODCAST = "Podcasts";
    public final static String PODCAST_COLLECTION = "podcasts";

    @DocumentId
    private String id;
    private List<String> hosts;
    private long numEpisodes;

    public Podcast() {
    }

    public Podcast(String description, List<String> hosts, List<String> images, String name, long numEpisodes, long numHearts, String price, String audioSample) {
        super(name, description, numHearts, images, price, audioSample);
        this.hosts = hosts;
        this.numEpisodes = numEpisodes;
    }

    @Override
    public List<String> getHosts() {
        return hosts;
    }

    @Override
    public long getNumEpisodes() {
        return numEpisodes;
    }

    @Override
    public List<String> getSongs() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Podcast does not implement the 'getSongs' method");
    }

    @Override
    public String getArtist() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Podcast does not implement the 'getArtist' method");
    }

    @Override
    public String getLength() throws IncorrectSubTypeException {
        throw new IncorrectSubTypeException("Podcast does not implement the 'getLength' method");
    }

    @Override
    public int getViewType() {
        return PODCAST_VIEW_TYPE;
    }

    @Override
    public String getCategory() {
        String category = PODCAST;
        return category.substring(0, category.length() - 1);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getCategoryCollection() {
        return PODCAST_COLLECTION;
    }
}
