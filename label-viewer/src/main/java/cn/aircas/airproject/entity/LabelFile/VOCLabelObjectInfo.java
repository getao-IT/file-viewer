package cn.aircas.airproject.entity.LabelFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: VOCLabelObjectInfo
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 15:07
 * @Version 1.0
 */


@Data
@Slf4j
public class VOCLabelObjectInfo implements LabelObject{

    @JSONField(serialize = false)
    private String fileName;

    @JSONField(serialize = false)
    private String coordinate = "PIXEL";

    @JSONField(name = "object")
    private List<VOCLabelObject> objects;

    public VOCLabelObjectInfo(VOCBridgedObjectInfo bridgedObjectInfo){

        this.fileName = bridgedObjectInfo.getFileName();

        objects = new ArrayList<>();

        int id = 0;
        for (VOCBridgedObjectInfo.LabelObject vocLabel : bridgedObjectInfo.getLabelObjects()) {
            VOCLabelObject vocLabelObject = new VOCLabelObject(vocLabel.getBandBox().getXmin(), vocLabel.getBandBox().getYmin(),
                    vocLabel.getBandBox().getXmax(), vocLabel.getBandBox().getYmax());

            List<VOCLabelObject.PossibleResult> possibleResultList = new ArrayList<>();
            VOCLabelObject.PossibleResult pr = new VOCLabelObject.PossibleResult();
            pr.setName("未知");
            pr.setProbability("1");
            possibleResultList.add(pr);
            vocLabelObject.setPossibleResultList(possibleResultList);

            vocLabelObject.setType("Polygon");
            vocLabelObject.setId(id++);
            objects.add(vocLabelObject);
        }

    }

    @Data
    public static class VOCLabelObject {

        public VOCLabelObject(double xmin, double ymin, double xmax, double ymax){
            points = new Points();
            points.setPoint(new ArrayList<>());
            points.getPoint().add(xmin + ", " + ymin );
            points.getPoint().add(xmin + ", " + ymax );
            points.getPoint().add(xmax + ", " + ymax );
            points.getPoint().add(xmax + ", " + ymin );
        }

        @JSONField(ordinal = 6)
        private Points points;

        @JSONField(ordinal = 5)
        private String type;
        @JSONField(ordinal = 3)
        private int id;
        @JSONField(ordinal = 1)
        private String coordinate;
        @JSONField(ordinal = 2)
        private String description;



        @XmlElement(name = "possibleresult")
        @JSONField(name = "possibleresult",ordinal = 4)
        private List<PossibleResult> possibleResultList;

        @Data
        public static class PossibleResult {
            private String className;
            private String name;
            private String probability;
        }

        @Data
        public static class Points{
            private List<String> point;
        }
    }


    @Override
    public boolean isEmpty() {
        return (null == objects || objects.isEmpty());
    }

    //默认从VOC中获取的都是像素
    @Override
    public String getCoordinate() {
        if(isEmpty())
            return null;
        return this.coordinate;
    }

    @Override
    public JSONObject toJSONObject() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }

    @Override
    public void addFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<Integer, double[][]> getPointMap() {
        Map<Integer, double[][]> pointMap = new HashMap<>();
        for (int objectIndex = 0; objectIndex < this.objects.size(); objectIndex++) {
            VOCLabelObject vocLabelObject = this.objects.get(objectIndex);
            List<String> objectPointList = vocLabelObject.getPoints().getPoint();
            if(null == objectPointList){
                continue;
            }

            double[][] objectPointArray = new double[objectPointList.size()][2];
            for (int index = 0; index < objectPointList.size(); index++) {
                String[] points = objectPointList.get(index).split(",");
                double xCoordinate = Double.parseDouble(points[0]);
                double yCoordinate = Double.parseDouble(points[1]);
                objectPointArray[index] = new double[]{xCoordinate,yCoordinate};
            }
            pointMap.put(objectIndex,objectPointArray);
        }
        return pointMap;
    }

    @Override
    public void updatePointList(Map<Integer, double[][]> labelPointMap, String coordinate) {
        this.coordinate = coordinate;

        for(Integer objectIndex : labelPointMap.keySet()) {
            VOCLabelObject vocLabelObject = this.objects.get(objectIndex);
            List<String> pointList = vocLabelObject.getPoints().getPoint();
            pointList.clear();
            for(int i = 0; i < labelPointMap.get(objectIndex).length; ++i){
                double[] point = labelPointMap.get(objectIndex)[i];
                pointList.add(point[0] + ", " + point[1]);
            }
        }
    }
}
