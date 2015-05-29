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

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.emf.mwe2integration.IListeningChannel;
import org.eclipse.viatra.emf.mwe2integration.ITargetChannel;
import org.eclipse.viatra.emf.mwe2integration.ITransformationChain;
import org.eclipse.viatra.emf.mwe2integration.ITransformationStep;

/**
 * The MWE2 based implementation of the ITransformationChain interface it also represents the entire transformation
 * workflow as an MWE IWorkflowComponent.
 * 
 * It defines a start and an end channel which can be used to identify the first and last transformation steps in the
 * workflow.
 * 
 * 
 * @author Peter Lunk
 *
 */
public class MWE2TransformationChain implements IWorkflowComponent, ITransformationChain {

    private List<ITransformationStep> transformationSteps = new ArrayList<ITransformationStep>();
    private IListeningChannel endChannel;
    private ITargetChannel startChannel;

    @Override
    public void addTransformationStep(ITransformationStep step) {
        transformationSteps.add(step);

    }

    @Override
    public IListeningChannel getEndChannel() {
        return endChannel;
    }

    @Override
    public void setEndChannel(IListeningChannel endChannel) {
        this.endChannel = endChannel;

    }

    @Override
    public ITargetChannel getStartChannel() {
        return startChannel;
    }

    @Override
    public void setStartChannel(ITargetChannel startChannel) {
        this.startChannel = startChannel;

    }

    public void addTransformationStep(MWE2TransformationStep fragment) {
        transformationSteps.add(fragment);
    }

    /**
     * In this method, the registered transformation steps are initialized and a control event instance is sent to the
     * start channel.
     * 
     * After the initialization, the transformation chain object waits for the transformation workflow to finish, and
     * disposes every transformation step.
     */
    @Override
    public void invoke(IWorkflowContext ctx) {
        boolean ended = false;
        // init fragments
        System.out.println("Initialize transformation steps");
        for (ITransformationStep fragment : transformationSteps) {
            fragment.initialize(ctx);
        }
        for (ITransformationStep fragment : transformationSteps) {
            Thread worker = new Thread(fragment);
            worker.start();
        }

        // add start event to first transformation

        startChannel.createEvent(null);

        // wait for fragments to finish
        while (!ended) {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!this.endChannel.getChannel().getEvents().isEmpty())
                ended = true;
        }

        // stop fragments
        for (ITransformationStep fragment : transformationSteps) {
            fragment.dispose();
        }
        System.out.println("Sequence finished");

    }

    @Override
    public void postInvoke() {
    }

    @Override
    public void preInvoke() {
    }

}
