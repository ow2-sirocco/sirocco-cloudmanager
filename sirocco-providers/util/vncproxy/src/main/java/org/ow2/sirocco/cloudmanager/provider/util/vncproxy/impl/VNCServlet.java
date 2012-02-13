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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ow2.sirocco.cloudmanager.provider.util.vncproxy.api.VNCProxy;

public class VNCServlet extends HttpServlet {
	private static final long serialVersionUID = -1;
	private VNCProxy webSocketProxyManager;

	VNCServlet(final VNCProxy webSocketProxyManager) {
		this.webSocketProxyManager = webSocketProxyManager;
	}

	@Override
	public void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException,
			ServletException {
		response.setContentType("text/html");

		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");

		String vncSessionToken = request.getParameter("token");
		int localPort = -1;
		try {
			localPort = this.webSocketProxyManager
					.getWebSocketProxyLocalPort(vncSessionToken);
		} catch (Exception e) {
			out.println("<h1> " + e.getMessage() + "</h1>");
			out.println("</body>");
			out.println("</html>");
			out.close();
			return;
		}

		out.println("<head>");
		out.println("<title>Sirocco - Virtual Machine VNC</title>");
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"chrome=1\">");
		out.println("<link rel=\"stylesheet\" href=\"include/base.css\" title=\"plain\">");
		out.println("<script src=\"include/vnc.js\"></script>");
		out.println("</head>");
		out.println("");
		out.println("<body style=\"margin: 0px;\">");
		out.println("<div id=\"noVNC_screen\">");
		out.println("<div id=\"noVNC_status_bar\" class=\"noVNC_status_bar\" style=\"margin-top: 0px;\">");
		out.println("<table border=0 width=\"100%\"><tr>");
		out.println("<td><div id=\"noVNC_status\">Loading</div></td>");
		out.println("<td width=\"1%\"><div id=\"noVNC_buttons\">");
		out.println("<input type=button value=\"Send CtrlAltDel\"");
		out.println("id=\"sendCtrlAltDelButton\">");
		out.println("</div></td>");
		out.println("</tr></table>");
		out.println("</div>");
		out.println("<canvas id=\"noVNC_canvas\" width=\"640px\" height=\"20px\">");
		out.println("Canvas not supported.");
		out.println("</canvas>");
		out.println("</div>");
		out.println("");
		out.println("<script>");
		out.println("/*jslint white: false */");
		out.println("/*global window, $, Util, RFB, */");
		out.println("\"use strict\";");
		out.println("");
		out.println("var rfb;");
		out.println("");
		out.println("function passwordRequired(rfb) {");
		out.println("var msg;");
		out.println("msg = '<form onsubmit=\"return setPassword();\"';");
		out.println("msg += ' style=\"margin-bottom: 0px\">';");
		out.println("msg += 'Password Required: ';");
		out.println("msg += '<input type=password size=10 id=\"password_input\" class=\"noVNC_status\">';");
		out.println("msg += '<\\/form>';");
		out.println("$D('noVNC_status_bar').setAttribute(\"class\", \"noVNC_status_warn\");");
		out.println("$D('noVNC_status').innerHTML = msg;");
		out.println("}");
		out.println("function setPassword() {");
		out.println("rfb.sendPassword($D('password_input').value);");
		out.println("return false;");
		out.println("}");
		out.println("function sendCtrlAltDel() {");
		out.println("rfb.sendCtrlAltDel();");
		out.println("return false;");
		out.println("}");
		out.println("function updateState(rfb, state, oldstate, msg) {");
		out.println("var s, sb, cad, level;");
		out.println("s = $D('noVNC_status');");
		out.println("sb = $D('noVNC_status_bar');");
		out.println("cad = $D('sendCtrlAltDelButton');");
		out.println("switch (state) {");
		out.println("case 'failed': level = \"error\"; break;");
		out.println("case 'fatal': level = \"error\"; break;");
		out.println("case 'normal': level = \"normal\"; break;");
		out.println("case 'disconnected': level = \"normal\"; break;");
		out.println("case 'loaded': level = \"normal\"; break;");
		out.println("default: level = \"warn\"; break;");
		out.println("}");
		out.println("");
		out.println("if (state === \"normal\") { cad.disabled = false; }");
		out.println("else { cad.disabled = true; }");
		out.println("");
		out.println("if (typeof(msg) !== 'undefined') {");
		out.println("sb.setAttribute(\"class\", \"noVNC_status_\" + level);");
		out.println("s.innerHTML = msg;");
		out.println("}");
		out.println("}");
		out.println("");
		out.println("window.onload = function () {");
		out.println("var host, port, password, path;");
		out.println("");
		out.println("$D('sendCtrlAltDelButton').style.display = \"inline\";");
		out.println("$D('sendCtrlAltDelButton').onclick = sendCtrlAltDel;");
		out.println("");
		out.println("document.title = unescape(WebUtil.getQueryVar('title', 'noVNC'));");

		out.println("host = '" + request.getServerName() + "';");
		out.println("port = '" + localPort + "';");
		out.println("password = WebUtil.getQueryVar('password', '');");

		out.println("path = WebUtil.getQueryVar('path', '');");
		out.println("if ((!host) || (!port)) {");
		out.println("updateState('failed',");
		out.println("\"Must specify host and port in URL\");");
		out.println("return;");
		out.println("}");
		out.println("");
		out.println("rfb = new RFB({'target': $D('noVNC_canvas'),");
		out.println("'encrypt': WebUtil.getQueryVar('encrypt', false),");
		out.println("'true_color': WebUtil.getQueryVar('true_color', true),");
		out.println("'local_cursor': WebUtil.getQueryVar('cursor', true),");
		out.println("'shared': WebUtil.getQueryVar('shared', true),");
		out.println("'updateState': updateState,");
		out.println("'onPasswordRequired': passwordRequired});");
		out.println("rfb.connect(host, port, password, path);");
		out.println("};");
		out.println("</script>");
		out.println("");
		out.println("</body>");
		out.println("</html>");

		out.close();
	}

	/**
	 * If there is an exception, print the exception.
	 * 
	 * @param out
	 *            the given writer
	 * @param errMsg
	 *            the error message
	 * @param e
	 *            the content of the exception
	 */
	@SuppressWarnings("unused")
	private void displayException(final PrintWriter out, final String errMsg,
			final Exception e) {
		out.println("<p>Exception : " + errMsg);
		out.println("<pre>");
		e.printStackTrace(out);
		out.println("</pre></p>");
	}

}
