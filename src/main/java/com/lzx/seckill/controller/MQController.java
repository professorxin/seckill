package com.lzx.seckill.controller;

import com.lzx.seckill.rabbitmq.MQSender;
import com.lzx.seckill.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/mq")
public class MQController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/hello")
    @ResponseBody
    public Result hello() {
        mqSender.send("Hello World");
        return Result.success(true);
    }

    @RequestMapping("/topic")
    @ResponseBody
    public Result topic() {
        mqSender.sendTopic("Hello World");
        return Result.success(true);
    }

    @RequestMapping("/fanout")
    @ResponseBody
    public Result fanout() {
        mqSender.sendFanout("Hello World");
        return Result.success(true);
    }


    @RequestMapping("/header")
    @ResponseBody
    public Result header() {
        mqSender.sendHeaders("Hello World");
        return Result.success(true);
    }
}
