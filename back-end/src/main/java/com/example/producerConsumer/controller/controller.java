package com.example.producerConsumer.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "pc")

public class controller {
    services Services;
    @Autowired
    public controller(services Services){
        this.Services = Services;
    }
    @PostMapping
    public boolean networkAndRun(@RequestBody Map<String, Object> requestBody) {
        Map<String, ArrayList<String>> myNetwork = (Map<String, ArrayList<String>>) requestBody.get("myNetwork");
        return Services.constructNetworkAndRun(myNetwork);
    }


    @GetMapping
    public Map<String, Object> polling(){
        return Services.polling();
    }

    @PutMapping
    public boolean stop(){
        return Services.stop();
    }
    @PutMapping("/inputStop")
    public boolean inputStop(){
        return Services.inputStop();
    }

    @DeleteMapping
    public boolean clear(){
        return Services.clear();
    }

}
