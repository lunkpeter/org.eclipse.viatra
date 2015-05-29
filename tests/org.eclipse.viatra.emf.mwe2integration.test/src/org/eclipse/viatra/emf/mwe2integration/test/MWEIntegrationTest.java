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
package org.eclipse.viatra.emf.mwe2integration.test;

import java.util.HashMap;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup;
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner;
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl;
import org.junit.Test;

import com.google.inject.Injector;

/**
 * Test case that demonstrates how an MWE framework should be called programmatically. 
 * 
 * Note: Always run as a plain JUnit test.
 * 
 * @author Peter Lunk
 *
 */
public class MWEIntegrationTest {

    @Test
    public void test() {
        Injector injector = new Mwe2StandaloneSetup().createInjectorAndDoEMFRegistration();
        Mwe2Runner mweRunner = injector.getInstance(Mwe2Runner.class);
        mweRunner.run(URI.createURI("src/org/eclipse/viatra/emf/mwe2integration/demo/TransformationDemo.mwe2"), new HashMap<String,String>(), new WorkflowContextImpl());
    }

}
