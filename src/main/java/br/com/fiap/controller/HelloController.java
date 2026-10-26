package br.com.fiap.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

//Anotations --anotações
@Controller
public class HelloController {

    @GetMapping
    public RedirectView home() {
        return new RedirectView("/times");
    }
}
