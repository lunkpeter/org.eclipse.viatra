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
 * Interface that defines the base functions of the events being sent between individual
 * <link>ITransformationStep</link> objects. These events can a parameter defined by the <ParameterType> template
 * parameter.
 * 
 * @author Peter Lunk
 *
 * @param <ParameterType> Template parameter which defines the type of the given event's parameter.
 */
public interface IEvent<ParameterType extends Object> {
    /**
     * Returns the parameter of the <link>IEvent</link> object.
     * @return The parameter
     */
    public ParameterType getParameter();

    /**
     * Sets the parameter of the <link>IEvent</link> object.
     *  
     * @param parameter New value of the parameter.
     */
    public void setParameter(ParameterType parameter);
}
