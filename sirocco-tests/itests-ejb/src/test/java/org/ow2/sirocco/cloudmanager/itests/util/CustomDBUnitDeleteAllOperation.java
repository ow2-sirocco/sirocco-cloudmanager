package org.ow2.sirocco.cloudmanager.itests.util;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IBatchStatement;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITableIterator;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.operation.AbstractOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class replaces the DeleteAllOperation from DBUnit 
 * to temporary remove db integrity constraints to be able to delete all rows.<br>
 * Works with MYSQL and HSQLDB databases.<br>
 * This class should be used as an argument of setSetUpOperation()<br>
 * @author ycas7461
 *
 */
public class CustomDBUnitDeleteAllOperation extends AbstractOperation{

    /**
     * Logger for this class
     */
    private final Logger logger = LoggerFactory.getLogger(CustomDBUnitDeleteAllOperation.class);

    private String databaseType;
    
    CustomDBUnitDeleteAllOperation()
    {
    }
    /**
     * Constructor that allows to specify a database.<br>
     * If an unsupported database is given, the standard behaviour is kept
     * @param databaseType
     */
    public CustomDBUnitDeleteAllOperation(String databaseType)
    {
        this.databaseType=databaseType;
    }

    protected String getDeleteAllCommand()
    {
        return "delete from ";
    }

    ////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(IDatabaseConnection connection, IDataSet dataSet)
            throws DatabaseUnitException, SQLException
    {
        logger.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        IDataSet databaseDataSet = connection.createDataSet();

        DatabaseConfig databaseConfig = connection.getConfig();
        IStatementFactory statementFactory = (IStatementFactory)databaseConfig.getProperty(DatabaseConfig.PROPERTY_STATEMENT_FACTORY);
        IBatchStatement statement = statementFactory.createBatchStatement(connection);
        
        if (databaseType.equals("MYSQL"))
        {
            statement.addBatch("SET FOREIGN_KEY_CHECKS=0;");
        }
        if (databaseType.equals("HSQLDB"))
        {
            statement.addBatch("SET REFERENTIAL_INTEGRITY FALSE;");
        }
        try
        {
            int count = 0;
            
            Stack tableNames = new Stack();
            Set tablesSeen = new HashSet();
            ITableIterator iterator = dataSet.iterator();
            while (iterator.next())
            {
                String tableName = iterator.getTableMetaData().getTableName();
                if (!tablesSeen.contains(tableName))
                {
                    tableNames.push(tableName);
                    tablesSeen.add(tableName);
                }
            }

            // delete tables once each in reverse order of seeing them.
            while (!tableNames.isEmpty())
            {
                String tableName = (String)tableNames.pop();

                // Use database table name. Required to support case sensitive database.
                ITableMetaData databaseMetaData = databaseDataSet.getTableMetaData(tableName);
                tableName = databaseMetaData.getTableName();

                StringBuffer sqlBuffer = new StringBuffer(128);
                sqlBuffer.append(getDeleteAllCommand());
                sqlBuffer.append(getQualifiedName(connection.getSchema(), tableName, connection));
                String sql = sqlBuffer.toString();
                statement.addBatch(sql);

                if(logger.isDebugEnabled())
                    logger.debug("Added SQL: {}", sql);
                
                count++;
            }

            if (count > 0)
            {
                statement.executeBatch();
                statement.clearBatch();
            }
        }
        finally
        {
            if (databaseType.equals("MYSQL"))
            {
                statement.addBatch("SET FOREIGN_KEY_CHECKS=1;");
            }
            if (databaseType.equals("HSQLDB"))
            {
                statement.addBatch("SET REFERENTIAL_INTEGRITY TRUE;");
            }
            statement.executeBatch();
            statement.clearBatch();
            statement.close();
        }
    }
}