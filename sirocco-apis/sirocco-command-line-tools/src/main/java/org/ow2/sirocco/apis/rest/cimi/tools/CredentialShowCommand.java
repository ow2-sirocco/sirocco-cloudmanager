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
package org.ow2.sirocco.apis.rest.cimi.tools;

import java.util.Map;

import org.nocrala.tools.texttablefmt.Table;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiClient;
import org.ow2.sirocco.apis.rest.cimi.sdk.CimiException;
import org.ow2.sirocco.apis.rest.cimi.sdk.Credential;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "show credential")
public class CredentialShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the credential", required = true)
    private String credentialId;

    @Override
    public String getName() {
        return "credential-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws CimiException {
        Credential cred = Credential.getCredentialByReference(cimiClient, this.credentialId);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(cred.getId());

        table.addCell("name");
        table.addCell(cred.getName());
        table.addCell("description");
        table.addCell(cred.getDescription());
        table.addCell("public key");
        if (cred.getPublicKey() != null) {
            table.addCell(cred.getPublicKey().substring(0, 10) + "...");
        } else {
            table.addCell("");
        }
        table.addCell("created");
        table.addCell(cred.getCreated().toString());
        table.addCell("updated");
        if (cred.getUpdated() != null) {
            table.addCell(cred.getUpdated().toString());
        } else {
            table.addCell("");
        }
        table.addCell("properties");
        StringBuffer sb = new StringBuffer();
        if (cred.getProperties() != null) {
            for (Map.Entry<String, String> prop : cred.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
