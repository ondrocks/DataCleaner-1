/**
 * DataCleaner (community edition)
 * Copyright (C) 2014 Neopost - Customer Information Management
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.datacleaner.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.datacleaner.connection.AccessDatastore;
import org.datacleaner.connection.CassandraDatastore;
import org.datacleaner.connection.CompositeDatastore;
import org.datacleaner.connection.CouchDbDatastore;
import org.datacleaner.connection.CsvDatastore;
import org.datacleaner.connection.DatastoreDescriptor;
import org.datacleaner.connection.DatastoreDescriptorImpl;
import org.datacleaner.connection.DbaseDatastore;
import org.datacleaner.connection.ElasticSearchDatastore;
import org.datacleaner.connection.ExcelDatastore;
import org.datacleaner.connection.FixedWidthDatastore;
import org.datacleaner.connection.HBaseDatastore;
import org.datacleaner.connection.JdbcDatastore;
import org.datacleaner.connection.JsonDatastore;
import org.datacleaner.connection.MongoDbDatastore;
import org.datacleaner.connection.SalesforceDatastore;
import org.datacleaner.connection.SasDatastore;
import org.datacleaner.connection.SugarCrmDatastore;
import org.datacleaner.connection.XmlDatastore;
import org.datacleaner.user.UserPreferences;
import org.apache.metamodel.util.CollectionUtils;
import org.apache.metamodel.util.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A catalog of metadata about database drivers and their current installation
 * status.
 */
@Singleton
public class DatabaseDriverCatalog implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseDriverCatalog.class);

    public static final String DATABASE_NAME_JDBC_ODBC_BRIDGE = "JDBC-ODBC bridge";
    public static final String DATABASE_NAME_TERADATA = "Teradata";
    public static final String DATABASE_NAME_H2 = "H2";
    public static final String DATABASE_NAME_HSQLDB_HYPER_SQL = "Hsqldb/HyperSQL";
    public static final String DATABASE_NAME_ORACLE = "Oracle";
    public static final String DATABASE_NAME_APACHE_DERBY_EMBEDDED = "Apache Derby (embedded)";
    public static final String DATABASE_NAME_APACHE_DERBY_CLIENT = "Apache Derby (client)";
    public static final String DATABASE_NAME_SQLITE = "SQLite";
    public static final String DATABASE_NAME_SYBASE = "Sybase";
    public static final String DATABASE_NAME_MICROSOFT_SQL_SERVER_JTDS = "Microsoft SQL Server";
    public static final String DATABASE_NAME_MICROSOFT_SQL_SERVER_OFFICIAL = "Microsoft SQL Server (official)";
    public static final String DATABASE_NAME_POSTGRESQL = "PostgreSQL";
    public static final String DATABASE_NAME_SAP_DB = "SAP DB";
    public static final String DATABASE_NAME_FIREBIRD = "Firebird";
    public static final String DATABASE_NAME_INGRES = "Ingres";
    public static final String DATABASE_NAME_DB2 = "DB2";
    public static final String DATABASE_NAME_MYSQL = "MySQL";
    public static final String DATABASE_NAME_PENTAHO_DATA_INTEGRATION = "Pentaho Data Integration";
    public static final String DATABASE_NAME_LUCIDDB = "LucidDB";
    public static final String DATABASE_NAME_PERVASIVE = "Pervasive";
    public static final String DATABASE_NAME_CUBRID = "Cubrid";
    public static final String DATABASE_NAME_HIVE = "Apache Hive";

    private static final List<DatabaseDriverDescriptor> _databaseDrivers;

    static {
        _databaseDrivers = new ArrayList<DatabaseDriverDescriptor>();
        add(DATABASE_NAME_MYSQL, "images/datastore-types/databases/mysql.png", "com.mysql.jdbc.Driver",
                "http://repo1.maven.org/maven2/mysql/mysql-connector-java/5.1.18/mysql-connector-java-5.1.18.jar",
                "jdbc:mysql://<hostname>:3306/<database>?defaultFetchSize=" + Integer.MIN_VALUE
                        + "&largeRowSizeThreshold=1024", "jdbc:mysql://<hostname>:<port>/<database>?defaultFetchSize="
                        + Integer.MIN_VALUE + "&largeRowSizeThreshold=1024");
        add(DATABASE_NAME_DB2, "images/datastore-types/databases/db2.png", "com.ibm.db2.jcc.DB2Driver", null,
                "jdbc:db2://<hostname>:<port>/<database>", "jdbc:db2j:net://<hostname>:<port>/<database>");
        add(DATABASE_NAME_INGRES, "images/datastore-types/databases/ingres.png", "com.ingres.jdbc.IngresDriver",
                "http://repo1.maven.org/maven2/com/ingres/jdbc/iijdbc/9.3-3.8.2/iijdbc-9.3-3.8.2.jar",
                "jdbc:ingres://<hostname>:II7/<database>");
        add(DATABASE_NAME_FIREBIRD,
                "images/datastore-types/databases/firebird.png",
                "org.firebirdsql.jdbc.FBDriver",
                // firebird's driver also depends on the j2ee spec
                new String[] { "http://repo1.maven.org/maven2/org/firebirdsql/jdbc/jaybird/2.1.6/jaybird-2.1.6.jar",
                        "http://repo1.maven.org/maven2/geronimo-spec/geronimo-spec-j2ee/1.4-rc4/geronimo-spec-j2ee-1.4-rc4.jar" },
                new String[] { "jdbc:firebirdsql:<hostname>:<path/to/database>.fdb" });
        add(DATABASE_NAME_SAP_DB, "images/datastore-types/databases/sapdb.png", "com.sap.dbtech.jdbc.DriverSapDB",
                null, "jdbc:sapdb://<hostname>/<database>");
        add(DATABASE_NAME_POSTGRESQL, "images/datastore-types/databases/postgresql.png", "org.postgresql.Driver",
                "http://repo1.maven.org/maven2/postgresql/postgresql/9.3-1102-jdbc4/postgresql-9.3-1102-jdbc4.jar",
                "jdbc:postgresql://<hostname>:5432/<database>");
        add(DATABASE_NAME_MICROSOFT_SQL_SERVER_JTDS, "images/datastore-types/databases/microsoft.png",
                "net.sourceforge.jtds.jdbc.Driver",
                "http://repo1.maven.org/maven2/net/sourceforge/jtds/jtds/1.3.1/jtds-1.3.1.jar",
                "jdbc:jtds:sqlserver://<hostname>/<database>;useUnicode=true;characterEncoding=UTF-8",
                "jdbc:jtds:sqlserver://<hostname>:<port>/<database>;instance=<instance>;useUnicode=true;characterEncoding=UTF-8");
        add(DATABASE_NAME_SYBASE, "images/datastore-types/databases/sybase.png", "net.sourceforge.jtds.jdbc.Driver",
                "http://repo1.maven.org/maven2/net/sourceforge/jtds/jtds/1.2.4/jtds-1.2.4.jar",
                "jdbc:jtds:sybase://<hostname>/<database>");
        add(DATABASE_NAME_SQLITE, "images/datastore-types/databases/sqlite.png", "org.sqlite.JDBC",
                "http://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.7.2/sqlite-jdbc-3.7.2.jar",
                "jdbc:sqlite:<path/to/database>.db");
        add(DATABASE_NAME_APACHE_DERBY_CLIENT, "images/datastore-types/databases/derby.png",
                "org.apache.derby.jdbc.ClientDriver",
                "http://repo1.maven.org/maven2/org/apache/derby/derbyclient/10.8.2.2/derbyclient-10.8.2.2.jar",
                "jdbc:derby://<hostname>:1527/<path/to/database>");
        add(DATABASE_NAME_APACHE_DERBY_EMBEDDED, "images/datastore-types/databases/derby.png",
                "org.apache.derby.jdbc.EmbeddedDriver",
                "http://repo1.maven.org/maven2/org/apache/derby/derby/10.8.2.2/derby-10.8.2.2.jar",
                "jdbc:derby:<database>");
        add(DATABASE_NAME_ORACLE, "images/datastore-types/databases/oracle.png", "oracle.jdbc.OracleDriver", null,
                "jdbc:oracle:thin:@<hostname>:1521:<sid>", "jdbc:oracle:thin:@<hostname>:<port>:<sid>",
                "jdbc:oracle:thin:@<hostname>:<port>/<service>:<server>/<instance>");
        add(DATABASE_NAME_MICROSOFT_SQL_SERVER_OFFICIAL, "images/datastore-types/databases/microsoft.png",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver", null,
                "jdbc:sqlserver://<hostname>:3341;databaseName=<database>",
                "jdbc:sqlserver://<hostname>:<port>;databaseName=<database>;integratedSecurity=true");
        add(DATABASE_NAME_HSQLDB_HYPER_SQL, "images/datastore-types/databases/hsqldb.png", "org.hsqldb.jdbcDriver",
                "http://repo1.maven.org/maven2/hsqldb/hsqldb/1.8.0.10/hsqldb-1.8.0.10.jar",
                "jdbc:hsqldb:hsql://<hostname>:9001/<database>", "jdbc:hsqldb:file:<path/to/database>");
        add(DATABASE_NAME_H2, "images/datastore-types/databases/h2.png", "org.h2.Driver",
                "http://repo1.maven.org/maven2/com/h2database/h2/1.3.162/h2-1.3.162.jar", "jdbc:h2:<path/to/database>");
        add(DATABASE_NAME_TERADATA, "images/datastore-types/databases/teradata.png", "com.teradata.jdbc.TeraDriver",
                null, "jdbc:teradata:<hostname>", "jdbc:teradata:<hostname>/database=<database>");
        add(DATABASE_NAME_PERVASIVE, "images/datastore-types/databases/pervasive.png", "com.pervasive.jdbc.v2.Driver",
                null, "jdbc:pervasive://<hostname>:1583/<datasource>");
        add(DATABASE_NAME_CUBRID, "images/datastore-types/databases/cubrid.png", "cubrid.jdbc.driver.CUBRIDDriver",
                "http://clojars.org/repo/cubrid/cubrid-jdbc/8.4.1.0564/cubrid-jdbc-8.4.1.0564.jar",
                "jdbc:cubrid:<hostname>:30000:<database>:::");
        add(DATABASE_NAME_LUCIDDB,
                "images/datastore-types/databases/luciddb.png",
                "org.luciddb.jdbc.LucidDbClientDriver",
                "http://repository.pentaho.org/artifactory/third-party/luciddb/LucidDbClient-minimal/0.9.4/LucidDbClient-minimal-0.9.4.jar",
                "jdbc:luciddb:http://<hostname>");
        add(DATABASE_NAME_PENTAHO_DATA_INTEGRATION, "images/datastore-types/databases/kettle.png",
                "org.pentaho.di.jdbc.KettleDriver", null, "jdbc:kettle:file://<filename>");
        add(DATABASE_NAME_JDBC_ODBC_BRIDGE, "images/datastore-types/databases/odbc.png",
                "sun.jdbc.odbc.JdbcOdbcDriver", null, "jdbc:odbc:<data-source-name>");
        add(DATABASE_NAME_HIVE, "images/datastore-types/databases/hive.png", "org.apache.hive.jdbc.HiveDriver",
                "http://repo1.maven.org/maven2/org/apache/hive/hive-jdbc/1.2.1/hive-jdbc-1.2.1.jar",
                "jdbc:hive://<hostname>:10000/<database>");

        Collections.sort(_databaseDrivers);
    }

    private final UserPreferences _userPreferences;

    @Inject
    public DatabaseDriverCatalog(UserPreferences userPreferences) {
        _userPreferences = userPreferences;
    }

    public List<DatabaseDriverDescriptor> getDatabaseDrivers() {
        return _databaseDrivers;
    }

    public List<DatabaseDriverDescriptor> getInstalledWorkingDatabaseDrivers() {
        return CollectionUtils.filter(_databaseDrivers, new Predicate<DatabaseDriverDescriptor>() {

            @Override
            public Boolean eval(DatabaseDriverDescriptor input) {
                if (getState(input) == DatabaseDriverState.INSTALLED_WORKING) {
                    return true;
                }
                return false;
            }
        });
    }

    private static void add(String databaseName, String iconImagePath, String driverClassName, String[] downloadUrls,
            String[] urlTemplates) {
        _databaseDrivers.add(new DatabaseDescriptorImpl(databaseName, iconImagePath, driverClassName, downloadUrls,
                urlTemplates));
    }

    private static void add(String databaseName, String iconImagePath, String driverClassName, String downloadUrl,
            String... urlTemplates) {
        String[] urls;
        if (downloadUrl == null) {
            urls = null;
        } else {
            urls = new String[] { downloadUrl };
        }
        _databaseDrivers.add(new DatabaseDescriptorImpl(databaseName, iconImagePath, driverClassName, urls,
                urlTemplates));
    }

    public DatabaseDriverState getState(DatabaseDriverDescriptor databaseDescriptor) {
        String driverClassName = databaseDescriptor.getDriverClassName();
        if (_userPreferences != null) {
            List<UserDatabaseDriver> drivers = _userPreferences.getDatabaseDrivers();
            for (UserDatabaseDriver userDatabaseDriver : drivers) {
                if (userDatabaseDriver.getDriverClassName().equals(driverClassName)) {
                    return userDatabaseDriver.getState();
                }
            }
        }
        try {
            Class.forName(driverClassName);
            return DatabaseDriverState.INSTALLED_WORKING;
        } catch (ClassNotFoundException e) {
            return DatabaseDriverState.NOT_INSTALLED;
        } catch (Exception e) {
            logger.warn("Unexpected error occurred while initializing driver class: " + driverClassName, e);
            return DatabaseDriverState.INSTALLED_NOT_WORKING;
        }
    }

    public static String getIconImagePath(DatabaseDriverDescriptor dd) {
        String iconImagePath = null;
        if (dd != null) {
            iconImagePath = dd.getIconImagePath();
        }
        if (iconImagePath == null) {
            iconImagePath = "images/model/datastore.png";
        }
        return iconImagePath;
    }

    public static DatabaseDriverDescriptor getDatabaseDriverByDriverDatabaseName(String databaseName) {
        if (databaseName == null) {
            return null;
        }
        for (DatabaseDriverDescriptor databaseDriver : _databaseDrivers) {
            if (databaseName.equals(databaseDriver.getDisplayName())) {
                return databaseDriver;
            }
        }
        return null;
    }

    public static DatabaseDriverDescriptor getDatabaseDriverByDriverClassName(String driverClass) {
        if (driverClass == null) {
            return null;
        }
        for (DatabaseDriverDescriptor databaseDriver : _databaseDrivers) {
            if (driverClass.equals(databaseDriver.getDriverClassName())) {
                return databaseDriver;
            }
        }
        return null;
    }

    public boolean isInstalled(String databaseName) {
        DatabaseDriverDescriptor databaseDriver = getDatabaseDriverByDriverDatabaseName(databaseName);
        if (databaseDriver == null) {
            return false;
        }
        return getState(databaseDriver) == DatabaseDriverState.INSTALLED_WORKING;
    }
    
    /**
     * Returns the descriptors of datastore types available in DataCleaner.
     */
    public List<DatastoreDescriptor> getAvailableDatastoreDescriptors() {
        List<DatastoreDescriptor> availableDatabaseDescriptors = new ArrayList<>();
        
        List<DatastoreDescriptor> manualDatastoreDescriptors = getManualDatastoreDescriptors();
        availableDatabaseDescriptors.addAll(manualDatastoreDescriptors);
        
        List<DatastoreDescriptor> driverBasedDatastoreDescriptors = getDriverBasedDatastoreDescriptors();
        availableDatabaseDescriptors.addAll(driverBasedDatastoreDescriptors);
        
        Set<String> alreadyAddedDatabaseNames = new HashSet<>();
        for (DatastoreDescriptor datastoreDescriptor : driverBasedDatastoreDescriptors) {
            alreadyAddedDatabaseNames.add(datastoreDescriptor.getName());
        }
        
        List<DatastoreDescriptor> otherDatastoreDescriptors = getOtherDatastoreDescriptors(alreadyAddedDatabaseNames);
        availableDatabaseDescriptors.addAll(otherDatastoreDescriptors);
        
        return availableDatabaseDescriptors;
    }
    
    private List<DatastoreDescriptor> getManualDatastoreDescriptors() {
        List<DatastoreDescriptor> datastoreDescriptors = new ArrayList<>();

        DatastoreDescriptor csvDatastoreDescriptor = new DatastoreDescriptorImpl("CSV file",
                "Comma-separated values (CSV) file (or file with other separators)", CsvDatastore.class);
        datastoreDescriptors.add(csvDatastoreDescriptor);

        DatastoreDescriptor excelDatastoreDescriptor = new DatastoreDescriptorImpl("Excel spreadsheet",
                "Microsoft Excel spreadsheet. Either .xls (97-2003) or .xlsx (2007+) format.", ExcelDatastore.class);
        datastoreDescriptors.add(excelDatastoreDescriptor);

        DatastoreDescriptor accessDatastoreDescriptor = new DatastoreDescriptorImpl("Access database",
                "Microsoft Access database file (.mdb).", AccessDatastore.class);
        datastoreDescriptors.add(accessDatastoreDescriptor);

        DatastoreDescriptor sasDatastoreDescriptor = new DatastoreDescriptorImpl("SAS library",
                "A directory of SAS library files (.sas7bdat).", SasDatastore.class);
        datastoreDescriptors.add(sasDatastoreDescriptor);

        DatastoreDescriptor dbaseDatastoreDescriptor = new DatastoreDescriptorImpl("DBase database",
                "DBase database file (.dbf)", DbaseDatastore.class);
        datastoreDescriptors.add(dbaseDatastoreDescriptor);

        DatastoreDescriptor fixedWidthDatastoreDescriptor = new DatastoreDescriptorImpl("Fixed width file",
                "Text file with fixed width values. Each value spans a fixed amount of text characters.",
                FixedWidthDatastore.class);
        datastoreDescriptors.add(fixedWidthDatastoreDescriptor);

        DatastoreDescriptor xmlDatastoreDescriptor = new DatastoreDescriptorImpl("XML file",
                "Extensible Markup Language file (.xml)", XmlDatastore.class);
        datastoreDescriptors.add(xmlDatastoreDescriptor);

        DatastoreDescriptor jsonDatastoreDescriptor = new DatastoreDescriptorImpl("JSON file",
                "JavaScript Object NOtation file (.json).", JsonDatastore.class);
        datastoreDescriptors.add(jsonDatastoreDescriptor);
        
        DatastoreDescriptor salesforceDatastoreDescriptor = new DatastoreDescriptorImpl("Salesforce.com",
                "Connect to a Salesforce.com account", SalesforceDatastore.class, Arrays.asList("Cloud service"));
        datastoreDescriptors.add(salesforceDatastoreDescriptor);
        
        DatastoreDescriptor sugarCrmDatastoreDescriptor = new DatastoreDescriptorImpl("SugarCRM",
                "Connect to a SugarCRM system", SugarCrmDatastore.class, Arrays.asList("Cloud service"));
        datastoreDescriptors.add(sugarCrmDatastoreDescriptor);

        DatastoreDescriptor mongoDbDatastoreDescriptor = new DatastoreDescriptorImpl("MongoDB database",
                "Connect to a MongoDB database", MongoDbDatastore.class, Arrays.asList("Database"));
        datastoreDescriptors.add(mongoDbDatastoreDescriptor);
        
        DatastoreDescriptor couchDbDatastoreDescriptor = new DatastoreDescriptorImpl("CouchDB database",
                "Connect to an Apache CouchDB database", CouchDbDatastore.class, Arrays.asList("Database"));
        datastoreDescriptors.add(couchDbDatastoreDescriptor);
        
        DatastoreDescriptor elasticSearchDatastoreDescriptor = new DatastoreDescriptorImpl("ElasticSearch index",
                "Connect to an ElasticSearch index", ElasticSearchDatastore.class, Arrays.asList("Database"));
        datastoreDescriptors.add(elasticSearchDatastoreDescriptor);
        
        DatastoreDescriptor cassandraDatastoreDescriptor = new DatastoreDescriptorImpl("Cassandra database",
                "Connect to an Apache Cassandra database", CassandraDatastore.class, Arrays.asList("Database"));
        datastoreDescriptors.add(cassandraDatastoreDescriptor);
        
        DatastoreDescriptor hbaseDatastoreDescriptor = new DatastoreDescriptorImpl("HBase database",
                "Connect to an Apache HBase database", HBaseDatastore.class, Arrays.asList("Database"));
        datastoreDescriptors.add(hbaseDatastoreDescriptor);
        
        return datastoreDescriptors;
    }
    
    private List<DatastoreDescriptor> getDriverBasedDatastoreDescriptors() {
        List<DatastoreDescriptor> datastoreDescriptors = new ArrayList<>();
        
        if (isInstalled(DatabaseDriverCatalog.DATABASE_NAME_HIVE)) {
            DatastoreDescriptor hiveDatastoreDescriptor = new DatastoreDescriptorImpl(DatabaseDriverCatalog.DATABASE_NAME_HIVE,
                    "Connect to an Apache Hive database", JdbcDatastore.class, Arrays.asList("Database"));
            datastoreDescriptors.add(hiveDatastoreDescriptor);
        }
        
        if (isInstalled(DatabaseDriverCatalog.DATABASE_NAME_MYSQL)) {
            DatastoreDescriptor mysqlDatastoreDescriptor = new DatastoreDescriptorImpl(DatabaseDriverCatalog.DATABASE_NAME_MYSQL,
                    "Connect to a MySQL database", JdbcDatastore.class, Arrays.asList("Database"));
            datastoreDescriptors.add(mysqlDatastoreDescriptor);
        }
        
        if (isInstalled(DatabaseDriverCatalog.DATABASE_NAME_POSTGRESQL)) {
            DatastoreDescriptor postgresqlDatastoreDescriptor = new DatastoreDescriptorImpl(DatabaseDriverCatalog.DATABASE_NAME_POSTGRESQL,
                    "Connect to a PostgreSQL database", JdbcDatastore.class, Arrays.asList("Database"));
            datastoreDescriptors.add(postgresqlDatastoreDescriptor);
        }

        if (isInstalled(DatabaseDriverCatalog.DATABASE_NAME_ORACLE)) {
            DatastoreDescriptor oracleDatastoreDescriptor = new DatastoreDescriptorImpl(DatabaseDriverCatalog.DATABASE_NAME_ORACLE,
                    "Connect to a Oracle database", JdbcDatastore.class, Arrays.asList("Database"));
            datastoreDescriptors.add(oracleDatastoreDescriptor);
        }

        if (isInstalled(DatabaseDriverCatalog.DATABASE_NAME_MICROSOFT_SQL_SERVER_JTDS)) {
            DatastoreDescriptor sqlServerDatastoreDescriptor = new DatastoreDescriptorImpl(DatabaseDriverCatalog.DATABASE_NAME_MICROSOFT_SQL_SERVER_JTDS,
                    "Connect to a Microsoft SQL Server database", JdbcDatastore.class, Arrays.asList("Database"));
            datastoreDescriptors.add(sqlServerDatastoreDescriptor);
        }

        return datastoreDescriptors;
    }
    
    private List<DatastoreDescriptor> getOtherDatastoreDescriptors(Set<String> alreadyAddedDatabaseNames) {
        List<DatastoreDescriptor> datastoreDescriptors = new ArrayList<>();
        
        final List<DatabaseDriverDescriptor> databaseDrivers = getInstalledWorkingDatabaseDrivers();
        for (DatabaseDriverDescriptor databaseDriver : databaseDrivers) {
            final String databaseName = databaseDriver.getDisplayName();
            if (!alreadyAddedDatabaseNames.contains(databaseName)) {
                DatastoreDescriptor jdbcDatastoreDescriptor = new DatastoreDescriptorImpl(databaseName,
                        "Connect to " + databaseName, JdbcDatastore.class);
                datastoreDescriptors.add(jdbcDatastoreDescriptor);
            }
        }

        // custom/other jdbc connection
        final String databaseName = "Other database";
        if (!alreadyAddedDatabaseNames.contains(databaseName)) {
            DatastoreDescriptor otherDatastoreDescriptor = new DatastoreDescriptorImpl(databaseName,
                    "Connect to other database", JdbcDatastore.class);
            datastoreDescriptors.add(otherDatastoreDescriptor);
        }

        // composite datastore
        final String compositeDatastoreName = "Composite datastore";
        if (!alreadyAddedDatabaseNames.contains(compositeDatastoreName)) {
            DatastoreDescriptor compositeDatastoreDescriptor = new DatastoreDescriptorImpl(compositeDatastoreName,
                    "Create composite datastore", CompositeDatastore.class);
            datastoreDescriptors.add(compositeDatastoreDescriptor);
        }

        
        return datastoreDescriptors;
    }

}
