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

import org.eclipse.viatra.emf.mwe2integration.IEvent;
import org.eclipse.viatra.emf.mwe2integration.IEventProcessor;
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep;

/**
 * Event processor that processes MWE2 Control events. This processor is the default processor
 * of every newly instantiated ITargetChannel object.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2ControlEventProcessor implements IEventProcessor<Object, MWE2ControlEvent> {
    protected ITransformationStep parent;

    public ITransformationStep getParent() {
        return parent;
    }

    public void setParent(ITransformationStep parent) {
        this.parent = parent;
    }

    @Override
    public MWE2ControlEvent getNextEvent(BlockingQueue<? extends IEvent<? extends Object>> events) {
        IEvent<? extends Object> event = null;
        while(event == null && !events.isEmpty()){
            event = events.poll();
            if(event instanceof MWE2ControlEvent){
                return (MWE2ControlEvent) event;
            }
        }
        return null;
    }

    @Override
    public void processEvent(IEvent<? extends Object> nextEvent) {
        //Control events do not have any particular function besides from activating the node itself.
        
    }
    
    @Override
    public boolean hasNextEvent(BlockingQueue<? extends IEvent<? extends Object>> events) {
        for (IEvent<? extends Object> event : events) {
            if(event instanceof MWE2ControlEvent){
                return true;
            }
        }
        return false;
    }

    


}
