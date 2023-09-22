package com.yvan.williwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("api/work")
public class WillIWorkController {
    @Autowired
    WillIWorkService willIWorkService;

    Logger logger = Logger.getLogger(WillIWorkController.class.getName());

    public WillIWorkController() {
        // constructor is empty
    }

    //Create a controller method with zone and a number of days as parameters. It returns a true or false weather it fall in a work day or not
    @GetMapping("/zone")
    public boolean willIWork(String zone, int days) {
        logger.info("Zone: " + zone + " Days: " + days);
        final boolean isWorkingDay = willIWorkService.willIWork(zone, days);
        logger.info("isWorkingDay" + isWorkingDay);
        return isWorkingDay;
    }
}
