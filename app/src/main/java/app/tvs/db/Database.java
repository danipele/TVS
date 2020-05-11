package app.tvs.db;

import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import app.tvs.db.converters.BitmapConverter;
import app.tvs.db.converters.SeenStateConverter;
import app.tvs.db.converters.StateConverter;
import app.tvs.entities.Episode;
import app.tvs.entities.Season;
import app.tvs.entities.TVSeries;
import app.tvs.entities.TVSeriesShort;

@android.arch.persistence.room.Database(entities = {TVSeries.class, Season.class, Episode.class, TVSeriesShort.class}, version = 4, exportSchema = false)
@TypeConverters({StateConverter.class, SeenStateConverter.class, BitmapConverter.class})
public abstract class Database extends RoomDatabase {

    public abstract Dao dao();

}
