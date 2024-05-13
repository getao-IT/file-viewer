package cn.aircas.airproject;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.utils.ImageUtil;
import org.junit.Test;

/**
 用于测试系统中使用GDAL工具进行操作的工具函数
 */


public class GDALToolFuncTest {

    private static String bytePath = "D:\\00-WorkDir\\byte.tiff";
    private static String f32Path = "D:\\00-WorkDir\\float32.tif";
    private static String f64Path = "D:\\00-WorkDir\\float64.tif";

    /**
     *
     */
    public void formatConverterTest(String inputPath, String gdalExtension, String info){
        String username = System.getProperty("user.name");
        if(username.equals("yzhan")){
            String dstPath = "D:\\00-WorkDir";
            ImageUtil.formatConvertor(inputPath, dstPath, gdalExtension, new ProgressContr());
            System.out.println(info);
        }else {
            System.out.println("跳过当前测试");
        }
    }

    @Test
    public void testByte2Jpg(){
        formatConverterTest(bytePath, "JPEG","byte to jpg OK");
    }

    @Test
    public void testf32ToJpg(){
        formatConverterTest(f32Path, "JPEG","float32 to jpg OK");
    }

    @Test
    public void testf64ToJpg(){
        formatConverterTest(f64Path, "JPEG","float64 to jpg OK");
    }

    @Test
    public void testByte2Png(){
        formatConverterTest(bytePath, "PNG","byte to png OK");
    }

    @Test
    public void testf32ToPng(){
        formatConverterTest(f32Path, "PNG","f32 to png OK");
    }

    @Test
    public void testf64ToPng(){
        formatConverterTest(f64Path, "PNG","f64 to png OK");
    }
}
