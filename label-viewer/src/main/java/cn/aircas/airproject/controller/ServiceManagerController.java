package cn.aircas.airproject.controller;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.service.impl.ConnectServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/receive")
public class ServiceManagerController {


    @Autowired
    private ConnectServiceManager serviceManager;

    @GetMapping(value = "/service")
    public CommonResult<String> getService(int service_id) {
        CommonResult<String> service = serviceManager.getService(service_id);
        return service;
    }
}
