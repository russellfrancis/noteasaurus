package com.metrosix.noteasaurus.database;

import org.picocontainer.Startable;

/**
 * This interface defines our interface with the database, we can start and stop the database this
 * is needed primarily for embedded database like derby or h2.
 * 
 * @author Russell Francis (russ@metro-six.com)
 */
public interface DatabaseManager extends Startable {
}
