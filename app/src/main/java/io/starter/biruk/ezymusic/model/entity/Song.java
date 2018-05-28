package io.starter.biruk.ezymusic.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Biruk on 10/7/2017.
 */

@DatabaseTable
public class Song{

    public final static String ID_NAME="id";

    @DatabaseField
    public long songId;

    @DatabaseField
    public long albumId;

    @DatabaseField
    public String artist;

    @DatabaseField
    public String title;

    @DatabaseField
    public String albumTitle;

    @DatabaseField
    public long duration;

    @DatabaseField
    public int trackNumber;

    @DatabaseField
    public boolean favorite;

    @DatabaseField(id = true,columnName = ID_NAME)
    public String data;

    public Song(){

    }

}
