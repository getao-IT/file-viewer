package cn.aircas.airproject.entity.LabelFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "annotation")
public class XMLLabelObjectInfo_copy implements LabelObject {

    @JSONField(serialize = false)
    private Source source = new Source();

    @JSONField(name = "object")
    @XmlElement(name = "object")
    @XmlElementWrapper(name = "objects")
    private List<XMLLabelObject> XMLLabelObjectList;

    @Data
    public static class Source {
        private int id;
        private String fileName;
        private String original;
    }

    @Data
    @XmlType(name = "object",propOrder = {"id","type","coordinate","description","possibleResultList","points","coc","ver","radius"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class XMLLabelObject {
        @JSONField(ordinal = 0)
        private int id;
        @JSONField(ordinal = 1)
        private String type;
        @JSONField(ordinal = 5)
        private Points points;
        @JSONField(ordinal = 2)
        private String coordinate;
        @JSONField(ordinal = 3)
        private String description;
        @JSONField(name = "cocLatLng", ordinal = 6)
        @XmlElement(name = "cocLatLng")
        private Coc coc;
        @JSONField(name = "vertexLatLng", ordinal = 7)
        @XmlElement(name = "vertexLatLng")
        private Ver ver;
        @JSONField(ordinal = 8)
        private double radius;

        @XmlElement(name = "possibleresult")
        @JSONField(name = "possibleresult",ordinal = 4)
        private List<PossibleResult> possibleResultList;

        @Data
        @Builder
        @AllArgsConstructor
        public static class Coc {
            private Object lat;
            private Object lng;
        }

        @Data
        @Builder
        @AllArgsConstructor
        public static class Ver {
            private Object lat;
            private Object lng;
        }

        @Data
        public static class PossibleResult {
            private String name;
            private String probability;
        }

        @Data
        public static class Points{
            private List<String> point;
        }
    }


    /**
     * 标注信息是否为空
     * @return
     */
    @Override
    public boolean isEmpty() {
        return XMLLabelObjectList.isEmpty();
    }

    /**
     * 获取坐标系类型
     * @return
     */
    @Override
    public String getCoordinate() {
        if (XMLLabelObjectList.isEmpty())
            return null;
        return XMLLabelObjectList.get(0).getCoordinate();
    }

    @Override
    public JSONObject toJSONObject() {
        return JSONObject.parseObject(JSONObject.toJSONString(this));
    }

    @Override
    public void addFileName(String fileName) {
        this.source.setFileName(fileName);
    }

    /**
     * 获取坐标点列表
     * @return
     */
    @Override
    public Map<Integer,double[][]> getPointMap() {
        Map<Integer,double[][]> pointMap = new HashMap<>();
        for (int objectIndex = 0; objectIndex < this.XMLLabelObjectList.size(); objectIndex++) {
            XMLLabelObject xmlLabelObject = this.XMLLabelObjectList.get(objectIndex);

            // 如果是圆，则构建圆信息
            if (xmlLabelObject.getType().equalsIgnoreCase("CustomCircle")) {
                double[][] objectPointArray = new double[2][2];
                XMLLabelObject.Coc coc = xmlLabelObject.getCoc();
                XMLLabelObject.Ver ver = xmlLabelObject.getVer();
                objectPointArray[0] = new double[]{Double.parseDouble(coc.getLat().toString()), Double.parseDouble(coc.getLng().toString())};
                objectPointArray[1] = new double[]{Double.parseDouble(ver.getLat().toString()), Double.parseDouble(ver.getLng().toString())};
                pointMap.put(objectIndex, objectPointArray);
            } else {
                List<String> objectPointList = xmlLabelObject.getPoints().getPoint();
                if (objectPointList == null)
                    continue;
                double[][] objectPointArray = new double[objectPointList.size()][2];

                for (int index = 0; index < objectPointList.size(); index++) {
                    String[] points = objectPointList.get(index).split(",");
                    double xCoordinate = Double.parseDouble(points[0]);
                    double yCoordinate = Double.parseDouble(points[1]);
                    objectPointArray[index] = new double[]{xCoordinate,yCoordinate};
                }
                pointMap.put(objectIndex,objectPointArray);
            }

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
            XMLLabelObject xmlLabelObject = this.XMLLabelObjectList.get(objectIndex);
            xmlLabelObject.setCoordinate(coordinate.toLowerCase());

            if (xmlLabelObject.getType().equalsIgnoreCase("CustomCircle")) {
                double[] cocLngLat = labelPointMap.get(objectIndex)[0];
                double[] verTexLngLat = labelPointMap.get(objectIndex)[1];
                xmlLabelObject.setCoc(XMLLabelObject.Coc.builder().lat(cocLngLat[0]).lng(cocLngLat[1]).build());
                xmlLabelObject.setVer(XMLLabelObject.Ver.builder().lat(verTexLngLat[0]).lng(verTexLngLat[1]).build());
            } else {
                List<String> pointList = xmlLabelObject.getPoints().getPoint();
                pointList.clear();
                for (int i = 0; i < labelPointMap.get(objectIndex).length; i++) {
                    double[] point = labelPointMap.get(objectIndex)[i];
                    pointList.add(point[0]+","+point[1]);
                }
            }

        }
    }


    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sdf","sdf");
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
        LabelObject labelObject = JSONObject.parseObject(text, XMLLabelObjectInfo_copy.class);


    }
}
