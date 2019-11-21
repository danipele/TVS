package app.tvs;

import app.tvs.db.Database;

public class Global {

    public static Database database;

    public enum STATES{
        ON_GOING,
        IN_STAND_BY,
        FINISHED,
        UNDECLARED
    }

    public enum SEENSTATES {
        PLAY,
        PAUSE,
        STOP,
        UNDECLARED
    }

}
