package com.vmware.vcloud.sdk;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.vmware.vcloud.api.rest.schema.ObjectFactory;

// this class hides the class with the same name in VMware vcloud sdk jar
// it works around an issue when using JABX from an OSGi bundle with GlassFish  
// see http://java.net/jira/browse/GLASSFISH-11748
// 

public class JAXBUtil {
    private static Logger logger = Logger.getLogger("com.vmware.vcloud.sdk");

    private static JAXBContext jaxbContexts;

    public static void addPackages(final List<String> packageNames) {
        String packages = "com.vmware.vcloud.api.rest.schema.versioning:com.vmware.vcloud.api.rest.schema.ovf:com.vmware.vcloud.api.rest.schema:com.vmware.vcloud.api.rest.schema.extension:com.vmware.vcloud.api.rest.schema.ovf.environment:com.vmware.vcloud.api.rest.schema.ovf.vmware:";

        for (String packageName : packageNames) {
            packages = packages + packageName + ":";
        }
        try {
            if ((packages != null) && (!packages.isEmpty())) {
                JAXBUtil.jaxbContexts = JAXBContext.newInstance(packages, ObjectFactory.class.getClassLoader());
            } else {
                throw new VCloudRuntimeException("No Packages Found");
            }
        } catch (JAXBException e) {
            JAXBUtil.logger.log(Level.SEVERE, null, e);
            e.printStackTrace();
            throw new VCloudRuntimeException(e.getMessage());
        }
    }

    public static <Type> Type unmarshallResource(final InputStream is) {
        JAXBElement<Type> element = null;
        try {
            Unmarshaller unmarshaller = JAXBUtil.jaxbContexts.createUnmarshaller();

            element = (JAXBElement) unmarshaller.unmarshal(new StreamSource(new InputStreamReader(is, "UTF-8")));
        } catch (JAXBException ex) {
            JAXBUtil.logger.log(Level.SEVERE, null, ex);
            throw new VCloudRuntimeException(ex.getMessage());
        } catch (UnsupportedEncodingException e) {
            JAXBUtil.logger.log(Level.SEVERE, null, e);
            throw new VCloudRuntimeException(e.getMessage());
        }
        if (element == null) {
            return null;
        }
        return element.getValue();
    }

    public static String marshal(final JAXBElement<?> element) {
        try {
            Marshaller marshaller = JAXBUtil.jaxbContexts.createMarshaller();

            marshaller.setProperty("jaxb.encoding", "UTF-8");

            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);

            OutputStream os = new ByteArrayOutputStream();
            marshaller.marshal(element, os);
            return os.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    static {
        if (JAXBUtil.jaxbContexts == null) {
            try {
            	// do not use package names 
                JAXBUtil.jaxbContexts = JAXBContext.newInstance(ObjectFactory.class,
                    com.vmware.vcloud.api.rest.schema.extension.ObjectFactory.class,
                    com.vmware.vcloud.api.rest.schema.ovf.environment.ObjectFactory.class,
                    com.vmware.vcloud.api.rest.schema.ovf.ObjectFactory.class,
                    com.vmware.vcloud.api.rest.schema.ovf.vmware.ObjectFactory.class,
                    com.vmware.vcloud.api.rest.schema.versioning.ObjectFactory.class);
            } catch (JAXBException e) {
                e.printStackTrace();
                JAXBUtil.logger.log(Level.SEVERE, null, e);
            }
        }
    }
}