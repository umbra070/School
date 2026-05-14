package org.skyschool.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {
    @Value("${server.port}")
    private int port;

    //GET http://localhost:port(8080 default)/info/port
    @GetMapping("/port")
    public ResponseEntity<Integer> getPort(){
        return ResponseEntity.ok(port);
    }
}
