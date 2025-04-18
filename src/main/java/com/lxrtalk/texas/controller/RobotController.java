package com.lxrtalk.texas.controller;

import com.lxrtalk.texas.texas.robot.RobotOperationsUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotController {
    @RequestMapping("/robot/init")
    public String init() {
        RobotOperationsUtil.init();
        return "ok";
    }

}
