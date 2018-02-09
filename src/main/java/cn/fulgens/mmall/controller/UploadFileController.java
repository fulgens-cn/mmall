package cn.fulgens.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UploadFileController {

    @RequestMapping("/toUpload")
    public String toUpload() {
        return "uploadFile";
    }

}
