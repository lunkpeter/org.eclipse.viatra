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
 * Interface that defines the base functions of an event factory. These factories are responsible for the creation of
 * certain typed events. The type of these events is specified in the <Event> template parameter, while the parameter
 * type of these events is specified in the <ParameterType> template parameter.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType> Type of the parameter of the events to be created.
 * @param <Event> Type of the events to be created.
 */
public interface IEventFactory<ParameterType extends Object, Event extends IEvent<ParameterType>> {

    /**
     * Creates an event of the type specified by the <Event> parameter.
     * 
     * @param parameter Parameter of the event to be created.
     * @return The created event.
     */
    public Event createEvent(Object parameter);

    /**
     * Creates an event without a parameter.
     * 
     * @return The created event.
     */
    public Event createEvent();

    /**
     * Checks if the given <link>Object</link> is an eligible parameter.
     * 
     * @param parameter <link>Object</link> to be checked.
     * @return Validity of the parameter
     */
    public boolean isValidParameter(Object parameter);
}
