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
 * Interface that defines the base functions of an event processor. The event processors are responsible for processing
 * certain typed events. The type of these events is specified in the <Event> template parameter, while the parameter
 * type of these events is specified in the <ParameterType> template parameter.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType>
 * @param <Event>
 */
public interface IEventProcessor<ParameterType extends Object, Event extends IEvent<ParameterType>> {

    /**
     * Returns the parent <link>ITransformationStep</link> object of this particular event processor.
     * 
     * @return
     */
    public ITransformationStep getParent();

    /**
     * Sets the parent reference of the event processor to the given <link>ITransformationStep</link>
     * 
     * @param parent
     */
    public void setParent(ITransformationStep parent);

    /**
     * Retrieves the next event the <link>IEventProcessor</link> can process, from a given list of generic events.
     * Returns an <Event> typed event
     * 
     * @param events
     *            List of generic events
     * @return The next event the processor can process.
     */
    public Event getNextEvent(BlockingQueue<? extends IEvent<? extends Object>> events);

    /**
     * Checks if the given list has any events that the <link>IEventProcessor</link> can process.
     * 
     * @param events
     *            List of generic events
     * @return True if the list contains any events to be processed, false otherwise.
     */
    public boolean hasNextEvent(BlockingQueue<? extends IEvent<? extends Object>> events);

    /**
     * The <link>IEventProcessor</link> processes the given <link>IEvent</link>. The additional functionality of the
     * processor is added here.
     * 
     * @param nextEvent
     */
    public void processEvent(IEvent<? extends Object> nextEvent);
}
