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

import org.eclipse.viatra.emf.mwe2integration.IEvent;

/**
 * MWE2 Control event implementation. It has no parameters, its only purpose is to enable the transformation step it is
 * being sent to. This is the default event type, every newly created IListeningChannel and ITargetChannel object will
 * use this event by default. Naturally, this can be overridden by the addition of custom factories and processors.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2ControlEvent implements IEvent<Object> {

    @Override
    public Object getParameter() {
        return null;
    }

    @Override
    public void setParameter(Object parameter) {

    }

}
