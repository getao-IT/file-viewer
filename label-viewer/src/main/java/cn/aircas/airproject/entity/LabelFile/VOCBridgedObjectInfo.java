package cn.aircas.airproject.entity.LabelFile;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 从VOC格式的xml文件中解析出的对象，该对象作为一个桥接对象，过渡到本项目一开始定义的能够被前端识别的LabelFile.XMLLabelObjectInfo.XMLLabelObject类
 */


@Data
@XmlRootElement(name = "annotation")
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = {"folder", "fileName", "path", "source", "size", "segmented", "labelObjects"})
public class VOCBridgedObjectInfo {

    @JSONField(ordinal = 1)
    private String folder;
    @JSONField(ordinal = 2)
    @XmlElement(name = "filename")
    private String fileName;
    @JSONField(ordinal = 3)
    private String path;
    @JSONField(ordinal = 4)
    private ImageSource source;
    @JSONField(ordinal = 5)
    private ImageSize size;
    @JSONField(ordinal = 6)
    private Integer segmented;

    @JSONField(ordinal = 7)
    @XmlElement(name = "object")
    private List<LabelObject> labelObjects;


    @Data
    @XmlAccessorType(value = XmlAccessType.FIELD)
    public static class ImageSource {
        private String database;
    }

    @Data
    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(propOrder = {"width", "height", "depth"})
    public static class ImageSize {
        @JSONField(ordinal = 1)
        private Integer width;
        @JSONField(ordinal = 2)
        private Integer height;
        @JSONField(ordinal = 3)
        private Integer depth;
    }

    @Data
    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(propOrder = {"name", "pose", "truncated", "difficult", "bandBox", "parts"})
    public static class LabelObject {
        @JSONField(ordinal = 1)
        private String name;
        @JSONField(ordinal = 2)
        private String pose;
        @JSONField(ordinal = 3)
        private Integer truncated;
        @JSONField(ordinal = 4)
        private Integer difficult;
        @JSONField(ordinal = 5)
        @XmlElement(name = "bndbox")
        private LabelBandBox bandBox;

        @JSONField(ordinal = 6)
        @XmlElement(name = "part")
        private List<LabelObjectPart> parts;

    }

    @Data
    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(propOrder = {"name", "bandBox"})
    public static class LabelObjectPart {
        @JSONField(ordinal = 1)
        private String name;
        @JSONField(ordinal = 2)
        @XmlElement(name = "bndbox")
        private LabelBandBox bandBox;
    }

    @Data
    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(propOrder = {"xmin", "ymin", "xmax", "ymax"})
    public static class LabelBandBox {
        @JSONField(ordinal = 1)
        private Integer xmin;
        @JSONField(ordinal = 2)
        private Integer ymin;
        @JSONField(ordinal = 3)
        private Integer xmax;
        @JSONField(ordinal = 4)
        private Integer ymax;
    }
}
