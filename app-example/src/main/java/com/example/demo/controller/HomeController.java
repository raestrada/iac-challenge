package com.example.demo.controller;

import com.example.demo.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final PersonService personService;
    
    @Value("${spring.application.name:Demo Application}")
    private String applicationName;
    
    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    @GetMapping
    @ResponseBody
    public String home() {
        long personCount = personService.countPersons();
        
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>" + applicationName + "</title>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 800px; margin: 0 auto; padding: 20px; }" +
                "        h1 { color: #333; border-bottom: 1px solid #eee; padding-bottom: 10px; }" +
                "        .card { border: 1px solid #ddd; border-radius: 8px; padding: 20px; margin-bottom: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }" +
                "        .info { display: flex; justify-content: space-between; }" +
                "        .count { font-size: 48px; font-weight: bold; color: #4CAF50; text-align: center; }" +
                "        .label { color: #666; font-size: 14px; text-align: center; }" +
                "        .version { color: #888; font-size: 12px; margin-top: 30px; text-align: right; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <h1>" + applicationName + "</h1>" +
                "    <div class=\"card\">" +
                "        <p>This application provides API endpoints to manage person information.</p>" +
                "        <p>Use <code>POST /person</code> to create a new person record.</p>" +
                "        <p>Use <code>GET /person/{id}</code> to retrieve a specific person's details.</p>" +
                "    </div>" +
                "    <div class=\"card\">" +
                "        <div class=\"info\">" +
                "            <div>" +
                "                <div class=\"count\">" + personCount + "</div>" +
                "                <div class=\"label\">Total Persons</div>" +
                "            </div>" +
                "        </div>" +
                "    </div>" +
                "    <div class=\"version\">Version: " + applicationVersion + "</div>" +
                "</body>" +
                "</html>";
    }
}
