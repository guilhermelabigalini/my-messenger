/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author guilherme
 */
@Controller
public class HomeController {
    
    @RequestMapping("/")
    public ModelAndView index() {
        
        return new ModelAndView("index");
    }
    
    @RequestMapping("/testsocket")
    public String test() {
        
        return "testsocket";
    }
}
