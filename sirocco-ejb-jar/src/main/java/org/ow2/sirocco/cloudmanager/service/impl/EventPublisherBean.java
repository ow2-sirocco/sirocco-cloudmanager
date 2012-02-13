package org.ow2.sirocco.cloudmanager.service.impl;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.service.api.IEventPublisher;
import org.ow2.sirocco.cloudmanager.service.event.DelVMImageEvent;
import org.ow2.sirocco.cloudmanager.service.event.DelVirtualMachineEvent;
import org.ow2.sirocco.cloudmanager.service.event.DelVolumeEvent;
import org.ow2.sirocco.cloudmanager.service.event.NewVMImageEvent;
import org.ow2.sirocco.cloudmanager.service.event.NewVirtualMachineEvent;
import org.ow2.sirocco.cloudmanager.service.event.NewVolumeEvent;
import org.ow2.sirocco.cloudmanager.service.event.VMImageUpdateEvent;
import org.ow2.sirocco.cloudmanager.service.event.VirtualMachineStatusChangeEvent;
import org.ow2.sirocco.cloudmanager.service.event.VolumeUpdateEvent;

@Stateless(mappedName = IEventPublisher.EJB_JNDI_NAME)
@Local(IEventPublisher.class)
public class EventPublisherBean implements IEventPublisher {
    private static Logger logger = Logger.getLogger(EventPublisherBean.class.getName());

    @Resource(mappedName = "JTCF")
    private TopicConnectionFactory topicConnectionFactory;

    @Resource(mappedName = "CloudAdmin")
    private Topic cloudAdminTopic;

    @Override
    public void emitTopicMessage(final Serializable obj, final String property, final String value) {
        try {
            TopicConnection connection = this.topicConnectionFactory.createTopicConnection();
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher topicPublisher = session.createPublisher(this.cloudAdminTopic);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(obj);
            message.setStringProperty(property, value);
            topicPublisher.publish(message);
            EventPublisherBean.logger.info("EMITTED EVENT " + obj + " on CloudAdmin topic");
            topicPublisher.close();
            session.close();
            connection.close();
        } catch (JMSException ex) {
            EventPublisherBean.logger.severe(ex.getMessage());
        }
    }

    public void notifyMachineChange(final Machine vm) {
        this.emitTopicMessage(new VirtualMachineStatusChangeEvent(vm.toValueObject()), "projectId", vm.getProject()
            .getProjectId());
    }

    public void notifyMachineDeletion(final Machine vm) {
        this.emitTopicMessage(new DelVirtualMachineEvent(vm.getId().toString(), null), "projectId", vm.getProject()
            .getProjectId());
    }

    public void notifyMachineCreation(final Machine vm) {
        this.emitTopicMessage(new NewVirtualMachineEvent(vm.toValueObject()), "projectId", vm.getProject().getProjectId());
    }

    public void notifyImageChange(final MachineImage image) {
        this.emitTopicMessage(new VMImageUpdateEvent(image.toValueObject()), "projectId", image.getProject().getProjectId());
    }

    public void notifyImageCreation(final MachineImage image) {
        this.emitTopicMessage(new NewVMImageEvent(image.toValueObject()), "projectId", image.getProject().getProjectId());
    }

    public void notifyImageDeletion(final MachineImage image) {
        this.emitTopicMessage(new DelVMImageEvent(image.getId()), "projectId", image.getProject().getProjectId());
    }

    public void notifyVolumeChange(final Volume volume) {
        this.emitTopicMessage(new VolumeUpdateEvent(volume.toValueObject()), "projectId", volume.getProject().getProjectId());
    }

    public void notifyVolumeCreation(final Volume volume) {
        this.emitTopicMessage(new NewVolumeEvent(volume.toValueObject()), "projectId", volume.getProject().getProjectId());
    }

    public void notifyVolumeDeletion(final Volume volume) {
        this.emitTopicMessage(new DelVolumeEvent(volume.getId()), "projectId", volume.getProject().getProjectId());
    }

}
