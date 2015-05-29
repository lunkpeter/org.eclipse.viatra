/*******************************************************************************
 * Copyright (c) 2004-2015, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Peter Lunk - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.emf.mwe2integration.mwe2impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.eclipse.viatra.emf.mwe2integration.IChannel;
import org.eclipse.viatra.emf.mwe2integration.IEvent;

/**
 * MWE 2 based IChannel implementation that stores its contained events in a BlockingQueue object.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2Channel implements IChannel{

    protected BlockingQueue<IEvent<? extends Object>> events = new ArrayBlockingQueue<IEvent<? extends Object>>(10);

    @Override
    public void registerEvent(IEvent<? extends Object> event) throws InterruptedException {
        events.add(event);
    }

    @Override
    public BlockingQueue<IEvent<? extends Object>> getEvents() {
        return events;
    }
    
    
   
}
