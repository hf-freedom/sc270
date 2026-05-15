package com.cs.controller;

import com.cs.dto.Result;
import com.cs.model.SystemStats;
import com.cs.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:3007")
public class StatsController {

    @Autowired
    private SessionService sessionService;

    @GetMapping
    public Result<SystemStats> getSystemStats() {
        return Result.success(sessionService.getSystemStats());
    }
}
