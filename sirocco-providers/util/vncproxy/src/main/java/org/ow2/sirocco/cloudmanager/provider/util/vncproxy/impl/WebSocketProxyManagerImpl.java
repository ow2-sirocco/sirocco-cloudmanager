/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.provider.util.vncproxy.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.http.HttpService;
import org.ow2.sirocco.cloudmanager.provider.util.vncproxy.api.VNCProxy;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

public class WebSocketProxyManagerImpl implements VNCProxy, ManagedService {
    private static Log logger = LogFactory.getLog(WebSocketProxyManagerImpl.class);

    private static final String PORT_RANGE_MIN_PROP_NAME = "service.websocketproxy.portrange.min";

    private static final String PORT_RANGE_MAX_PROP_NAME = "service.websocketproxy.portrange.max";

    private static final String WEBSOCKIFY_PROGRAM_PROP_NAME = "service.websocketproxy.program";

    private static final String VNC_SERVLET_HOST_PROP_NAME = "service.vnc.servlet.host";

    private static final String VNC_SERVLET_PORT_PROP_NAME = "service.vnc.servlet.port";

    private static final String VNC_SERVLET_ALIAS_PROP_NAME = "service.vnc.servlet.alias";

    private static final int DEFAULT_LOCAL_PORT_PORT_RANGE_MIN = 6000;

    private static final int DEFAULT_LOCAL_PORT_PORT_RANGE_MAX = 6999;

    private static final String DEFAULT_WEBSOCKIFY_PROGRAM = "wsproxy.sh";

    private static final int DEFAULT_VNC_SERVLET_PORT = 9000;

    private static final String DEFAULT_VNC_SERVLET_HOST = "localhost";

    private static final String DEFAULT_VNC_SERVLET_ALIAS = "/console";

    private static class Proxy {
        final String targetHost;

        final int targetPort;

        final int localPort;

        final Process process;

        final String token;

        Proxy(final String targetHost, final int targetPort, final int localPort, final Process process, final String token) {
            this.targetHost = targetHost;
            this.targetPort = targetPort;
            this.localPort = localPort;
            this.process = process;
            this.token = token;
        }
    }

    private Map<String, Proxy> proxies = new HashMap<String, Proxy>();

    private int portRangeMin = WebSocketProxyManagerImpl.DEFAULT_LOCAL_PORT_PORT_RANGE_MIN;

    private int portRangeMax = WebSocketProxyManagerImpl.DEFAULT_LOCAL_PORT_PORT_RANGE_MAX;

    private String webSockifyProgram = WebSocketProxyManagerImpl.DEFAULT_WEBSOCKIFY_PROGRAM;

    private HttpService httpService;

    private String vncServletHost = WebSocketProxyManagerImpl.DEFAULT_VNC_SERVLET_HOST;

    private int vncServletPort = WebSocketProxyManagerImpl.DEFAULT_VNC_SERVLET_PORT;

    private String vncServletAlias = WebSocketProxyManagerImpl.DEFAULT_VNC_SERVLET_ALIAS;

    WebSocketProxyManagerImpl(final HttpService httpService) {
        this.httpService = httpService;
    }

    public void start() {
        try {
            this.httpService.registerServlet(this.vncServletAlias, new VNCServlet(this), null, null);
            this.httpService.registerResources("/include", "/include", null);
        } catch (Exception e) {
            WebSocketProxyManagerImpl.logger.error("Failed to register servlet", e);
        }

        WebSocketProxyManagerImpl.logger.info("WebSocket Proxy Manager started");
    }

    public synchronized void shutdown() {
        for (Proxy proxy : this.proxies.values()) {
            WebSocketProxyManagerImpl.logger.info("Destroying WebSocket proxy localport=" + proxy.localPort + " target="
                + proxy.targetHost + ":" + proxy.targetPort);
            proxy.process.destroy();
        }
    }

    @SuppressWarnings("rawtypes")
    public void updated(final Dictionary properties) throws ConfigurationException {
        if (properties != null) {
            try {
                String s = (String) properties.get(WebSocketProxyManagerImpl.PORT_RANGE_MIN_PROP_NAME);
                if (s != null) {
                    this.portRangeMin = Integer.parseInt(s);
                }
                s = (String) properties.get(WebSocketProxyManagerImpl.PORT_RANGE_MAX_PROP_NAME);
                if (s != null) {
                    this.portRangeMax = Integer.parseInt(s);
                }
                this.webSockifyProgram = (String) properties.get(WebSocketProxyManagerImpl.WEBSOCKIFY_PROGRAM_PROP_NAME);
                if (this.webSockifyProgram == null) {
                    this.webSockifyProgram = WebSocketProxyManagerImpl.DEFAULT_WEBSOCKIFY_PROGRAM;
                }

                this.vncServletHost = (String) properties.get(WebSocketProxyManagerImpl.VNC_SERVLET_HOST_PROP_NAME);
                if (this.vncServletHost == null) {
                    this.vncServletHost = WebSocketProxyManagerImpl.DEFAULT_VNC_SERVLET_HOST;
                }

                this.vncServletAlias = (String) properties.get(WebSocketProxyManagerImpl.VNC_SERVLET_ALIAS_PROP_NAME);
                if (this.vncServletAlias == null) {
                    this.vncServletAlias = WebSocketProxyManagerImpl.DEFAULT_VNC_SERVLET_ALIAS;
                }

                s = (String) properties.get(WebSocketProxyManagerImpl.VNC_SERVLET_PORT_PROP_NAME);
                if (s != null) {
                    this.vncServletPort = Integer.parseInt(s);
                }

            } catch (NumberFormatException ex) {
                WebSocketProxyManagerImpl.logger.error("Illegal parameter port WebSocket Proxy Manager port range "
                    + ex.getMessage());
                this.portRangeMin = WebSocketProxyManagerImpl.DEFAULT_LOCAL_PORT_PORT_RANGE_MIN;
                this.portRangeMax = WebSocketProxyManagerImpl.DEFAULT_LOCAL_PORT_PORT_RANGE_MAX;
            }
        }
        WebSocketProxyManagerImpl.logger.info("WebSocket Proxy Manager ready, exec=" + this.webSockifyProgram + " port range ["
            + this.portRangeMin + "," + this.portRangeMax + "]");
    }

    @Override
    public synchronized String getWebSocketProxySessionToken(final String host, final int port) throws Exception {
        String key = host + ":" + port;
        Proxy proxy = this.proxies.get(key);
        if (proxy == null) {
            proxy = this.createProxy(host, port);
            this.proxies.put(key, proxy);
        }
        return proxy.token;
    }

    @Override
    public String getVncUrl(final String host, final int port) throws Exception {
        String token = this.getWebSocketProxySessionToken(host, port);
        return "http://" + this.vncServletHost + ":" + this.vncServletPort + this.vncServletAlias + "?token=" + token;
    }

    @Override
    public synchronized int getWebSocketProxyLocalPort(final String token) throws Exception {
        for (Proxy proxy : this.proxies.values()) {
            if (proxy.token.equals(token)) {
                return proxy.localPort;
            }
        }
        throw new Exception("Illegal token");
    }

    @Override
    public synchronized void destroyWebSocketProxy(final String host, final int port) throws Exception {
        String key = host + ":" + port;
        Proxy proxy = this.proxies.get(key);
        if (proxy != null) {
            try {
                proxy.process.destroy();
            } finally {
                this.proxies.remove(key);
            }
        }
    }

    private Proxy createProxy(final String host, final int port) throws Exception {
        int localPort = AvailablePortFinder.getNextAvailable(this.portRangeMin);
        if (localPort >= this.portRangeMax) {
            throw new Exception("Port range exhausted");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        processBuilder.command(this.webSockifyProgram, Integer.toString(localPort), host + ":" + port);
        WebSocketProxyManagerImpl.logger.info("Starting process " + processBuilder.command());
        Process process = processBuilder.start();

        // XXX wait 1s for process to exit in case of error
        Thread.sleep(1000);
        try {
            int returnCode = process.exitValue();
            throw new Exception("Failed to start websockify program, exit code=" + returnCode + " "
                + this.getProcessOuput(process));
        } catch (IllegalThreadStateException ex) {
            int waitTimeInSec = 10;
            while (waitTimeInSec-- > 0) {
                if (!AvailablePortFinder.available(localPort)) {
                    break;
                }
                Thread.sleep(1000);
            }

            Proxy proxy = new Proxy(host, port, localPort, process, UUID.randomUUID().toString());
            WebSocketProxyManagerImpl.logger.info("Created WebSocket proxy for " + host + ":" + port + " token=" + proxy.token);
            return proxy;
        }
    }

    private String getProcessOuput(final Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuffer result = new StringBuffer();
        while (reader.ready()) {
            result.append(reader.readLine());
        }
        return result.toString();
    }

}
