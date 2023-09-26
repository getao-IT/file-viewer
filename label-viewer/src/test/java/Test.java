import cn.aircas.airproject.FileProcessApplication;
import cn.aircas.airproject.controller.LabelProjectController;
import cn.aircas.airproject.entity.domain.SaveLabelRequest;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.service.LabelProjectService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileProcessApplication.class)
public class Test {

    @Autowired
    private LabelProjectService labelProjectService;

    @org.junit.Test
    public void test() throws IOException {
        SaveLabelRequest saveLabelRequest = new SaveLabelRequest();
        String sdf = "{\"object\":[{\"id\":35,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"1045639.49981334 , 48512.49999920051\",\"1045639.4998133401 , 48534.999999200154\",\"1045602.9998134142 , 48534.99999920051\",\"1045602.9998134141 , 48512.499999200874\"]}},{\"id\":38,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"1045547.9998135255 , 48480.499999201944\",\"1045547.999813525 , 48502.99999920157\",\"1045511.4998136 , 48502.99999920192\",\"1045511.4998135989 , 48480.4999992023\"]}},{\"id\":40,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"531887.0000000001 , 2777048.5\",\"531886.9999999993 , 2777616.5000000005\",\"533102.9999999998 , 2777616.499999999\",\"533103.0000000002 , 2777048.5\"]}}]}";
        saveLabelRequest.setLabel(sdf);
        saveLabelRequest.setSavePath("1.xml");
        saveLabelRequest.setImagePath("卡塔尔-乌代德空军基地_2017101418063800.tiff");
        saveLabelRequest.setLabelPointType(LabelPointType.GEODEGREE);
        labelProjectService.saveLabel(saveLabelRequest);

    }

}
