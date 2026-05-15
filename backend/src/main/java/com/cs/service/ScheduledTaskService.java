package com.cs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    @Autowired
    private SessionService sessionService;

    @Scheduled(fixedRateString = "${scheduler.stats-interval-ms:10000}")
    public void scheduleSessionAssignment() {
        sessionService.tryAssignSession();
    }

    @Scheduled(fixedRateString = "${scheduler.stats-interval-ms:10000}")
    public void checkTimeoutSessions() {
        sessionService.checkAndTransferTimeoutSessions();
    }

    @Scheduled(fixedRateString = "${scheduler.recycle-interval-ms:30000}")
    public void recycleOfflineAgentSessions() {
        sessionService.recycleOfflineAgentSessions();
    }
}
