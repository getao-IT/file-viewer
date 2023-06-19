package cn.aircas.airproject;

import cn.aircas.airproject.utils.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class TestFun {

    @Autowired
    private FileUtils fileUtils;

    @Test
    public void test1() {
        //String path = "/var/nfs/general/data/airpipeline/base/external/120/template/12/code/";
        String path = "/var/nfs/general/data/AirPAI/AirPAI_Data/componentsD/";
        boolean allowAccess = fileUtils.isAllowAccess(path,"1");
        System.out.println(allowAccess);
    }
}
