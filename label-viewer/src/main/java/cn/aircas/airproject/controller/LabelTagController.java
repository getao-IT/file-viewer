package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.entity.domain.LabelTagDatabaseInfo;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import cn.aircas.airproject.entity.dto.LabelTagDto;
import cn.aircas.airproject.service.impl.LabelTagChildrenServiceImpl;
import cn.aircas.airproject.service.impl.LabelTagParentServiceImpl;
import cn.aircas.airproject.utils.HttpUtils;
import cn.aircas.airproject.utils.SQLiteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.List;



/**
 * 标签管理控制器
 */
@RestController
@RequestMapping(value = "/labelTag")
public class LabelTagController {

    @Value(value = "${database.driverPath}")
    private String driverPath;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LabelTagChildrenServiceImpl childrenService;

    @Autowired
    private LabelTagParentServiceImpl parentService;


    @Log("获取标签库列表")
    @GetMapping("/listLabelTagDatabase")
    public CommonResult<List<LabelTagDatabaseInfo>> listLabelTagDatabase() {
        List<LabelTagDatabaseInfo> result = SQLiteUtils.listLabelTagDatabase(request);
        return new CommonResult<List<LabelTagDatabaseInfo>>().success().data(result).message("获取标签库列表并连接成功");
    }


    @Log("获取标签库连接")
    @GetMapping("/getConnect")
    public CommonResult<String> getConnect() {
        String ip = HttpUtils.getClientIp(request);
        String dbPath = driverPath + "/" + ip + ".db";
        SQLiteUtils.getSQLiteConnection(dbPath);
        return new CommonResult<String>().success().data(ip).message("获取标签库连接成功");
    }


    @Log("获取标签库信息")
    @GetMapping("/listLabelTag")
    public CommonResult<List<LabelTagDto>> listLabelTag() {
        List<LabelTagDto> result = parentService.listLabelTag();
        return new CommonResult<List<LabelTagDto>>().success().data(result).message("获取标签库成功");
    }


    /**
     * { "tag_name": "舰船" }
     */
    @Log("增加一级标签")
    @PostMapping("/addLabelTagParent")
    public CommonResult<Boolean> addLParentTag(@RequestBody LabelTagParent tagParent) {
        int insert = parentService.insert(tagParent);
        if (insert != -1) {
            return new CommonResult<Boolean>().success().message("增加一级标签成功");
        }
        return new CommonResult<Boolean>().success().message("增加一级标签失败");
    }


    /**
     * { "parent_id": 1, "tag_name": "巡洋舰", "parenttag_name": "舰船", "properties_name":"巡洋舰" , "properties_color": "rgb(255,255,255)" }
     */
    @Log("增加二级标签")
    @PostMapping("/addLabelTagChildren")
    public CommonResult<Boolean> addChildrenTag(@RequestBody LabelTagChildren tagChildren) {
        int insert = childrenService.insert(tagChildren);
        if (insert != -1) {
            return new CommonResult<Boolean>().success().message("增加二级标签成功");
        }
        return new CommonResult<Boolean>().success().message("增加二级标签失败");
    }


    @Log("更新一级标签")
    @PutMapping("/updateLabelTagParent")
    public CommonResult<Boolean> updateLabelTagParent(@RequestBody LabelTagParent tagParent) {
        boolean insert = parentService.updateById(tagParent);
        if (insert) {
            return new CommonResult<Boolean>().success().message("更新一级标签成功");
        }
        return new CommonResult<Boolean>().success().message("更新一级标签失败");
    }


    @Log("更新二级标签")
    @PutMapping("/updateLabelTagChildren")
    public CommonResult<Boolean> updateChildrenTag(@RequestBody LabelTagChildren tagChildren) {
        boolean insert = childrenService.updateById(tagChildren);
        if (insert) {
            return new CommonResult<Boolean>().success().message("更新二级标签成功");
        }
        return new CommonResult<Boolean>().success().message("更新二级标签失败");
    }


    @Log("删除一级标签")
    @DeleteMapping("/deleteLabelTagParent")
    public CommonResult<Boolean> deleteLabelTagParent(int id) {
        boolean insert = parentService.deleteById(id);
        if (insert) {
            return new CommonResult<Boolean>().success().message("删除一级标签成功");
        }
        return new CommonResult<Boolean>().success().message("删除一级标签失败");
    }


    @Log("删除二级标签")
    @DeleteMapping("/deleteLabelTagChildren")
    public CommonResult<Boolean> deleteChildrenTag(int id) {
        boolean insert = childrenService.deleteById(id);
        if (insert) {
            return new CommonResult<Boolean>().success().message("删除二级标签成功");
        }
        return new CommonResult<Boolean>().success().message("删除二级标签失败");
    }


    @Log("执行SQL")
    @PostMapping("/executeSql")
    public CommonResult<Boolean> executeSql(String sql) {
        boolean create = parentService.executeSql(sql);
        if (create) {
            return new CommonResult<Boolean>().success().message("执行SQL成功");
        }
        return new CommonResult<Boolean>().success().message("执行SQL失败");
    }


    @Log("导入标签库")
    @PostMapping("/importLabelTag")
    public CommonResult<Boolean> importLabelTag(MultipartFile file) {
        boolean result = parentService.importLabelTag(file);
        if (result) {
            return new CommonResult<Boolean>().success().message("导入标签库成功");
        }
        return new CommonResult<Boolean>().success().message("导入标签库失败");
    }


    @Log("导出标签库")
    @GetMapping("/exportLabelTag")
    public CommonResult<String> exportLabelTag() {
        String clientIp = HttpUtils.getClientIp(request);
        String result = parentService.exportLabelTag(clientIp);
        return new CommonResult<String>().success().data(result).message("导出标签库成功");
    }

}
