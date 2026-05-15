package com.cs.controller;

import com.cs.dto.Result;
import com.cs.enums.AgentStatus;
import com.cs.model.Agent;
import com.cs.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "http://localhost:3007")
public class AgentController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/all")
    public Result<List<Agent>> getAllAgents() {
        return Result.success(sessionService.getAllAgents());
    }

    @PostMapping("/heartbeat/{agentId}")
    public Result<Void> heartbeat(@PathVariable String agentId) {
        sessionService.updateAgentHeartbeat(agentId);
        return Result.success();
    }

    @PostMapping("/online/{agentId}")
    public Result<Void> setOnline(@PathVariable String agentId) {
        sessionService.setAgentStatus(agentId, AgentStatus.ONLINE);
        return Result.success();
    }

    @PostMapping("/offline/{agentId}")
    public Result<Void> setOffline(@PathVariable String agentId) {
        sessionService.setAgentStatus(agentId, AgentStatus.OFFLINE);
        return Result.success();
    }
}
