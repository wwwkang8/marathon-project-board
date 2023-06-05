package com.marathon.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * 사용 목적 : / 로 api 호출되었을 때 index.html로 리다이렉트 해주는 함수.
     * */
    @GetMapping("/")
    public String root(){
        return "forward:/articles";
    }
}
