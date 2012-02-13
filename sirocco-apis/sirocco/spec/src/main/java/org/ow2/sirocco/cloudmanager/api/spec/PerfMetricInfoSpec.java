package org.ow2.sirocco.cloudmanager.api.spec;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "perfMetricInfoSpec")
public class PerfMetricInfoSpec {
    private String target;

    public void setTarget(final String target) {
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    @Override
    public String toString() {
        return "PerfMetricInfoSpec [target=" + this.target + "]";
    }
}
