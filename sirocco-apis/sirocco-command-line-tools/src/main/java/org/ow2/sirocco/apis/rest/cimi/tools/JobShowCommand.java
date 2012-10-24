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
import org.ow2.sirocco.apis.rest.cimi.sdk.Job;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "job machine")
public class JobShowCommand implements Command {
    @Parameter(names = "-id", description = "id of the job", required = true)
    private String jobId;

    @Override
    public String getName() {
        return "job-show";
    }

    @Override
    public void execute(final CimiClient cimiClient) throws Exception {
        Job job = Job.getJobByReference(cimiClient, this.jobId);

        Table table = new Table(2);
        table.addCell("Attribute");
        table.addCell("Value");

        table.addCell("id");
        table.addCell(job.getId());

        table.addCell("action");
        table.addCell(job.getAction());
        table.addCell("state");
        table.addCell(job.getState().toString());
        table.addCell("target entity");
        table.addCell(job.getTargetResourceRef());
        table.addCell("affected entity");
        if (job.getAffectedResourceRefs().length > 0) {
            table.addCell(job.getAffectedResourceRefs()[0]);
        } else {
            table.addCell("");
        }
        table.addCell("created");
        table.addCell(job.getCreated().toString());
        table.addCell("updated");
        if (job.getUpdated() != null) {
            table.addCell(job.getUpdated().toString());
        } else {
            table.addCell("");
        }
        table.addCell("properties");
        StringBuffer sb = new StringBuffer();
        if (job.getProperties() != null) {
            for (Map.Entry<String, String> prop : job.getProperties().entrySet()) {
                sb.append("(" + prop.getKey() + "," + prop.getValue() + ") ");
            }
        }
        table.addCell(sb.toString());

        System.out.println(table.render());
    }

}
