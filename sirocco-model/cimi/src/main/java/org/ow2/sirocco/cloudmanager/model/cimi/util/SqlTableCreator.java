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
 *  $Id: SqlTableCreator.java 850 2012-02-14 09:26:25Z Cyril AUBOIN $
 *
 */
package org.ow2.sirocco.cloudmanager.model.cimi.util;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.ejb.Ejb3Configuration;

/**
 * SQL Creator for Tables according to JPA/Hibernate annotations. Use:
 * {@link #createTablesScript()} To create the table creationg script
 * {@link #dropTablesScript()} to create the table destruction script
 */
public class SqlTableCreator {

    private final AnnotationConfiguration hibernateConfiguration;

    /**
     * we use MySQL5 & InnoDB
     */
    private final Dialect dialect = new org.hibernate.dialect.MySQL5InnoDBDialect();

    public SqlTableCreator(final Collection<Class> entities) {

        final Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
        for (final Class entity : entities) {
            ejb3Configuration.addAnnotatedClass(entity);
        }

        this.hibernateConfiguration = ejb3Configuration.getHibernateConfiguration();
    }

    /**
     * Create the SQL script to create all tables.
     * 
     * @return A {@link String} representing the SQL script.
     */
    public String createTablesScript() {
        final StringBuilder script = new StringBuilder();

        final String[] creationScript = this.hibernateConfiguration.generateSchemaCreationScript(this.dialect);
        script.append(";sirocco creation script\n");
        for (final String string : creationScript) {
            script.append(string).append(";\n");
        }
        return script.toString();
    }

    /**
     * Create the SQL script to drop all tables.
     * 
     * @return A {@link String} representing the SQL script.
     */
    public String dropTablesScript() {
        final StringBuilder script = new StringBuilder();

        final String[] creationScript = this.hibernateConfiguration.generateDropSchemaScript(this.dialect);
        script.append(";sirocco deletion script\n");
        for (final String string : creationScript) {
            script.append(string).append(";\n");
        }
        return script.toString();
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        // TODO Auto-generated method stub

        ArrayList<Class> coll = new ArrayList<Class>();

        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.CloudProvider.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderAccount.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.CloudProviderLocation.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.Machine.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.MachineAdmin.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.Volume.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.User.class);
        coll.add(org.ow2.sirocco.cloudmanager.model.cimi.MachineImage.class);

        SqlTableCreator sqlTC = new SqlTableCreator(coll);
        System.out.println(sqlTC.dropTablesScript());
        System.out.println(sqlTC.createTablesScript());

    }
}
