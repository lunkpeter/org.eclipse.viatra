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
package org.eclipse.viatra.emf.mwe2integration.eventdriven.mwe2impl;

import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.Executor;
import org.eclipse.incquery.runtime.evm.api.event.EventRealm;
import org.eclipse.incquery.runtime.evm.api.resolver.ChangeableConflictSet;
import org.eclipse.viatra.emf.mwe2integration.eventdriven.IEnabler;

/**
 * An IncQuery EVM Executor, that enables the MWE 2 workflow to control any EVM based fine-grained event-driven
 * transformation, provided that the transformation's ExecutionSchema is created with this executor.
 * 
 * It also implements the IEnabler interface which means that based on the state of other transformation steps, the
 * event-driven transformation can be enabled or disabled.
 * 
 * @author Peter Lunk
 *
 */
public class MWE2EnablerExecutor extends Executor implements IEnabler {
    protected boolean canRun = false;
    protected boolean scheduled = false;
    protected boolean scheduling = false;

    public MWE2EnablerExecutor(EventRealm realm) {
        super(realm);
    }

    /**
     * This method is called by the EVM scheduler of the event-driven transformation. If the transformation is enabled,
     * calling of this method will result in the execution of the transformation. If the transformation has not been
     * enabled by the workflow, a flag is set.
     */
    @Override
    protected void schedule() {
        if (canRun && !scheduling) {
            doSchedule();
        } else if (!scheduled) {
            scheduled = true;
        }
    }

    private void doSchedule() {
        scheduling = true;
        if (!startScheduling()) {
            return;
        }

        Activation<?> nextActivation = null;
        ChangeableConflictSet conflictSet = getRuleBase().getAgenda().getConflictSet();
        while ((nextActivation = conflictSet.getNextActivation()) != null) {
            getRuleBase().getLogger().debug("Executing: " + nextActivation + " in " + this);
            nextActivation.fire(getContext());
        }

        endScheduling();
        scheduling = false;
    }

    @Override
    public void enable() {
        if (scheduled && !scheduling) {
            doSchedule();
            scheduled = false;
        }
        canRun = true;

    }

    @Override
    public void disable() {
        canRun = false;
    }

}
