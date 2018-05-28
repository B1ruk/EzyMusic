package io.starter.biruk.ezymusic.model.entity;



import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Biruk on 10/7/2017.
 */
@DatabaseTable
public class Playlist{

    public final static String ID_NAME="id";

    @DatabaseField(id = true,columnName = ID_NAME)
    public String name;

    @DatabaseField
    public String date;

    public Playlist() {
    }
}
