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
 * This interface contains the functions of a transformation chain. Transformation chains contain transformation steps
 * that can be executed in either a batch or event driven manner. These transformation steps communicate among each
 * other using different specific channels and events. These similar events are used to inform the transformation chain,
 * that the all of the steps have finished their execution. This way event driven, incremental transformation steps can
 * be added to an originally batch execution based MWE workflow.
 * 
 * @author Peter Lunk
 *
 */
public interface ITransformationChain {

    /**
     * Via this method <link>ITransformationStep</link> objects can be added to the chain. These transformation steps
     * represent the different steps in a complex transformation chain (For example: Model to model transformation, code
     * generation...).
     * 
     * @param step
     */
    public void addTransformationStep(ITransformationStep step);

    /**
     * Getter method for the <link>IListeningChannel</link> object, which can be used to inform the chain that it should
     * terminate its execution, as all of the transformation steps have been executed.
     * 
     * @return Returns the aforementioned <link>IListeningChannel</link>
     */
    public IListeningChannel getEndChannel();

    /**
     * Setter method for the <link>IListeningChannel</link> object, which can be used to inform the chain that it should
     * terminate its execution, as all of the transformation steps have been executed.
     * 
     * @param endChannel
     *            <link>IListeningChannel</link> to which the <link>ITransformationChain<link> will listen.
     */
    public void setEndChannel(IListeningChannel endChannel);

    /**
     * Getter method for the <link>IListeningChannel</link> object, which can be used to start the execution of the
     * first transformation step.
     * 
     * @return  <link>IListeningChannel</link> to which the <link>ITransformationChain<link> will send start events.
     */
    public ITargetChannel getStartChannel();

    /**
     * Setter method for the <link>IListeningChannel</link> object, which can be used to start the execution of the
     * first transformation step.
     * 
     * @param startChannel <link>IListeningChannel</link> to which the <link>ITransformationChain<link> will send start events.
     */
    public void setStartChannel(ITargetChannel startChannel);

}
