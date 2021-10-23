package io.github.softeng3062021.team21.Models;

import java.util.List;

/**
 * This class is the interface for Item that contains methods specifically for getting fields from
 * the Songs, Albums and Podcasts.
 *
 * @author Kinan Cheung
 * @author Maggie Pedersen
 * @author Flynn Fromont
 */
public interface IItem {

    String getName();

    String getDescription();

    long getNumHearts();

    void setNumHearts(long numHearts);

    String getPrice();

    String getAudioSample();

    List<String> getImages();

    String getLogoImage();

    String getArtist() throws IncorrectSubTypeException;

    String getLength() throws IncorrectSubTypeException;

    List<String> getHosts() throws IncorrectSubTypeException;

    List<String> getSongs() throws IncorrectSubTypeException;

    long getNumEpisodes() throws IncorrectSubTypeException;

    int getViewType();

    String getCategory();

    String getId();

    String getCategoryCollection();
}
