package cn.aircas.airproject;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.utils.ImageUtil;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: LabelParserTest
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/27 8:30
 * @Version 1.0
 */


@Ignore("单独测试通过，打包时jaxb报错，跳过测试")
@SpringBootTest(classes = {FileProcessApplication.class})
@RunWith(SpringRunner.class)
public class LabelParserTest {

    private static boolean canTest = true;

    private String XML_ARICAS_FILE = "00-WorkDir\\byte.xml";
    private String XML_VIF_FILE = "00-WorkDir\\test-vif.vif";
    private String XML_VOC_FILE = "00-WorkDir\\voc.xml";
    private String IMAGE_FILE = "00-WorkDir\\byte.tiff";

    @Autowired
    private LabelProjectService labelProjectService;

    @BeforeClass
    public static void CheckServerName() {
        String username = System.getProperty("user.name");
        if(username.equals("yzhan")){
            canTest = true;
        } else {
            canTest = false;
            System.out.println("跳过对应测试");
        }
        canTest = username.equals("yzhan")? true: false;
    }


    @Test
    public void parseXmlAIRCAS() throws Exception {

        if(false == canTest)
            return;

        String parseResult = labelProjectService.viewSelectedLabelFile(IMAGE_FILE, XML_ARICAS_FILE, LabelFileType.XML, LabelFileFormat.AIRCAS);
        System.out.println(parseResult);
    }

    @Test
    public void parseXMLVIF() throws Exception {
        if(false == canTest)
            return;

        System.out.println(labelProjectService);
        String parseResult = labelProjectService.viewSelectedLabelFile(IMAGE_FILE, XML_VIF_FILE, LabelFileType.XML, LabelFileFormat.VIF);
        System.out.println(parseResult);
    }

    @Test
    public void parseXMLVOC() throws Exception {
        if(false == canTest)
            return;

        String parseResult = labelProjectService.viewSelectedLabelFile(IMAGE_FILE, XML_VOC_FILE, LabelFileType.XML, LabelFileFormat.VOC);
        System.out.println(parseResult);
    }

    /**
     * 测试使用新的工具改造后的原始函数viewXmlFile的解析AIRCAS类型文件的功能
     * @throws Exception
     */
    @Test
    public void testViewXmlFileForARICAS() throws Exception {
        if(false == canTest)
            return;

        String parseResult = labelProjectService.viewXmlFile(IMAGE_FILE, LabelPointType.GEODEGREE, XML_ARICAS_FILE);
        System.out.println(parseResult);
    }

    /**
     * 测试使用新的工具改造后的原始函数viewXmlFile的解析VIF类型文件的功能
     * @throws Exception
     */
    @Test
    public void testViewXmlFileForVIF() throws Exception {
        String parseResult = labelProjectService.viewXmlFile(IMAGE_FILE, LabelPointType.GEODEGREE, XML_VIF_FILE);
        System.out.println(parseResult);
    }


}
