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
 * Provides an interface for channels, that are specifically designed as listening channels. It wraps an
 * <link>IChannel</link> and an <link>IEventProcessor</link>. The <link>IEventProcessor</link> is used for processing
 * the events contained by the <link>IChannel</link>. Unlike the <link>IEventProcessor</link> interface
 * <link>IListeningChannel</link> is not parametric, therefore any typed processors can be added to one implementation.
 * 
 * Listening channels are contained by an <link>ITransformationStep</link> object, to which they have parent reference.
 * 
 * @author Peter Lunk
 *
 */
public interface IListeningChannel {

    public IChannel getChannel();
    public void setChannel(IChannel channel);

    public String getName();
    public void setName(String name);

    public ITransformationStep getParent();
    public void setParent(ITransformationStep parent);

    public int getPriority();
    public void setPriority(String priority);

    public IEventProcessor<? extends Object, ? extends IEvent<? extends Object>> getProcessor();
    public void setProcessor(IEventProcessor<? extends Object, ? extends IEvent<? extends Object>> processor);

    /**
     * Processes the next event contained by the wrapped <link>IChannel</link> object that can be processed by the
     * wrapped <link>IEventProcessor</link>
     * 
     * @return
     */
    public IEvent<? extends Object> processNextEvent();

    /**
     * Checks if there are any events on the wrapped <link>IChannel</link> that can be processed by the
     * wrapped <link>IEventProcessor</link>
     * @return
     */
    public boolean isTriggered();
}
