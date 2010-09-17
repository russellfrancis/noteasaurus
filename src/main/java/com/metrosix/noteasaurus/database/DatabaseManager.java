package com.metrosix.noteasaurus.database;

import org.picocontainer.Startable;

/**
 * This interface defines our interface with the database, we can start and stop the database this
 * is needed primarily for embedded database like derby or h2.
 * 
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DatabaseManager.java 247 2010-08-07 23:15:10Z adam $
 */
public interface DatabaseManager extends Startable {
}
