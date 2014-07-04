package vmarcinko.nanocubes;

import vmarcinko.nanocubes.quadtree.QuadTreeConverter;
import vmarcinko.nanocubes.temporal.SummedTimeCountsTable;

import java.util.ArrayList;
import java.util.List;

public class NanocubesTest {
    public static void main(String[] args) {
        Schema<ExampleDataPoint> schema = prepareSchema();
        TimeLabellingFn<ExampleDataPoint> timeLabellingFn = prepareTimeLabellingFn();
        Nanocube<ExampleDataPoint> nanocube = new Nanocube<>(schema, timeLabellingFn);

        List<ExampleDataPoint> dataPoints = prepareDataPoints();
        for (ExampleDataPoint dataPoint : dataPoints) {
            nanocube.insert(dataPoint);
        }

        System.out.println("nanocube = " + nanocube.toPrettyString());

/*
        Node rootContent = nanocube.getRoot().getContent(Node.class);

        System.out.println("nanocube.getRoot().getContentLink().getTarget() = " + rootContent.getContent(SummedTimeCountsTable.class).queryTotalCount());

        Node rootAndroidNode = rootContent.getChild(ExampleDataPoint.DeviceType.ANDROID);
        System.out.println("rootContent.getChildLinks().keySet() = " + rootAndroidNode.getContent(SummedTimeCountsTable.class).queryTotalCount());

        Node rootIphoneNode = rootContent.getChild(ExampleDataPoint.DeviceType.IPHONE);
        System.out.println("rootContent.getChildLinks().keySet() = " + rootAndroidNode.getContent(SummedTimeCountsTable.class).queryTotalCount());
*/
    }

    private static List<ExampleDataPoint> prepareDataPoints() {
        List<ExampleDataPoint> list = new ArrayList<>();
        list.add(new ExampleDataPoint(1, 2, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(1, 2, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(2, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        list.add(new ExampleDataPoint(2, 2, ExampleDataPoint.DeviceType.ANDROID, 0));
        list.add(new ExampleDataPoint(3, 1, ExampleDataPoint.DeviceType.IPHONE, 0));
        return list;
    }

    private static Schema<ExampleDataPoint> prepareSchema() {
        Schema<ExampleDataPoint> schema = new Schema<>();

        List<LabellingFn<ExampleDataPoint>> geoChain = schema.addChain();
        geoChain.add(createGeoLabellingFn(1));
        geoChain.add(createGeoLabellingFn(2));

        List<LabellingFn<ExampleDataPoint>> deviceTypeChain = schema.addChain();
        deviceTypeChain.add(createDeviceTypeLabellingFn());

        return schema;
    }

    private static LabellingFn<ExampleDataPoint> createGeoLabellingFn(final int depth) {
        return new LabellingFn<ExampleDataPoint>() {
            @Override
            public long label(ExampleDataPoint dataPoint) {
                return QuadTreeConverter.convert(dataPoint.getGeoX(), dataPoint.getGeoY(), 3, 3, depth);
            }
        };
    }

    private static LabellingFn<ExampleDataPoint> createDeviceTypeLabellingFn() {
        return new LabellingFn<ExampleDataPoint>() {
            @Override
            public long label(ExampleDataPoint dataPoint) {
                return dataPoint.getDeviceType().ordinal();
            }
        };
    }

    private static TimeLabellingFn<ExampleDataPoint> prepareTimeLabellingFn() {
        return new TimeLabellingFn<ExampleDataPoint>() {
            @Override
            public long label(ExampleDataPoint dataPoint) {
                return dataPoint.getTime();
            }
        };
    }
}
