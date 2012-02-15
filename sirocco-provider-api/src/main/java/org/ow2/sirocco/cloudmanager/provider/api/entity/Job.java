package org.ow2.sirocco.cloudmanager.provider.api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.Future;

import javax.persistence.Entity;

@Entity
public class Job<T> extends CloudEntity implements Serializable {

    static enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    };

    private static final long serialVersionUID = 1L;

    private Integer id;

    private CloudEntity targetEntity;

    private String action;

    private Status status;

    private Integer returnCode;

    private Integer progress;

    private String statusMessage;

    private Date timeOfStatusChange;

    private Boolean isCancellable;

    private CloudEntity parentJob;

    private Future<T> result;

    Job(final Integer id, final Future<T> result, final CloudEntity targetEntity, final String action) {
        this.id = id;
        this.result = result;
        this.targetEntity = targetEntity;
        this.action = action;
        // this.updateStatus();
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public CloudEntity getTargetEntity() {
        return this.targetEntity;
    }

    public String getAction() {
        return this.action;
    }

    public Future<T> getResult() {
        return this.result;
    }

    public boolean isCancellable() {
        // TODO
        return false;
    }

    public boolean cancel() {
        // TODO
        return false;
    }

    /*
     * private synchronized void updateStatus() { if (this.status == null ||
     * this.status == Status.RUNNING) { Status newStatus = this.status; if
     * (this.result.isCancelled()) { newStatus = Status.CANCELLED;
     * this.statusMessage = "cancelled"; } else if (!this.result.isDone()) {
     * newStatus = Status.RUNNING; this.statusMessage = "running"; } else {
     * boolean interrupted = false; try { while (true) { try {
     * this.result.get(); newStatus = Status.SUCCESS; break; } catch
     * (InterruptedException ex) { interrupted = true; // retry until not
     * interrupted } catch (ExecutionException ex) { this.statusMessage =
     * ex.getCause().getMessage(); newStatus = Status.FAILED; break; } catch
     * (CancellationException ex) { newStatus = Status.CANCELLED;
     * this.statusMessage = "cancelled"; break; } } } finally { if (interrupted)
     * { Thread.currentThread().interrupt(); } } } if (newStatus != this.status)
     * { this.status = newStatus; this.timeOfStatusChange = new Date(); } } }
     */

    public Status getStatus() {
        // this.updateStatus();
        return this.status;
    }

    public String getStatusMessage() {
        // this.updateStatus();
        return this.statusMessage;
    }

    public int getProgress() {
        // TODO
        return 0;
    }

    public Date getTimeOfStatusChange() {
        // this.updateStatus();
        return this.timeOfStatusChange;
    }

    @Override
    public String toString() {
        // this.updateStatus();
        return "Job [id=" + this.id + ", targetEntityId=" + this.targetEntity + ", action=" + this.action
            + ", timeOfStatusChange=" + this.timeOfStatusChange + ", status=" + this.status + ", statusMessage="
            + this.statusMessage + "]";
    }
}