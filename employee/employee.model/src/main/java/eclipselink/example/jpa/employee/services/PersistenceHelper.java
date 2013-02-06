/*******************************************************************************
 * Copyright (c) 2010-2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  dclarke - EclipseLink 2.3 - MySports Demo Bug 344608
 ******************************************************************************/
package eclipselink.example.jpa.employee.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import eclipselink.example.jpa.employee.persistence.SQLCaptureSessionLog;
import eclipselink.example.jpa.employee.persistence.SQLCaptureSessionLog.SessionLogHandler;
import eclipselink.example.jpa.employee.persistence.SQLTrace;

/**
 * This helper bean will cause the container to weave the persistence unit
 * through its injection at application startup. It will also server as an
 * interface to get the SQL trace for a given application operation.
 * <p>
 * Since this example uses the persistence unit through the application
 * bootstrap API the container will not instrument/weave the entity classes.
 * This class is ONLY required in the application to force the weaving to occur
 * and the {@link EntityManagerFactory} within here is never used.
 * 
 * @author dclarke
 * @since EclipseLink 2.4.2
 */
public class PersistenceHelper {

    protected static SessionLogHandler getHandler(EntityManager em) {
        return SQLCaptureSessionLog.getHandler(em);
    }

    public static SQLTrace startSQLTrace(EntityManager em) {
        return getHandler(em).start();
    }

    public static SQLTrace endSQLTrace(EntityManager em) {
        return getHandler(em).stop();
    }

}
