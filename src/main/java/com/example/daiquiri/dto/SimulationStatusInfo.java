package com.example.daiquiri;

public class SimulationStatusInfo extends TransInfo {
    private static final long serialVersionUID = 1L;
    private boolean isStart;
    private String targetClient;

    public SimulationStatusInfo(boolean isStart, String targetClient) {
        this.isStart = isStart;
        this.targetClient = targetClient;
    }

    public boolean isStart() {
        return isStart;
    }

    public String getTargetClient() {
        return targetClient;
    }
}
