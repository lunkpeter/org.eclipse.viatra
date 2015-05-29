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

import java.util.concurrent.BlockingQueue;

import org.eclipse.viatra.emf.mwe2integration.IChannel;
import org.eclipse.viatra.emf.mwe2integration.IEvent;
import org.eclipse.viatra.emf.mwe2integration.IEventProcessor;
import org.eclipse.viatra.emf.mwe2integration.IListeningChannel;
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep;

/**
 * MWE2 based listening channel implementation. It contains an IChannel and an IEventProcessor implementation. The event
 * processor is used for processing the events contained by the channel. This IListeningChannel implementation also
 * features name and priority properties, which are used for directly referencing the channels and help in defining an order
 * in which conflicting channels can be processed.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2ListeningChannel implements IListeningChannel {
    private ITransformationStep parent;
    private IChannel channel;
    private String name;
    private int priority = 0;
    private IEventProcessor<? extends Object, ? extends IEvent<? extends Object>> processor;

    public MWE2ListeningChannel() {
        processor = new MWE2ControlEventProcessor();
    }

    @Override
    public IEvent<? extends Object> processNextEvent() {
        BlockingQueue<IEvent<? extends Object>> events = channel.getEvents();
        IEvent<? extends Object> nextEvent = processor.getNextEvent(events);
        if (nextEvent != null) {
            processor.processEvent(nextEvent);
        }
        return nextEvent;
    }
    
    public ITransformationStep getParent() {
        return parent;
    }

    public void setParent(ITransformationStep parent) {
        this.parent = parent;
        if (processor != null) {
            processor.setParent(parent);
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(String priority) {
        try {
            this.priority = Integer.parseInt(priority);
        } catch (Exception e) {
            this.priority = 0;
            e.printStackTrace();
        }
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
    public IEventProcessor<? extends Object, ? extends IEvent<? extends Object>> getProcessor() {
        return processor;
    }

    @Override
    public void setProcessor(IEventProcessor<? extends Object, ? extends IEvent<? extends Object>> processor) {
        this.processor = processor;
        if (parent != null) {
            this.processor.setParent(parent);
        }

    }

    @Override
    public boolean isTriggered() {
        return processor.hasNextEvent(channel.getEvents());
    }
}
