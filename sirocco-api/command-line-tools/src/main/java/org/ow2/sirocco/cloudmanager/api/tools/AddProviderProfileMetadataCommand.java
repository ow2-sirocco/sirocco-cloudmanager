/**
 *
 * SIROCCO
 * Copyright (C) 2013 France Telecom
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
 *
 */
package org.ow2.sirocco.cloudmanager.api.tools;

import java.util.List;

import org.ow2.sirocco.cloudmanager.api.model.ProviderProfile;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "add provider profile metadata")
public class AddProviderProfileMetadataCommand implements Command {
    private static String COMMAND_NAME = "provider-profile-metadata-add";

    @Parameter(names = "-profileId", description = "profile id", required = true)
    private String profileId;

    @Parameter(names = "-metadata", variableArity = true, description = "metadata", required = true)
    private List<String> metadata;

    @Override
    public String getName() {
        return AddProviderProfileMetadataCommand.COMMAND_NAME;
    }

    private String removeQuotes(final String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        ProviderProfile.AccountParameter accountParam = new ProviderProfile.AccountParameter();

        for (String keyValuePair : this.metadata) {
            String[] keyValue = keyValuePair.split("=");
            switch (keyValue[0]) {
            case "name":
                accountParam.setName(this.removeQuotes(keyValue[1]));
                break;
            case "alias":
                accountParam.setAlias(this.removeQuotes(keyValue[1]));
                break;
            case "description":
                accountParam.setDescription(this.removeQuotes(keyValue[1]));
                break;
            case "type":
                accountParam.setType(keyValue[1]);
                break;
            case "required":
                accountParam.setRequired(Boolean.valueOf(keyValue[1]));
                break;

            }
        }

        restClient.postCreateRequest("providers/profiles/" + this.profileId + "/metadata", accountParam,
            ProviderProfile.AccountParameter.class);
    }

}
