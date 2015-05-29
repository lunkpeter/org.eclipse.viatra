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
package org.eclipse.viatra.emf.mwe2integration;

import java.util.concurrent.BlockingQueue;

/**
 * Interface that defines the base functions of a channel object, which is used in the communication between individual
 * <link>ITransformationStep</link> objects. These channel objects can contain various typed events. The creation and
 * procession of these events is done by <link>IEventFactory</link> and <link>IEventProcessor</link> objects.
 * 
 * @author Peter Lunk
 *
 */
public interface IChannel {
    /**
     * Adds the given <link>IEvent</link> object to the channel.
     * 
     * @param event The <link>IEvent<link> to be added to the channel.
     * @throws InterruptedException Throws an <link>InterruptedException</link> if the addition of the event is disrupted.
     */
    public void registerEvent(IEvent<? extends Object> event) throws InterruptedException;

    /**
     * Returns the contained <link>IEvent<link> objects
     * @return <link>BlockingQueue</link> that contains the events present on this channel.
     */
    public BlockingQueue<IEvent<? extends Object>> getEvents();
}
