/**
 *
 * SIROCCO
 * Copyright (C) 2012 France Telecom
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
package org.ow2.sirocco.apis.rest.cimi.sdk;

import java.util.ArrayList;
import java.util.List;

public class QueryParams {
    private int first = -1;

    private int last = -1;

    private List<String> filters = new ArrayList<String>();

    private String expand = null;

    private QueryParams() {
    }

    public static QueryParams build() {
        return new QueryParams();
    }

    public QueryParams setFirst(final int first) {
        this.first = first;
        return this;
    }

    public QueryParams setLast(final int last) {
        this.last = last;
        return this;
    }

    public QueryParams addFilter(final String filter) {
        this.filters.add(filter);
        return this;
    }

    public QueryParams setExpand(final String expand) {
        this.expand = expand;
        return this;
    }

    public int getFirst() {
        return this.first;
    }

    public int getLast() {
        return this.last;
    }

    public List<String> getFilters() {
        return this.filters;
    }

    public String getExpand() {
        return this.expand;
    }

}
