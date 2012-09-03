/* 
 * Copyright (c) 2010-2012 Thiago T. Sá
 * 
 * This file is part of CloudReports.
 *
 * CloudReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CloudReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For more information about your rights as a user of CloudReports,
 * refer to the LICENSE file or see <http://www.gnu.org/licenses/>.
 */

package cloudreports.database;

import cloudreports.gui.Dialog;
import cloudreports.models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Provides a set of operations related to database creation and management.
 * 
 * @author      Thiago T. Sá
 * @since       1.0
 */
public class Database {
    
    /** The database connection. */
    private static Connection connection;
    
    /** 
     * Establishes a connection with the current active database.
     *
     * @see     #connection
     * @since   1.0
     */    
    private static void establishConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:db/" + HibernateUtil.getActiveDatabase() + ".cre");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Creates a brand new database.
     *
     * @see     #connection
     * @since   1.0
     */       
    public static void createDatabase() {
        try {
            establishConnection();
            Statement stat = connection.createStatement();
            createCustomersTable(stat);
            createDatacentersTable(stat);
            createHostsTable(stat);
            createSanStorageTable(stat);
            createUtilizationProfilesTable(stat);
            createVirtualMachinesTable(stat);            
            createNetworkMapTable(stat);            
            createReportDataTable(stat);            
            createMigrationsTable(stat);            
            createSettingsTable(stat);
            createRandomPoolTable(stat);            
            insertDefaultSettingsValues(stat);
        } catch (Exception ex) {
            Dialog.showErrorMessage(new JFrame(), "An error occurred while creating the database.");
            System.exit(0);
        }
        finally {
        	closeConnection(connection);
        }
    }

    /** 
     * Closes the connection with the current active database.
     *
     * @see     #connection
     * @since   1.1
     */ 
    private static void closeConnection(Connection connection) {
    	try {
			connection.close();
		} catch (SQLException ex) {
			Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
    

	/** 
     * Inserts default values of application's settings.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void insertDefaultSettingsValues(Statement stat)
			throws SQLException {
		stat.executeUpdate("INSERT INTO Settings VALUES (0,'Randomness','0')");
		stat.executeUpdate("INSERT INTO Settings VALUES (1,'NumberOfSimulations','1')");
		stat.executeUpdate("INSERT INTO Settings VALUES (2,'CurrentSimulation','1')");
		stat.executeUpdate("INSERT INTO Settings VALUES (3,'EnableMailNotification','false')");
		stat.executeUpdate("INSERT INTO Settings VALUES (4,'TimeToSimulate','60')");
		stat.executeUpdate("INSERT INTO Settings VALUES (5,'HtmlReports','true')");
		stat.executeUpdate("INSERT INTO Settings VALUES (6,'RawDataReports','true')");
	}

    /** 
     * Creates the RandomPool table
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createRandomPoolTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE RandomPool ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Value REAL NOT NULL"
		                        + ");");
		
		stat.executeUpdate("INSERT INTO RandomPool VALUES (1," + Math.random() +")");
	}

    /** 
     * Creates the Settings table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createSettingsTable(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE TABLE Settings ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Name TEXT NOT NULL,"
		                        + "Value TEXT NOT NULL"
		                        + ");");
	}

    /** 
     * Creates the Migrations table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createMigrationsTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE Migrations ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "SimulationId INTEGER NOT NULL,"
		                        + "Description TEXT NOT NULL,"
		                        + "Time REAL NOT NULL,"
		                        + "TargetHost TEXT NOT NULL,"
		                        + "SourceHost TEXT NOT NULL,"
		                        + "Vm TEXT NOT NULL,"
		                        + "DatacenterName TEXT NOT NULL,"
		                        + "SourceHostCpuUtilization REAL NOT NULL,"
		                        + "SourceHostRamUtilization REAL NOT NULL,"
		                        + "SourceHostPowerConsumption REAL NOT NULL,"
		                        + "TargetHostCpuUtilization REAL NOT NULL,"
		                        + "TargetHostRamUtilization REAL NOT NULL,"
		                        + "TargetHostPowerConsumption REAL NOT NULL"
		                        + ");");
	}

    /** 
     * Creates the ReportData table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createReportDataTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE ReportData ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "SimulationId INTEGER NOT NULL,"
		                        + "Type TEXT NOT NULL,"
		                        + "DatacenterName TEXT,"
		                        + "CustomerName TEXT,"
		                        + "VmId INTEGER,"                    
		                        + "HostId INTEGER,"
		                        + "Time REAL,"
		                        + "Amount REAL"                  
		                        + ");");
	}

    /** 
     * Creates the NetworkMap table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createNetworkMapTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE NetworkMap ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Source TEXT NOT NULL,"
		                        + "Destination TEXT NOT NULL,"
		                        + "Bandwidth REAL NOT NULL,"
		                        + "Latency REAL NOT NULL"
		                        + ");");
	}

    /** 
     * Creates the VirtualMachines table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createVirtualMachinesTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE VirtualMachines ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Indx INTEGER,"
		                        + "Size INTEGER NOT NULL,"
		                        + "PesNumber INTEGER NOT NULL,"
		                        + "MIPS REAL NOT NULL,"
		                        + "Ram INTEGER NOT NULL,"
		                        + "Bandwidth INTEGER NOT NULL,"
		                        + "Priority INTEGER NOT NULL,"
		                        + "Hypervisor TEXT NOT NULL,"
		                        + "SchedulingPolicy TEXT NOT NULL,"
		                        + "Amount INTEGER NOT NULL,"
		                        + "CustomerId INTEGER"
		                        + ");");
	}

    /** 
     * Creates the UtilizationProfile table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createUtilizationProfilesTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE UtilizationProfiles ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "BrokerPolicy TEXT NOT NULL,"
		                        + "NumOfCloudlets INTEGER NOT NULL,"
		                        + "Length INTEGER NOT NULL,"
		                        + "FileSize INTEGER NOT NULL,"
		                        + "OutputSize INTEGER NOT NULL,"
		                        + "CloudletsPesNumber INTEGER NOT NULL,"
		                        + "CPUUtilizationModel TEXT NOT NULL,"
		                        + "RamUtilizationModel TEXT NOT NULL,"
		                        + "BwUtilizationModel TEXT NOT NULL,"
		                        + "TimeToSend REAL NOT NULL"
		                        + ");");
	}

    /** 
     * Creates the SanStorage table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createSanStorageTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE SanStorage ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Indx INTEGER,"
		                        + "Name TEXT NOT NULL,"
		                        + "Capacity REAL NOT NULL,"
		                        + "Bandwidth REAL NOT NULL,"
		                        + "NetworkLatency REAL NOT NULL,"
		                        + "DatacenterId INTEGER"
		                        + ");");
	}

    /** 
     * Creates the Hosts table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createHostsTable(Statement stat) throws SQLException {
		stat.executeUpdate("CREATE TABLE Hosts ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Indx INTEGER,"
		                        + "VmScheduler TEXT NOT NULL,"
		                        + "NumOfPes INTEGER NOT NULL,"
		                        + "MipsPerPe REAL NOT NULL,"
		                        + "MaxPower REAL NOT NULL,"
		                        + "StaticPowerPercent REAL NOT NULL,"
		                        + "PowerModel TEXT NOT NULL,"
		                        + "Ram INTEGER NOT NULL,"
		                        + "RamProvisioner TEXT NOT NULL,"
		                        + "Bandwidth INTEGER NOT NULL,"
		                        + "BwProvisioner TEXT NOT NULL,"
		                        + "Amount INTEGER NOT NULL,"
		                        + "Storage INTEGER NOT NULL,"
		                        + "PeProvisioner TEXT NOT NULL,"
		                        + "DatacenterId INTEGER"
		                        + ");");
	}

    /** 
     * Creates the Datacenters table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createDatacentersTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE Datacenters ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Name TEXT NOT NULL,"
		                        + "Architecture TEXT NOT NULL,"
		                        + "OS TEXT NOT NULL,"
		                        + "Hypervisor TEXT NOT NULL,"
		                        + "TimeZone REAL NOT NULL DEFAULT (0),"
		                        + "AllocationPolicy TEXT NOT NULL,"
		                        + "VmMigration INTEGER NOT NULL,"
		                        + "CostPerSec REAL NOT NULL,"
		                        + "CostPerMem REAL NOT NULL,"
		                        + "CostPerStorage REAL NOT NULL,"
		                        + "CostPerBw REAL NOT NULL,"
		                        + "UpperUtilizationThreshold REAL NOT NULL,"
		                        + "LowerUtilizationThreshold REAL NOT NULL,"
		                        + "SchedulingInterval REAL NOT NULL,"
		                        + "MonitoringInterval REAL NOT NULL"
		                        + ");");
	}

    /** 
     * Creates the Customers table.
     *
     * @see     #connection
     * @since   1.1
     */     
	private static void createCustomersTable(Statement stat)
			throws SQLException {
		stat.executeUpdate("CREATE TABLE Customers ("
		                        + "Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
		                        + "Name TEXT NOT NULL"
		                        + ");");
	}
    
    /** 
     * Removes all reports related data from the database.
     * It cleans the database after the report files have been generated.
     *
     * @see     #connection
     * @see     ReportData
     * @see     Migration
     * @since   1.0
     */        
    public static void cleanTempReport() {
        Session session = HibernateUtil.getSession();        
        try {
            session.beginTransaction();
            session.createQuery("DELETE FROM ReportData").executeUpdate();
            session.createQuery("DELETE FROM Migration").executeUpdate();
            session.getTransaction().commit();
        }
        catch (HibernateException ex) {
            Dialog.showErrorMessage(new JFrame(), "An error occurred while cleaning temporary report entries.");
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            HibernateUtil.closeSession(session);
        }
    }

}
