package org.eclipse.viatra.emf.mwe2integration.demo;

import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;
import org.eclipse.viatra.emf.mwe2integration.mwe2impl.MWE2TransformationStep;

/**
 * Yet another example transformation that is similar to the previous one. For a more complex implementation check out the CPS MWE Orchestrator example in the
 * following repository: https://github.com/IncQueryLabs/incquery-examples-cps
 * 
 * @author Peter Lunk
 *
 */
public class EventDrivenTransformationStep extends MWE2TransformationStep {
    @Override
    public void initialize(IWorkflowContext ctx) {
        System.out.println("Init event-driven transformation");
        this.context = ctx;
    }

    @Override
    public void execute() {
        processNextEvent();
        System.out.println("Send tick to event-driven transformation");
        sendEventToAllTargets();
    }

    @Override
    public void dispose() {
        isRunning = false;
        System.out.println("Dispose event-driven transformation");

    }
}
