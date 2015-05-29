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

/**
 * Provides an interface for channels, that are specifically designed as target channels. It wraps an
 * <link>IChannel</link> and an <link>IEventFactory</link>. The <link>IEventFactory</link> is used for creating
 * the events contained by the <link>IChannel</link>. Unlike the <link>IEventFactory</link> interface
 * <link>IListeningChannel</link> is not parametric, therefore any typed factories can be added to one implementation.
 * 
 * Target channels are contained by an <link>ITransformationStep</link> object, to which they have parent reference.
 * 
 * @author Peter Lunk
 *
 */
public interface ITargetChannel {
    public IChannel getChannel();
    public void setChannel(IChannel channel);
    
    public String getName();
    public void setName(String name);
    
    public IEventFactory<? extends Object, ? extends IEvent<? extends Object>> getFactory();
    public void setFactory(IEventFactory<? extends Object, ? extends IEvent<? extends Object>> factory);
    
    /**
     * Creates a new event with the given parameter.
     * @param parameter
     * @return The created event.
     */
    public IEvent<? extends Object> createEvent(Object parameter);
    
    /**
     * Creates a new event without any parameters.
     * @return
     */
    public IEvent<? extends Object> createEvent();
}
