package vmarcinko.nanocubes.gowalla;

import vmarcinko.nanocubes.LabellingFn;
import vmarcinko.nanocubes.Nanocube;
import vmarcinko.nanocubes.Schema;
import vmarcinko.nanocubes.TimeLabellingFn;
import vmarcinko.nanocubes.quadtree.QuadTreeConverter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Dataset taken from: http://snap.stanford.edu/data/loc-gowalla.html
 */
public class GowallaTest {
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'");

    public static void main(String[] args) throws IOException, ParseException {
/*
        LabellingFn<CheckinRecord> fn = constructGeoLocationLabellingFn(15);
        Object label = fn.label(new CheckinRecord(new Date(), 54.54353453, 22.645645));
        System.out.println("label = " + label);
*/
        Schema<CheckinRecord> schema = constructSchema();
        TimeLabellingFn<CheckinRecord> timeLabellingFn = constructTimeLabellingFn();
        Nanocube<CheckinRecord> nanocube = new Nanocube<>(schema, timeLabellingFn);

        long indexingStartTime = System.currentTimeMillis();
        insertRecordsIntoNanocube(nanocube, 1000000);

        System.out.println("");
        System.out.println("--------------------------------");
        printIndexingDuration(indexingStartTime);

        printMemoryState();

//        System.out.println("nanocube = " + nanocube.toPrettyString());
    }

    private static void printIndexingDuration(long indexingStartTime) {
        long durationInMillis = System.currentTimeMillis() - indexingStartTime;
        System.out.println("Indexing took " + (durationInMillis / (float) (1000 * 60)) + " mins");
    }

    private static void printMemoryState() {
        long totalMem = convertToMB(Runtime.getRuntime().totalMemory());
        long freeMem = convertToMB(Runtime.getRuntime().freeMemory());
        long usedMem = totalMem - freeMem;
        System.out.println(
                "Total mem: " + totalMem + " MB" +
                ", Free mem: " + freeMem + " MB" +
                ", Used mem: " + usedMem + " MB"
        );
    }

    private static long convertToMB(long byteCount) {
        return byteCount / (1024 * 1024);
    }

    private static TimeLabellingFn<CheckinRecord> constructTimeLabellingFn() {
        return new TimeLabellingFn<CheckinRecord>() {
            @Override
            public long label(CheckinRecord dataPoint) {
                long time = dataPoint.getTime().getTime();
                long hourLengthInMillis = 1000 * 60 * 60;
                long hourBinTimestamp = time / hourLengthInMillis;
                return hourBinTimestamp;
            }
        };
    }

    private static Schema<CheckinRecord> constructSchema() {
        Schema<CheckinRecord> schema = new Schema<>();
        List<LabellingFn<CheckinRecord>> locationChain = schema.addChain();
        for (int i = 1; i < 20; i++) {
            locationChain.add(constructGeoLocationLabellingFn(i));
        }

        List<LabellingFn<CheckinRecord>> hourOfDayChain = schema.addChain();
        hourOfDayChain.add(constructHourOfDayLabellingFn());

        List<LabellingFn<CheckinRecord>> dayOfWeekChain = schema.addChain();
        dayOfWeekChain.add(constructDayOfWeekLabellingFn());

        return schema;
    }

    private static LabellingFn<CheckinRecord> constructGeoLocationLabellingFn(final int depth) {
        return new LabellingFn<CheckinRecord>() {
            @Override
            public long label(CheckinRecord dataPoint) {
                int precision = 10000;
                int positiveLat = (int) ((dataPoint.getLatitude() + 90) * precision);
                int positiveLong = (int) ((dataPoint.getLongitude() + 180) * precision);
                return QuadTreeConverter.convert(positiveLat, positiveLong, 180 * precision, 360 * precision, depth);
            }
        };
    }

    private static LabellingFn<CheckinRecord> constructHourOfDayLabellingFn() {
        return new LabellingFn<CheckinRecord>() {
            @Override
            public long label(CheckinRecord dataPoint) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataPoint.getTime());
                int dayOfWeek = calendar.get(Calendar.HOUR_OF_DAY);
                return dayOfWeek;
            }
        };
    }

    private static LabellingFn<CheckinRecord> constructDayOfWeekLabellingFn() {
        return new LabellingFn<CheckinRecord>() {
            @Override
            public long label(CheckinRecord dataPoint) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dataPoint.getTime());
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                return dayOfWeek;
            }
        };
    }

    private static void insertRecordsIntoNanocube(Nanocube<CheckinRecord> nanocube, int limit) throws IOException, ParseException {
        // example line: 196578	2010-06-11T13:32:40Z	51.7459706073	-0.4852330685	965051
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/home/vmarcinko/nanocubes/Gowalla_totalCheckins.txt"));

            int count = 0;
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.trim().equals("")) {
                    count++;
                    if (limit > 0 && count > limit) {
                        break;
                    }
                    String[] splits = line.trim().split("\\s+");
                    Date time = dateFormat.parse(splits[1]);
                    Double coordX = Double.parseDouble(splits[2]);
                    Double coordY = Double.parseDouble(splits[3]);
                    CheckinRecord record = new CheckinRecord(time, coordX, coordY);
                    nanocube.insert(record);

                    if (count % 30000 == 0) {
                        System.out.println("Inserted " + count + " records into nanocube...");
                        System.gc();
                    }
                }
            }
            System.gc();

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
