package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import cn.aircas.airproject.entity.dto.LabelTagDto;
import cn.aircas.airproject.service.impl.LabelTagChildrenServiceImpl;
import cn.aircas.airproject.service.impl.LabelTagParentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;



/**
 * 标签管理控制器
 */
@RestController
@RequestMapping(value = "/labelTag")
public class LabelTagController {

    @Autowired
    private LabelTagChildrenServiceImpl childrenService;

    @Autowired
    private LabelTagParentServiceImpl parentService;


    @Log("获取标签库信息")
    @GetMapping("/listLabelTag")
    public CommonResult<List<LabelTagDto>> listLabelTag() {
        List<LabelTagDto> result = parentService.listLabelTag();
        return new CommonResult<List<LabelTagDto>>().success().data(result).message("获取标签库成功");
    }


    @Log("增加一级标签")
    @PostMapping("/addLabelTagParent")
    public CommonResult<Boolean> addLParentTag(@RequestBody LabelTagParent tagParent) {
        boolean insert = parentService.insert(tagParent);
        if (insert) {
            return new CommonResult<Boolean>().success().message("增加一级标签成功");
        }
        return new CommonResult<Boolean>().success().message("增加一级标签失败");
    }


    @Log("创建数据库表")
    @PostMapping("/createTable")
    public CommonResult<Boolean> createTable(String sql) {
        boolean create = parentService.createTable(sql);
        if (create) {
            return new CommonResult<Boolean>().success().message("创建数据库表成功");
        }
        return new CommonResult<Boolean>().success().message("创建数据库表失败");
    }

}
