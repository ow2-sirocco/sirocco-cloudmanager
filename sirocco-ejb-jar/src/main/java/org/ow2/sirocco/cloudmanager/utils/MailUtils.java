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

package org.ow2.sirocco.cloudmanager.utils;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ow2.sirocco.cloudmanager.common.DbParameters;

public final class MailUtils {

    private MailUtils() {

    }

    /**
     * Send a email
     * 
     * @throws AddressException
     * @throws MessagingException
     */
    public static void sendMail(final String from, final String to, final String subject, final String msg)
        throws AddressException, MessagingException {

        Properties props = System.getProperties(); // Get system properties
        props.put("mail.smtp.host", DbParameters.getInstance().MAIL_HOST);
        props.put("mail.smtp.port", DbParameters.getInstance().MAIL_PORT);
        props.put("mail.smtp.timeout", "30000");
        Session session = Session.getInstance(props, null);

        // Define message
        MimeMessage message = new MimeMessage(session);

        // Set the from address
        message.setFrom(new InternetAddress(from));

        // Set the to address
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set the subject
        message.setSubject(subject);
        message.setText(msg);
        Transport.send(message);

    }

    public static String buildMail(final String template, final Map<String, String> map) {

        final StringBuilder list = new StringBuilder("\\$(");

        for (final String key : map.keySet()) {
            list.append(key);
            list.append("|");
        }
        list.append("[^\\s\\S])");
        Pattern pattern = Pattern.compile(list.toString());
        Matcher matcher = pattern.matcher(template);
        final StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            final String string = matcher.group(1);
            matcher.appendReplacement(stringBuffer, map.get(string));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
}
