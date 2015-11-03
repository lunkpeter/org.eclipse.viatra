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
package org.eclipse.viatra.emf.runtime.tracer.traceexecutor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.incquery.runtime.evm.api.Activation;
import org.eclipse.incquery.runtime.evm.api.RuleSpecification;
import org.eclipse.incquery.runtime.evm.api.resolver.ConflictSet;
import org.eclipse.viatra.emf.runtime.adapter.impl.AbstractTransformationAdapter;
import org.eclipse.viatra.emf.runtime.rules.eventdriven.EventDrivenTransformationRule;
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.IActivationCoder;
import org.eclipse.viatra.emf.runtime.tracer.activationcoder.impl.DefaultActivationCoder;
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.ITraceModelSerializer;
import org.eclipse.viatra.emf.runtime.tracer.tracemodelserializer.impl.DefaultTraceModelSerializer;

import transformationtrace.ActivationTrace;
import transformationtrace.RuleParameterTrace;
import transformationtrace.TransformationTrace;

/**
 * Adapter implementation that loads transformation traces and executes the transformation according to them
 *
 * @author Peter Lunk
 *
 */
public class TraceExecutor extends AbstractTransformationAdapter{
    Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules;
    TransformationTrace trace;
    IActivationCoder activationCoder;
    ITraceModelSerializer serializer;
    private ConflictSet conflictSet;
    Iterator<?> traceIterator;
    
    public TraceExecutor(IActivationCoder activationCoder, ITraceModelSerializer serializer){
        this.activationCoder = activationCoder;
        this.serializer = serializer;
        trace = serializer.loadTraceModel();
    }
    
    public TraceExecutor(IActivationCoder activationCoder, URI location){
        this.activationCoder = activationCoder;
        this.serializer = new DefaultTraceModelSerializer(location);
        trace = serializer.loadTraceModel();
    }

    public TraceExecutor(URI location){
        this.activationCoder = new DefaultActivationCoder();
        this.serializer = new DefaultTraceModelSerializer(location);
        trace = serializer.loadTraceModel();
    }   
    
    @Override
    public Activation<?> beforeFiring(Activation<?> activation) {
        Set<Activation<?>> activations = conflictSet.getConflictingActivations();
        ActivationTrace activationCode = getNextActivationCode();
        Activation<?> temp = null;
        for(Activation<?> act : activations){
            if(compareActivationCodes(activationCoder.createActivationCode(act,rules), activationCode)){
                temp = act;
            }
        }
        if(temp!=null){
            return temp;          
        }else{
            throw new IllegalStateException("No Activation found for trace:"+activationCode.toString());
        }
    }

    @Override
    public ConflictSet beforeSchedule(ConflictSet conflictSet) {
        this.conflictSet = conflictSet;
        return conflictSet;
    }
    
    private ActivationTrace getNextActivationCode(){
        if(traceIterator == null){
            traceIterator = trace.getActivationTraces().iterator();
        }
        return (ActivationTrace) traceIterator.next();
    }
    
    private boolean compareActivationCodes(ActivationTrace a1, ActivationTrace a2){
        boolean retVal = false;
        if(a1.getRuleName().equals(a2.getRuleName())){
            EList<RuleParameterTrace> a1RuleParameterTraces = a1.getRuleParameterTraces();
            EList<RuleParameterTrace> a2ruleParameterTraces = a2.getRuleParameterTraces();
            if(a1RuleParameterTraces.size()==a2ruleParameterTraces.size()){
                boolean temp = true;
                for(int i = 0; i < a1RuleParameterTraces.size(); i++){
                    RuleParameterTrace a1Trace = a1RuleParameterTraces.get(i);
                    RuleParameterTrace a2Trace = a2ruleParameterTraces.get(i);
                 
                    if(!a1Trace.getParameterName().equals(a2Trace.getParameterName())
                            || !a1Trace.getObjectId().equals(a2Trace.getObjectId())){
                        temp = false;
                    }
                }
                retVal = temp;
            }
        }
        return retVal;
    }
    
    public void setRules(Map<RuleSpecification<?>, EventDrivenTransformationRule<?, ?>> rules){
        this.rules = rules;
    }
}
