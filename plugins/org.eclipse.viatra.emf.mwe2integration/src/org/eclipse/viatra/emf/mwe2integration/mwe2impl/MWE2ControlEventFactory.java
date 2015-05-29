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

import org.eclipse.viatra.emf.mwe2integration.IEventFactory;

/**
 * Event factory that is responsible for the instantiation of MWE2 Control events. This factory is the default factory
 * of every newly instantiated ITargetChannel object.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2ControlEventFactory implements IEventFactory<Object, MWE2ControlEvent> {

    @Override
    public MWE2ControlEvent createEvent(Object parameter) {
        return createEvent();
    }

    @Override
    public MWE2ControlEvent createEvent() {
        return new MWE2ControlEvent();
    }

    @Override
    public boolean isValidParameter(Object parameter) {
        return true;
    }

}
