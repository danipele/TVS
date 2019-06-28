package app.tvs.db.converters;

import android.arch.persistence.room.TypeConverter;

import app.tvs.Global;

public class StateConverter {


    @TypeConverter
    public static Global.STATES toState(int value) {
        return Global.STATES.values()[value];
    }

    @TypeConverter
    public static int toInt(Global.STATES state) {
        return state.ordinal();
    }
}
