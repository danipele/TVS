package app.tvs.db.converters;

import android.arch.persistence.room.TypeConverter;

import app.tvs.Global;

public class SeenStateConverter {


    @TypeConverter
    public static Global.SEENSTATES toSeenState(int value) {
        return Global.SEENSTATES.values()[value];
    }

    @TypeConverter
    public static int toInt(Global.SEENSTATES state) {
        return state.ordinal();
    }
}
