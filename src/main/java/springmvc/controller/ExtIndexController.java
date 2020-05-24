package springmvc.controller;


import springmvc.annotation.ExtController;
import springmvc.annotation.ExtRequestMapping;

/**
 * @description: ExtController
 * @author: Codegitz
 * @create: 2020-05-22 23:48
 **/
@ExtController
@ExtRequestMapping("/ext")
public class ExtIndexController {

    @ExtRequestMapping("/test")
    public String test(){
        System.out.println("Implement a springmvc framework manually");
        return "test";
    }

    @ExtRequestMapping("/index")
    public String index(){
        System.out.println("Implement a springmvc framework manually");
        return "index";
    }
}
