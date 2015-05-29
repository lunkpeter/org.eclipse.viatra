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
package org.eclipse.viatra.emf.mwe2integration.demo;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.MWE2TransformationStep;

/**
 * An example batch transformation. For a more complex implementation check out the CPS MWE Orchestrator example in the
 * following repository: https://github.com/IncQueryLabs/incquery-examples-cps
 * 
 * @author Peter Lunk
 *
 */
public class BatchTransformationStep extends MWE2TransformationStep {
    @Override
    public void initialize(IWorkflowContext ctx) {
        //The reference to the context is set
        this.context = ctx;
        //Normally the transformation is initialized here.
        System.out.println("Init batch transformation");   
    }
    
    public void execute() {
        //Firstly the triggering event is processed
        processNextEvent();
        //The transformation is executed 
        System.out.println("Batch transformation executed");
        //Events are sent to all target channels
        sendEventToAllTargets();
    }
    
    @Override
    public void dispose() {
        //Upon ending the runnable monitoring the incoming events needs to be stopped
        isRunning = false;
        //Dispose functions
        System.out.println("Dispose batch transformation");
    }
}
