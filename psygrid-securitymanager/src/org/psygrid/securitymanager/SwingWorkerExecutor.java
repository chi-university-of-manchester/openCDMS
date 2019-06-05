/*
Copyright (c) 2006-2008, The University of Manchester, UK.

This file is part of PsyGrid.

PsyGrid is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as 
published by the Free Software Foundation, either version 3 of 
the License, or (at your option) any later version.

PsyGrid is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public 
License along with PsyGrid.  If not, see <http://www.gnu.org/licenses/>.
*/


package org.psygrid.securitymanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jdesktop.swingworker.SwingWorker;

public class SwingWorkerExecutor {
    private static final SwingWorkerExecutor INSTANCE = new SwingWorkerExecutor();
    
    private ExecutorService executorService;
    
    private SwingWorkerExecutor() {
        final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
        executorService = Executors.newFixedThreadPool(1, new ThreadFactory() {
            public final Thread newThread(Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("SwingWorkerExecutor-" + thread.getName()); //$NON-NLS-1$
                return thread;
            }
        });
    }
    
    public static SwingWorkerExecutor getInstance() {
        return INSTANCE;
    }
    
    public void execute(SwingWorker<?, ?> worker) {
        executorService.submit(worker);
    }

    public void dispose() {
        executorService.shutdown();
    }
}
