package cn.aircas.airproject.entity.LabelFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
@XmlRootElement(name = "判读标注")
@XmlAccessorType(XmlAccessType.FIELD)
public class VifLabelOjectInfo implements LabelObject {

    @JSONField(serialize = false)
    @XmlAttribute
    private String imageSource;

    @JSONField(name = "object")
    @XmlElement(name = "Child")
    @XmlElementWrapper(name = "Children")
    private List<VifLabelObject> vifLabelObjectList;


    @Data
    @XmlType(name = "Child")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class VifLabelObject{


        @JSONField(name = "points")
        @XmlElement(name = "GeoShapePoint")
        @XmlElementWrapper(name = "GeoShape")
        private List<GeoShapePoint> geoShapePointList = new ArrayList<>();


        public void setGeoShapePointList(String points) {
            List<String> pointList = JSONObject.parseObject(points).getJSONArray("point").stream().map(Object::toString).collect(Collectors.toList());
            for (String point : pointList){
                GeoShapePoint geoShapePoint = new GeoShapePoint();
                geoShapePoint.setX(point.split(",")[0].trim());
                geoShapePoint.setY(point.split(",")[1].trim());
                geoShapePointList.add(geoShapePoint);
            }
        }

        @Data
        @XmlType(name = "GeoShapePoint",propOrder = {"x","y"})
        @XmlAccessorType(XmlAccessType.FIELD)
        public static class GeoShapePoint{
            @XmlAttribute
            private String x;
            @XmlAttribute
            private String y;
        }

        @JSONField(name = "possibleresult")
        @XmlTransient
        private List<PossibleResult> possibleResultList;

        @XmlTransient
        @Data
        public static class PossibleResult {
            private String name;
            private String probability;
        }

    }

    public void setVifLabelObjectList(Object jsonArray) {
        this.vifLabelObjectList = JSONArray.parseArray(jsonArray.toString(),VifLabelObject.class);
    }

    @Override
    public boolean isEmpty() {
        return vifLabelObjectList.isEmpty();
    }

    @Override
    public String getCoordinate() {
        if (vifLabelObjectList.isEmpty())
            return null;
        return "geodegree";
    }

    @Override
    public JSONObject toJSONObject() {
        return vifJsonToLabelInfo();
    }

    @Override
    public void addFileName(String fileName) {
        this.imageSource = fileName;
    }

    @Override
    public Map<Integer, double[][]> getPointMap() {
        Map<Integer,double[][]> pointMap = new HashMap<>();
        for (int objectIndex = 0; objectIndex < this.vifLabelObjectList.size(); objectIndex++) {
            VifLabelObject vifLabelObject = this.vifLabelObjectList.get(objectIndex);
            double[][] objectPointArray = new double[vifLabelObject.getGeoShapePointList().size()][2];
            for (int index = 0; index < vifLabelObject.geoShapePointList.size(); index++) {
                VifLabelObject.GeoShapePoint geoShapePoint = vifLabelObject.getGeoShapePointList().get(index);
                double xCoordinate = Double.parseDouble(geoShapePoint.getX());
                double yCoordinate = Double.parseDouble(geoShapePoint.getY());
                objectPointArray[index] = new double[]{xCoordinate,yCoordinate};
            }
            pointMap.put(objectIndex,objectPointArray);
        }
        return pointMap;
    }

    /**
     * 更新坐标点列表
     * @param labelPointMap
     */
    @Override
    public void updatePointList(Map<Integer, double[][]> labelPointMap, String coordinate) {
        for (Integer objectIndex : labelPointMap.keySet()) {
            List<VifLabelObject.GeoShapePoint> pointList = this.vifLabelObjectList.get(objectIndex).geoShapePointList;
            pointList.clear();
            for (int i = 0; i < labelPointMap.get(objectIndex).length; i++) {
                double[] point = labelPointMap.get(objectIndex)[i];
                VifLabelObject.GeoShapePoint geoShapePoint = new VifLabelObject.GeoShapePoint();
                geoShapePoint.setX(String.valueOf(point[0]));
                geoShapePoint.setY(String.valueOf(point[1]));
                pointList.add(geoShapePoint);
            }
        }
    }



    /**
     * xmlJson转vifJson
     */
    public static JSONObject xmlJsonToVifJson(String labelInfo){
        JSONObject jsonObject = JSONObject.parseObject(labelInfo);
        JSONArray jsonArray = jsonObject.getJSONArray("object");
        List<JSONObject> objects = new ArrayList<>();
        for (int index = 0 ; index < jsonArray.size() ; index ++){
            JSONObject perObject = jsonArray.getJSONObject(index);
            JSONObject pointObjects = perObject.getJSONObject("points");
            JSONArray points = pointObjects.getJSONArray("point");
            List<JSONObject> pointList = new ArrayList<>();
            for (Object point : points){
                List<String> coorpoint = Arrays.asList(point.toString().split(","));
                JSONObject pointObject = new JSONObject();
                pointObject.put("x",coorpoint.get(0));
                pointObject.put("y",coorpoint.get(1));
                pointList.add(pointObject);
            }
            pointObjects.put("GeoShapePoint",pointList);
            pointObjects.remove("point");
            perObject.put("GeoShape",pointObjects);
            perObject.remove("points");
            objects.add(perObject);
        }
        jsonObject.put("Child",objects);
        jsonObject.remove("object");

        JSONObject childrenObject = new JSONObject();
        JSONObject headObject = new JSONObject();
        childrenObject.put("Childs",jsonObject);
        headObject.put("判读标注",childrenObject);
        return headObject;
    }

    public JSONObject vifJsonToLabelInfo(){
        JSONObject object = new JSONObject();
        JSONArray ppe = new JSONArray();
        for(int i = 0; i < vifLabelObjectList.size(); ++i){
            JSONObject pe = new JSONObject();
            JSONObject e = new JSONObject();
            List<String> point = new ArrayList<>();
            for (VifLabelObject.GeoShapePoint geoShapePoint : vifLabelObjectList.get(i).getGeoShapePointList()) {
                double xCoordinate = Double.parseDouble(geoShapePoint.getX());
                double yCoordinate = Double.parseDouble(geoShapePoint.getY());
                point.add(xCoordinate + "," + yCoordinate);
            }
            e.put("point", point);
            pe.put("points", e);
            pe.put("id", i);
            pe.put("coordinate", "geodegree");
            pe.put("description", "经纬度坐标");
            List<JSONObject> possibleresult = new ArrayList<>();
            JSONObject poss = new JSONObject();
            poss.put("probability", 1);
            poss.put("name", "未知");
            possibleresult.add(poss);
            pe.put("possibleresult", possibleresult);
            pe.put("type", "Polygon");
            ppe.add(pe);
        }
        object.put("object", ppe);
        return object;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sdf", "sdf");
        System.out.println("sdf");
        String text = "{\n" +
                "    \"object\": [\n" +
                "        {\n" +
                "            \"id\": 4538,\n" +
                "            \"type\": \"Rectangle\",\n" +
                "            \"coordinate\": \"geodegree\",\n" +
                "            \"description\": \"经纬度坐标\",\n" +
                "            \"possibleresult\": [\n" +
                "                {\n" +
                "                    \"name\": \"test1\",\n" +
                "                    \"probability\": \"1\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"points\": {\n" +
                "                \"point\": [\n" +
                "                    \"54.44886908898144 , 24.431483875624146\",\n" +
                "                    \"54.44886908898144 , 24.431699494325915\",\n" +
                "                    \"54.44922420901173 , 24.431699494325915\",\n" +
                "                    \"54.44922420901173 , 24.431483875624146\"\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 4541,\n" +
                "            \"type\": \"Rectangle\",\n" +
                "            \"coordinate\": \"geodegree\",\n" +
                "            \"description\": \"经纬度坐标\",\n" +
                "            \"possibleresult\": [\n" +
                "                {\n" +
                "                    \"name\": \"test1\",\n" +
                "                    \"probability\": \"1\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"points\": {\n" +
                "                \"point\": [\n" +
                "                    \"54.449125568407844 , 24.431771365290228\",\n" +
                "                    \"54.44912556455888 , 24.43204089060372\",\n" +
                "                    \"54.44949054903446 , 24.43204089060372\",\n" +
                "                    \"54.44948655060671 , 24.431771365290228\"\n" +
                "                ]\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 4543,\n" +
                "            \"type\": \"Rectangle\",\n" +
                "            \"coordinate\": \"geodegree\",\n" +
                "            \"description\": \"经纬度坐标\",\n" +
                "            \"possibleresult\": [\n" +
                "                {\n" +
                "                    \"name\": \"test1\",\n" +
                "                    \"probability\": \"1\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"points\": {\n" +
                "                \"point\": [\n" +
                "                    \"54.44992458462703 , 24.430962797094868\",\n" +
                "                    \"54.44992458462703 , 24.431358098048115\",\n" +
                "                    \"54.45041780689132 , 24.431358098048115\",\n" +
                "                    \"54.45041780689132 , 24.430962797094868\"\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject jsonObject2 = JSONObject.parseObject(null);
        LabelObject labelObjecttest = JSONObject.toJavaObject(jsonObject2,VifLabelOjectInfo.class);
        LabelObject labelObject = JSONObject.toJavaObject(JSONObject.parseObject(text),VifLabelOjectInfo.class);
        System.out.println("sdf");


    }

}
