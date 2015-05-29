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

import org.eclipse.viatra.emf.mwe2integration.IChannel;
import org.eclipse.viatra.emf.mwe2integration.IEvent;
import org.eclipse.viatra.emf.mwe2integration.IEventFactory;
import org.eclipse.viatra.emf.mwe2integration.ITargetChannel;

/**
 * MWE 2 based ITargetChannel implementation. As specified in the ITargetChannel interface, instances of this class
 * contain an IChannel and an IEventFactory object. The event factory is responsible for adding events to the channel.
 * The TargetChannel class provides an easy to use interface for adding events to given channels, while also enabling
 * the user to specify the soft type of the given channel, via adding a certain event factory.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2TargetChannel implements ITargetChannel {

    private IChannel channel;
    private String name;
    private IEventFactory<? extends Object, ? extends IEvent<? extends Object>> factory;

    public MWE2TargetChannel() {
        factory = new MWE2ControlEventFactory();
    }
    
    @Override
    public IEvent<? extends Object> createEvent(Object parameter) {
        if (factory.isValidParameter(parameter)) {
            IEvent<? extends Object> event = factory.createEvent(parameter);
            try {
                channel.registerEvent(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return event;
        }
        return null;

    }

    @Override
    public IEvent<? extends Object> createEvent() {
        return createEvent("");
    }

    @Override
    public IChannel getChannel() {
        return channel;
    }

    @Override
    public void setChannel(IChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IEventFactory<? extends Object, ? extends IEvent<? extends Object>> getFactory() {
        return factory;
    }

    @Override
    public void setFactory(IEventFactory<? extends Object, ? extends IEvent<? extends Object>> factory) {
        this.factory = factory;

    }
}
