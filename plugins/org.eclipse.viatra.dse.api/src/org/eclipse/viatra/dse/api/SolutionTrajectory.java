/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.ChangeCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.incquery.runtime.api.IMatchProcessor;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.incquery.runtime.emf.EMFScope;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra.dse.statecode.IStateCoder;
import org.eclipse.viatra.dse.statecode.IStateCoderFactory;
import org.eclipse.viatra.dse.util.EMFHelper;

import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * A SolutionTrajectory represents a trajectory (i.e. sequence of transformation rule applications), which can transform
 * the initial model to a desired state. An instance of this class holds the the actual rule sequence and the
 * corresponding activation codes. Furthermore it can be used to perform the transformation on a given model (if
 * possible).
 * <p>
 * It is also possible to undo the transformation if initialized with an editing domain.
 * <p>
 * The instance of this class can be reused for different models.
 * 
 * @author Andras Szabolcs Nagy
 *
 */
public class SolutionTrajectory {

    private final List<Object> activationCodes;
    private final List<DSETransformationRule<?, ?>> transformationRules;
    private final IStateCoderFactory stateCoderFactory;
    private Map<String, Double> fitness;

    private IncQueryEngine engine;
    private EObject model;
    private EditingDomain editingDomain;
    private IStateCoder stateCoder;

    private int currentIndex;

    public SolutionTrajectory(final List<Object> activationCodes,
            final List<DSETransformationRule<?, ?>> transformationRules, final IStateCoderFactory stateCoderFactory) {
        checkNotNull(transformationRules, "Parameter transformationRules cannot be null!");
        checkNotNull(stateCoderFactory, "Parameter stateCoderFactory cannot be null!");
        checkNotNull(activationCodes, "Parameter activations cannot be null!");
        checkState(transformationRules.size() == activationCodes.size(),
                "The two List parameters must be the same in size.");

        this.activationCodes = activationCodes;
        this.transformationRules = transformationRules;
        this.stateCoderFactory = stateCoderFactory;
        currentIndex = 0;
    }

    /**
     * Initialize this SolutionTrajectory for transforming the model along the trajectory.
     * 
     * @param modelRoot
     *            The root of the model.
     * @throws IncQueryException
     *             If the IncQuery fails to initialize.
     */
    public void setModel(Notifier modelRoot) throws IncQueryException {
        EMFScope scope = new EMFScope(modelRoot);
        this.engine = IncQueryEngine.on(scope);
        this.model = (EObject) modelRoot;
        stateCoder = stateCoderFactory.createStateCoder();
        stateCoder.init(modelRoot);
        currentIndex = 0;
    }

    /**
     * Initialize this SolutionTrajectory for transforming the given model along the trajectory.
     * <p>
     * The transformation will be reversible by creating an {@link EditingDomain} on the model.
     * 
     * @param modelRoot
     *            The root of the model.
     * @throws IncQueryException
     *             If the IncQuery fails to initialize.
     */
    public void setModelWithEditingDomain(Notifier modelRoot) throws IncQueryException {
        setModel(modelRoot);
        editingDomain = EMFHelper.createEditingDomain(model);
    }

    /**
     * Transforms the given model along the trajectory.
     * 
     * @param modelRoot
     *            The root of the model.
     * @throws IncQueryException
     *             If the IncQuery fails to initialize.
     */
    public void doTransformation(Notifier modelRoot) throws IncQueryException {
        setModel(modelRoot);
        while (doNextTransformation());
    }

    /**
     * Transforms the given model along the trajectory.
     * <p>
     * The transformation will be reversible by creating an {@link EditingDomain} on the model.
     * 
     * @param modelRoot
     *            The root of the model.
     * @throws IncQueryException
     *             If the IncQuery fails to initialize.
     */
    public void doTransformationUndoable(Notifier modelRoot) throws IncQueryException {
        setModelWithEditingDomain(modelRoot);
        while (doNextTransformation());
    }

    /**
     * Transforms the given model along the trajectory. To initialize the model call the
     * {@link SolutionTrajectory#setModel(EObject)} method.
     * 
     * @throws Exception
     *             If the activation to fire is not found. Possible problems: wrong model, bad state serializer.
     * @throws IncQueryException
     *             If the IncQuery fails to initialize.
     */
    public void doTransformation() throws IncQueryException {
        while (doNextTransformation());
    }

    /**
     * Transforms the given model by one step to the solution (makes one step in the trajectory). To initialize the
     * model call the {@link SolutionTrajectory#setModel(EObject)} method.
     * 
     * @throws IncQueryException
     */
    public boolean doNextTransformation() throws IncQueryException {
        if (currentIndex >= activationCodes.size()) {
            return false;
        } else {
            doNextTransformation(currentIndex++);
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private void doNextTransformation(int index) throws IncQueryException {
        checkNotNull(model, "The model cannot be null! Use the setModel method.");

        // cast for the ".process(match)" method.
        DSETransformationRule<?, ?> tr = transformationRules.get(index);

        IncQueryMatcher<?> matcher = tr.getPrecondition().getMatcher(engine);

        boolean isActivationFound = false;
        for (final IPatternMatch match : matcher.getAllMatches()) {
            Object matchHash = stateCoder.createActivationCode(match);
            if (matchHash.equals(activationCodes.get(index))) {
                @SuppressWarnings("rawtypes")
                final IMatchProcessor action = tr.getAction();

                if (editingDomain != null) {
                    action.process(match);
                } else {
                    ChangeCommand cc = new ChangeCommand(model) {
                        @Override
                        protected void doExecute() {
                            action.process(match);
                        }
                    };
                    editingDomain.getCommandStack().execute(cc);
                }

                isActivationFound = true;
                break;
            }
        }
        if (!isActivationFound) {
            throw new UncheckedExecutionException(
                    "Activation was not found for transformation! Possible cause: wrong model, bad state coder.", null);
        }
    }

    /**
     * Call this method to undo the last transformation.
     * 
     * @return True, if it was successful.
     */
    public boolean undoLastTransformation() {
        checkNotNull(editingDomain, "To be able to undo the transformation initialize with editing domain.");

        if (currentIndex > 0) {
            editingDomain.getCommandStack().undo();
            currentIndex--;
            return true;
        }
        return false;
    }

    /**
     * Call this method to undo the transformation.
     */
    public void undoTransformation() {
        while (undoLastTransformation());
    }

    public List<Object> getActivationCodes() {
        return activationCodes;
    }

    public List<DSETransformationRule<?, ?>> getTransformationRules() {
        return transformationRules;
    }

    public IStateCoderFactory getStateCoderFactory() {
        return stateCoderFactory;
    }

    public IncQueryEngine getEngine() {
        return engine;
    }

    public Notifier getModel() {
        return model;
    }

    public IStateCoder getStateCoder() {
        return stateCoder;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTrajectoryLength() {
        return activationCodes.size();
    }

    public Map<String, Double> getFitness() {
        return fitness;
    }

    public void setFitness(Map<String, Double> fitness) {
        this.fitness = fitness;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        for (Object object : activationCodes) {
            sb.append(object.toString());
            sb.append(" | ");
        }
        sb.append("| Fitness: ");
        sb.append(fitness.toString());
        return sb.toString();
    }

}
