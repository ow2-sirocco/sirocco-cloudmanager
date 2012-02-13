package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "perfMetricSpec")
public class PerfMetricSpec {
    private String metricId;

    private String target;

    private String startTime;

    private String endTime;

    public void setMetricId(final String metricId) {
        this.metricId = metricId;
    }

    public String getMetricId() {
        return this.metricId;
    }

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    @Override
    public String toString() {
        return "PerfMetricSpec [metricId=" + this.metricId + ", target=" + this.target + ", startTime=" + this.startTime
            + ", endTime=" + this.endTime + "]";
    }
}
