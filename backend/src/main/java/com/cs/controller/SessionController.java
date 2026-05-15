package com.cs.controller;

import com.cs.dto.CreateSessionRequest;
import com.cs.dto.Result;
import com.cs.model.*;
import com.cs.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@CrossOrigin(origins = "http://localhost:3007")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping("/create")
    public Result<Session> createSession(@RequestBody CreateSessionRequest request) {
        User user = new User();
        user.setUserId(request.getUserId());
        user.setUserName(request.getUserName());
        user.setVip(request.isVip());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        Session session = sessionService.createSession(
            user, 
            request.getProblemType(), 
            request.getProblemDesc()
        );
        return Result.success(session);
    }

    @PostMapping("/response/{sessionId}")
    public Result<Void> agentResponse(@PathVariable String sessionId) {
        sessionService.agentResponse(sessionId);
        return Result.success();
    }

    @PostMapping("/close/{sessionId}")
    public Result<Void> closeSession(@PathVariable String sessionId) {
        sessionService.closeSession(sessionId);
        return Result.success();
    }

    @GetMapping("/all")
    public Result<List<Session>> getAllSessions() {
        return Result.success(sessionService.getAllSessions());
    }

    @GetMapping("/queue")
    public Result<List<Session>> getQueueSessions() {
        return Result.success(sessionService.getQueueSessions());
    }

    @GetMapping("/quality-check")
    public Result<List<Session>> getQualityCheckSessions() {
        return Result.success(sessionService.getQualityCheckSessions());
    }
}
