package app.tvs.services;

import android.content.Context;

import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import app.tvs.Global;
import app.tvs.db.Database;
import app.tvs.entities.TVSeriesShort;
import app.tvseries.R;

public class UpdateTVSeriesShortService {

    public static void update(Context context) {
        String message = "";
        try {
            int addedNew = 0;
            Database database = Global.database;
            InputStream input = new BufferedInputStream(new InputSource(new GZIPInputStream(new URL(context.getString(R.string.dbFileLink)).openConnection().getInputStream())).getByteStream());
            byte[] data = new byte[2097152];
            int count;
            String endMargins = "";

            String dataString, file = "";
            String[] lines;
            while ((count = input.read(data)) != -1) {
                dataString = new String(data, 0, count, context.getString(R.string.UTF8));
                lines = dataString.split(context.getString(R.string.newLine));
                for(String line : lines) {
                    if(line.equals(lines[lines.length - 1])) {
                        endMargins = line;
                    }
                    else if(line.equals(lines[0])) {
                        if(!line.equals(context.getString(R.string.dbFileHeader))) {
                            if(line.length() >= 9 && line.substring(0, 9).matches(context.getString(R.string.startsWithIdMatcher))) {
                                addedNew += updateTVSeriesShort(endMargins, database, context);
                                addedNew += updateTVSeriesShort(line, database, context);
                            } else {
                                addedNew += updateTVSeriesShort(endMargins.concat(line), database, context);
                            }
                        }
                    }
                    else {
                        addedNew += updateTVSeriesShort(line, database, context);
                    }
                }
            }


            System.out.print(file);
            input.close();
            message = "TVS database was updated. " + addedNew + " T.V. Series were added";

        }
        catch (Exception e) {
            message = "TVS database couldn't be updated";
        }
        finally {
            NotificationService.addNotification(message, context, "C2", "UpdateTVSeriesShort", "Update TVSeriesShort table");
        }

    }

    private static boolean TVSeriesConditions(String[] columns, Context context) {
        return (columns[1].equals(context.getString(R.string.tvSeries)) || columns[1].equals(context.getString(R.string.tvMiniSeries))) && !columns[5].equals(context.getString(R.string.unknownValue)) && !columns[7].equals(context.getString(R.string.unknownValue)) && !columns[8].contains(context.getString(R.string.TalkShow)) && !columns[8].contains(context.getString(R.string.GameShow)) && !columns[8].contains(context.getString(R.string.RealityTV)) && !columns[8].contains(context.getString(R.string.News)) && !columns[8].contains(context.getString(R.string.Short)) && !columns[8].contains(context.getString(R.string.Documentary)) && !columns[8].contains(context.getString(R.string.Sport)) && !columns[8].contains(context.getString(R.string.Adult)) && !columns[8].contains(context.getString(R.string.Music)) && !columns[8].equals(context.getString(R.string.unknownValue)) && !columns[8].contains(context.getString(R.string.Animation)) && Integer.parseInt(columns[5]) > 1990 && Integer.parseInt(columns[7]) > 20;
    }

    private static int updateTVSeriesShort(String line, Database database, Context context) {
        String[] columns = line.split(context.getString(R.string.tab));
        if (TVSeriesConditions(columns, context)) {
            TVSeriesShort tvSeriesShort = new TVSeriesShort(columns[0], columns[2], columns[5], columns[6]);
            if(database.dao().getTVSeriesShortWithId(tvSeriesShort.getId()) == 1) {
                database.dao().updateTVSeriesShort(tvSeriesShort);
            }
            else {
                database.dao().addTVSeriesShort(tvSeriesShort);
                return 1;
            }
        }
        return 0;
    }

}
