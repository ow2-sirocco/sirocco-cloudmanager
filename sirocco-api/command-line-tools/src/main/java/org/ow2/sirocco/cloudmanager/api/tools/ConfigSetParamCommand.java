/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
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
 */
package org.ow2.sirocco.cloudmanager.api.tools;

import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "set config parameter")
public class ConfigSetParamCommand implements Command {
    private static String COMMAND_NAME = "config-set";

    @Parameter(names = "-key", description = "config key", required = true)
    private String key;

    @Parameter(names = "-value", description = "config value", required = true)
    private String value;

    @Override
    public String getName() {
        return ConfigSetParamCommand.COMMAND_NAME;
    }

    @Override
    public void execute(final RestClient restClient) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put(this.key, this.value);
        restClient.putRequest("config", params);
    }

}
