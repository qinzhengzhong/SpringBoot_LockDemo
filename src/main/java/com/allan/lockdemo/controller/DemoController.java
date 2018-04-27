package com.allan.lockdemo.controller;

import com.allan.lockdemo.mapper.TlockMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 分布式锁demo
 */
@Controller
public class DemoController {

    @Resource
    TlockMapper tlockMapper;


    @RequestMapping("/demo")
    @ResponseBody
    public String demo() {
        return "hello word!";
    }

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(Integer id) {
       int temp= tlockMapper.insert(id);
       if (temp>0){
           return "insert succcess the id is:"+id;

       }else {
           return "insert error";
       }
    }


}
