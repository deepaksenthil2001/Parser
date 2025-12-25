package com.smartcode.analyzer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProgramFlow {

    @JsonProperty("calls")
    private List<String> topLevelCalls;

    @JsonProperty("depth")
    private int maxCallDepthEstimate;

    public ProgramFlow() {}

    public ProgramFlow(List<String> calls, int depth) {
        this.topLevelCalls = calls;
        this.maxCallDepthEstimate = depth;
    }

    public List<String> getTopLevelCalls() { return topLevelCalls; }
    public void setTopLevelCalls(List<String> topLevelCalls) { this.topLevelCalls = topLevelCalls; }

    public int getMaxCallDepthEstimate() { return maxCallDepthEstimate; }
    public void setMaxCallDepthEstimate(int maxCallDepthEstimate) {
        this.maxCallDepthEstimate = maxCallDepthEstimate;
    }
}
