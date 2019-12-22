package com.spotonresponse.adapter.controller.unpw;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPA_Controller {
    @GetMapping("/user/")
    String getUserPage(){
        return "redirect:/user/index.html";
    }

    @GetMapping("/admin/")
    String getAdminPage(){
        return "redirect:/admin/index.html";
    }
}
