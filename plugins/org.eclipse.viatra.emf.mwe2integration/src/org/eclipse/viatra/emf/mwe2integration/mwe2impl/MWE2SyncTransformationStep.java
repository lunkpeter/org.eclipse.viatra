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
import java.util.List;

import org.eclipse.viatra.emf.mwe2integration.IEvent;
import org.eclipse.viatra.emf.mwe2integration.IListeningChannel;

/**
 * Abstract child class of the MWE2TransformationStep. The main difference is, that this step will be activated if ALL
 * of its listening channels contain processable events. If the step is activated, it removes an event from every
 * listening channel, and executes the user defined functionality.
 * 
 * @author Peter Lunk
 *
 */
public abstract class MWE2SyncTransformationStep extends MWE2TransformationStep {

    @Override
    public boolean canRun() {
        boolean ret = true;
        for (IListeningChannel channel : getListeningChannels()) {
            if (channel.getChannel().getEvents().isEmpty()) {
                ret = false;
            }
        }
        return ret;
    }

    protected List<IEvent<? extends Object>> processEvents() {
        List<IEvent<? extends Object>> ret = new ArrayList<IEvent<? extends Object>>();
        for (IListeningChannel listeningChannel : getListeningChannels()) {
            if (listeningChannel != null) {
                IEvent<? extends Object> nextEvent = listeningChannel.processNextEvent();
                if (nextEvent != null) {
                    ret.add(nextEvent);
                }
            }
        }
        return ret;
    }

}
