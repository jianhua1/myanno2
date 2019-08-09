package controller;

import anno.Controller;
import anno.RequestMapping;
import view.View;

@Controller
public class MyController {
    @RequestMapping(value = "ab")
    public View a(){
        System.out.println("xxxxxxxxxxxxxx");
        return new View("index2.jsp","redirect");
    }

    @RequestMapping(value = "cc")
    public View b(){
        System.out.println("ccccccccccc");
        return new View("index2.jsp");
    }

    @RequestMapping(value = "dd")
    public View c(){
        System.out.println("ddddddddddd");
        return new View("index2.jsp");
    }
}
