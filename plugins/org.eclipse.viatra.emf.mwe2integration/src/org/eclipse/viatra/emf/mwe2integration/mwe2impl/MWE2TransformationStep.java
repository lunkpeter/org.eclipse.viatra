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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.emf.mwe2integration.IEvent;
import org.eclipse.viatra.emf.mwe2integration.IListeningChannel;
import org.eclipse.viatra.emf.mwe2integration.ITargetChannel;
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Abstract ITransformationStep implementation, that acts as a base class for user defined transformation steps. It
 * incorporates the following features: - In its run() method, it periodically checks if one of its listening channels
 * contains an event. - If events are contained it calls the execute() method, which has to be overridden by the user. -
 * It also provides helper methods for processing events, and sending them.
 * 
 * If any of the specified listening channels contain an event, this transformation step will trigger, and consume the
 * given event.
 * 
 * @author Peter Lunk
 *
 */
public abstract class MWE2TransformationStep implements ITransformationStep {
    protected ListMultimap<Integer, IListeningChannel> listeningChannels = ArrayListMultimap.create();
    protected List<ITargetChannel> targetChannels = new ArrayList<ITargetChannel>();

    protected boolean isRunning = true;
    protected IWorkflowContext context;

    public void addListeningChannel(IListeningChannel channel) {
        listeningChannels.put(channel.getPriority(), channel);
        channel.setParent(this);
    }

    public List<IListeningChannel> getListeningChannels(Integer priority) {
        return listeningChannels.get(priority);
    }

    public List<IListeningChannel> getListeningChannels() {
        List<IListeningChannel> ret = new ArrayList<IListeningChannel>();
        ret.addAll(listeningChannels.values());
        return ret;
    }

    public void addTargetChannel(ITargetChannel channel) {
        targetChannels.add(channel);
    }

    public List<ITargetChannel> getTargetChannels() {
        return targetChannels;
    }

    public IListeningChannel getListeningChannel(String name) {
        IListeningChannel ret = null;
        for (IListeningChannel channel : getListeningChannels()) {
            if (channel.getName().equals(name)) {
                ret = channel;
            }
        }
        return ret;
    }

    public ITargetChannel getTargetChannel(String name) {
        ITargetChannel ret = null;
        for (ITargetChannel channel : targetChannels) {
            if (channel.getName().equals(name)) {
                ret = channel;
            }
        }
        return ret;
    }

    @Override
    public void run() {
        while (isRunning) {
            if (canRun()) {
                execute();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if the transformation step's listening channels contain any events. If the do, it returns true.
     * 
     * @return
     */
    protected boolean canRun() {
        boolean ret = false;
        for (IListeningChannel channel : getListeningChannels()) {
            if (!channel.getChannel().getEvents().isEmpty()) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * This method selects the next event to be processed, removes it from its channel, and executes its corresponding
     * executor. Note, that this method uses the priorities of the listening channels, the highest number being the
     * highest priority.
     * 
     * @return
     */
    protected IEvent<? extends Object> processNextEvent() {
        IListeningChannel listeningChannel = getNextChannel();
        if (listeningChannel != null) {
            IEvent<? extends Object> nextEvent = listeningChannel.processNextEvent();
            return nextEvent;
        }
        return null;
    }

    /**
     * Sends an event with the given parameter to every target channel. This method uses the event creation methods
     * defined by ITargetChannel, which use the wrapped IEventFactory object to create and add the event to the given
     * IChannel.
     * 
     * @param parameter
     */
    protected void sendEventToAllTargets(Object parameter) {
        for (ITargetChannel channel : targetChannels) {
            channel.createEvent(parameter);
        }
    }

    /**
     * Sends an event with no parameter to every target channel. This method uses the event creation methods defined by
     * ITargetChannel, which use the wrapped IEventFactory object to create and add the event to the given IChannel.
     * 
     * @param parameter
     */
    protected void sendEventToAllTargets() {
        for (ITargetChannel channel : targetChannels) {
            channel.createEvent();
        }
    }

    /**
     * REturns the next channel to be processed based on channel priorities and if they contain any processable events.
     * 
     * @return
     */
    protected IListeningChannel getNextChannel() {
        IListeningChannel listeningChannel = null;
        for (IListeningChannel channel : getNextPriority()) {
            if (!channel.getChannel().getEvents().isEmpty()) {
                listeningChannel = channel;
                break;
            }
        }
        return listeningChannel;
    }

    /**
     * REturns the highest priority bucket that contains any processable events.
     * 
     * @return
     */
    protected List<IListeningChannel> getNextPriority() {
        List<Integer> keySet = new ArrayList<Integer>();
        keySet.addAll(listeningChannels.keySet());
        Collections.sort(keySet, new Comparator<Integer>() {
            @Override
            public int compare(Integer arg0, Integer arg1) {
                if (arg0 < arg1) {
                    return 1;
                } else if (arg0 > arg1) {
                    return -1;
                }
                return 0;
            }
        });
        for (Integer integer : keySet) {
            if (!(isChannelListEmpty(listeningChannels.get(integer)))) {
                return listeningChannels.get(integer);
            }
        }
        return listeningChannels.get(0);
    }

    /**
     * Checks if the given List<IListeningChannel> contains any processable events.
     * 
     * @param channelList
     * @return
     */
    private boolean isChannelListEmpty(List<IListeningChannel> channelList) {
        boolean ret = true;
        for (IListeningChannel listeningChannel : channelList) {
            if (listeningChannel.isTriggered()) {
                ret = false;
            }
        }
        return ret;
    }

}
