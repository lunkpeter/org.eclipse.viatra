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
package org.eclipse.viatra.dse.genetic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.viatra.dse.api.DSEException;
import org.eclipse.viatra.dse.api.SolutionTrajectory;
import org.eclipse.viatra.dse.api.strategy.StrategyBase;
import org.eclipse.viatra.dse.api.strategy.interfaces.INextTransition;
import org.eclipse.viatra.dse.base.DesignSpaceManager;
import org.eclipse.viatra.dse.base.GlobalContext;
import org.eclipse.viatra.dse.base.ThreadContext;
import org.eclipse.viatra.dse.designspace.api.IState;
import org.eclipse.viatra.dse.designspace.api.ITransition;
import org.eclipse.viatra.dse.genetic.interfaces.ICrossoverTrajectories;
import org.eclipse.viatra.dse.genetic.interfaces.IMutateTrajectory;
import org.eclipse.viatra.dse.genetic.interfaces.IStoreChild;
import org.eclipse.viatra.dse.multithreading.DSEThreadPool;
import org.eclipse.viatra.dse.util.EMFHelper;

public class MainGeneticStrategy implements INextTransition, IStoreChild {

    enum GeneticStrategyState {
        FIRST_POPULATION, CREATING_NEW_POPULATION, STOPPING
    }

    private GeneticSharedObject sharedObject;
    private List<InstanceData> parentPopulation = new ArrayList<InstanceData>();

    private GeneticStrategyState state = GeneticStrategyState.FIRST_POPULATION;
    private GlobalContext gc;
    private DesignSpaceManager dsm;

    private Random random = new Random();

    private Logger logger = Logger.getLogger(MainGeneticStrategy.class);

    @Override
    public void init(ThreadContext context) {

        gc = context.getGlobalContext();
        dsm = context.getDesignSpaceManager();

        Object so = gc.getSharedObject();

        if (so == null) {
            throw new DSEException("No GeneticSharedObject is set");
        }

        if (so instanceof GeneticSharedObject) {
            sharedObject = (GeneticSharedObject) so;
        } else {
            throw new DSEException("The shared object is not the type of GeneticSharedObject.");
        }

        DSEThreadPool pool = gc.getThreadPool();
        int workerThreads = sharedObject.workerThreads;
        pool.setMaximumPoolSize(workerThreads == 0 ? pool.getMaximumPoolSize() + 1 : workerThreads + 1);

        sharedObject.initialModel = EMFHelper.clone(context.getModelRoot());

        sharedObject.instancesToBeChecked = new ArrayBlockingQueue<InstanceData>(sharedObject.sizeOfPopulation, false);

        sharedObject.initialPopulationSelector.setChildStore(this);
        sharedObject.initialPopulationSelector.init(context);

        logger.debug("MainGeneticStratgey is inited");
    }

    @Override
    public ITransition getNextTransition(ThreadContext context, boolean lastWasSuccesful) {

        do {
            if (state == GeneticStrategyState.FIRST_POPULATION) {

                ITransition transition = sharedObject.initialPopulationSelector.getNextTransition(context,
                        lastWasSuccesful);
                if (transition != null) {
                    return transition;
                } else {
                    logger.debug("Initial population is selected, starting workers.");
                    startWorkerThreads(context);
                    state = GeneticStrategyState.CREATING_NEW_POPULATION;
                }

            }
            if (state == GeneticStrategyState.CREATING_NEW_POPULATION) {

                logger.debug(sharedObject.actNumberOfPopulation + ". population, selecting");

                // Selection
                if (sharedObject.actNumberOfPopulation < sharedObject.maxNumberOfPopulation) {
                    parentPopulation = sharedObject.selector.selectNextPopulation(parentPopulation,
                            sharedObject.comparators, sharedObject.sizeOfPopulation, false);
                } else {
                    List<InstanceData> bestSolutions = sharedObject.selector.selectNextPopulation(parentPopulation,
                            sharedObject.comparators, sharedObject.sizeOfPopulation, true);
                    sharedObject.addInstanceToBestSolutions.set(true);
                    for (InstanceData instanceData : bestSolutions) {
                        sharedObject.instancesToBeChecked.offer(instanceData);
                    }
                    state = GeneticStrategyState.STOPPING;
                    continue;
                }

                // Create children by crossover, everybody selected as a parent at least once
                ArrayList<InstanceData> tempChildren = new ArrayList<InstanceData>();
                ArrayList<InstanceData> alreadyTriedChildren = new ArrayList<InstanceData>();
                Iterator<InstanceData> mainIterator = parentPopulation.iterator();
                while (mainIterator.hasNext()) {
                    InstanceData parent1 = mainIterator.next();

                    // Mutation (crossover with one parent)
                    if (random.nextFloat() < sharedObject.chanceOfMutationInsteadOfCrossover) {
                        List<IMutateTrajectory> mutatiors = sharedObject.mutatiors;
                        int rnd = random.nextInt(mutatiors.size());
                        InstanceData child = mutatiors.get(rnd).mutate(parent1, context);
                        if (child.trajectory.isEmpty()) {
                            throw new DSEException("Mutation operator (at crossover) "
                                    + mutatiors.get(rnd).getClass().getSimpleName() + " returned an empty trajectory.");
                        }
                        logger.debug("Mutation: parent: " + parent1 + " child: " + child);
                        tempChildren.add(child);
                    }
                    // Crossover (with two parents)
                    else {
                        // Choose the second parent
                        int p1Index = random.nextInt(parentPopulation.size());
                        InstanceData parent2 = parentPopulation.get(p1Index);
                        if (parent1.equals(parent2)) {
                            if (p1Index + 1 < parentPopulation.size()) {
                                parent2 = parentPopulation.get(p1Index + 1);
                                ;
                            } else {
                                parent2 = parentPopulation.get(0);
                            }
                        }

                        // Crossover
                        List<ICrossoverTrajectories> crossovers = sharedObject.crossovers;
                        int rnd = random.nextInt(crossovers.size());
                        ICrossoverTrajectories crossover = crossovers.get(rnd);
                        Collection<InstanceData> result = crossover.crossover(Arrays.asList(parent1, parent1), context);
                        logger.debug("Crossover parent1: " + parent1 + " parent2: " + parent2);
                        for (InstanceData child : result) {
                            if (child.trajectory.isEmpty()) {
                                throw new DSEException("Crossover operation " + crossover.getClass().getSimpleName()
                                        + " returned an empty trajectory.");
                            }
                            logger.debug("  Child" + child);
                            tempChildren.add(child);
                        }
                    }

                    // Check the created children and make it feasible by the worker threads
                    for (Iterator<InstanceData> iterator = tempChildren.iterator(); iterator.hasNext();) {
                        InstanceData child = iterator.next();
                        if (child.trajectory.size() > 1 && !isDuplication(child, parentPopulation)
                                && !isDuplication(child, alreadyTriedChildren)) {
                            boolean queueIsNotFull = sharedObject.instancesToBeChecked.offer(child);
                            if (queueIsNotFull) {
                                alreadyTriedChildren.add(child);
                                logger.debug("Child to try: " + child.toString());
                            }
                        }
                        iterator.remove();
                    }

                    // wait for workers and
                    if (alreadyTriedChildren.size() >= sharedObject.sizeOfPopulation) {
                        while (sharedObject.childPopulation.size() + sharedObject.unfeasibleInstances.get() < alreadyTriedChildren
                                .size()) {
                            try {
                                Thread.sleep(1);
                                if (gc.getExceptionHappendInOtherThread().get()
                                        || !gc.getState().equals(GlobalContext.ExplorationProcessState.RUNNING)) {
                                    return null;
                                }
                            } catch (InterruptedException e) {
                            }
                        }

                        // Check for duplications in an other way
                        Iterator<InstanceData> duplicationIterator = sharedObject.childPopulation.iterator();
                        boolean isDuplication = true;
                        duplicationLoop: while (duplicationIterator.hasNext()) {
                            InstanceData id1 = duplicationIterator.next();
                            Iterator<InstanceData> it = sharedObject.childPopulation.iterator();
                            boolean checkedParents = false;
                            while (it.hasNext()) {
                                InstanceData id2 = it.next();
                                if (!id1.equals(id2)) {
                                    IState id1resultState = id1.trajectory.get(id1.trajectory.size() - 1)
                                            .getResultsIn();
                                    IState id2ResultState = id2.trajectory.get(id2.trajectory.size() - 1)
                                            .getResultsIn();
                                    if (id1resultState.equals(id2ResultState)
                                            && id1.sumOfConstraintViolationMeauserement == id2.sumOfConstraintViolationMeauserement) {
                                        for (String objective : sharedObject.comparators.keySet()) {
                                            Double d1 = id1.getFitnessValue(objective);
                                            Double d2 = id2.getFitnessValue(objective);
                                            if (Math.abs(d1 - d2) >= 0.000001) {
                                                isDuplication = false;
                                            }
                                        }
                                        if (isDuplication) {
                                            duplicationIterator.remove();
                                            sharedObject.unfeasibleInstances.incrementAndGet();
                                            continue duplicationLoop;
                                        } else {
                                            isDuplication = true;
                                        }
                                    }
                                }
                                if (!it.hasNext() && !checkedParents) {
                                    it = parentPopulation.iterator();
                                    checkedParents = true;
                                }
                            }
                        }

                        if (sharedObject.childPopulation.size() >= sharedObject.sizeOfPopulation) {
                            // break from creating children
                            break;
                        }
                    }

                    if (!mainIterator.hasNext()) {
                        mainIterator = parentPopulation.iterator();
                    }
                }

                parentPopulation.addAll(sharedObject.childPopulation);
                sharedObject.childPopulation.clear();
                sharedObject.unfeasibleInstances.set(0);

                ++sharedObject.actNumberOfPopulation;

            }
            if (state == GeneticStrategyState.STOPPING) {
                logger.debug("Stopping");
                sharedObject.newPopulationIsNeeded.set(false);
            }

        } while (sharedObject.newPopulationIsNeeded.get()
                && gc.getState().equals(GlobalContext.ExplorationProcessState.RUNNING));

        return null;
    }

    private boolean isDuplication(InstanceData instanceToCheck, List<InstanceData> existentInstances) {
        for (int i = existentInstances.size() - 1; i >= 0; --i) {
            InstanceData existent = existentInstances.get(i);
            boolean sameTrajectory = GeneticHelper.isSameTrajectory(existent.trajectory, instanceToCheck.trajectory);
            if (sameTrajectory) {
                return true;
            }
        }
        return false;
    }

    private void startWorkerThreads(ThreadContext context) {
        while (gc.tryStartNewThread(context, sharedObject.initialModel, true, new StrategyBase(
                new InstanceGeneticStrategy())) != null) {
        }
    }

    @Override
    public void newStateIsProcessed(ThreadContext context, boolean isAlreadyTraversed, boolean isGoalState,
            boolean constraintsNotSatisfied) {
        if (state == GeneticStrategyState.FIRST_POPULATION) {
            sharedObject.initialPopulationSelector.newStateIsProcessed(context, isAlreadyTraversed, isGoalState,
                    constraintsNotSatisfied);
        }
    }

    @Override
    public void addChild(ThreadContext context) {
        ArrayList<ITransition> trajectory = new ArrayList<ITransition>(dsm.getTrajectoryInfo()
                .getTransitionTrajectory());
        InstanceData instance = new InstanceData(trajectory);
        sharedObject.fitnessCalculator.calculateFitness(sharedObject, context, instance);
        parentPopulation.add(instance);
    }

}