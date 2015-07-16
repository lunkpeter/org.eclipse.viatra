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
package org.eclipse.viatra.emf.mwe2integration.test

import com.google.inject.Guice
import java.util.HashMap
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import org.eclipse.emf.mwe2.language.Mwe2RuntimeModule
import org.eclipse.emf.mwe2.language.Mwe2StandaloneSetup
import org.eclipse.emf.mwe2.launch.runtime.Mwe2Runner
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl
import org.eclipse.xtext.XtextPackage
import org.eclipse.xtext.resource.impl.BinaryGrammarResourceFactoryImpl
import org.junit.Test

import static org.junit.Assert.*

/**
 * 
 */
public class MWEIntegrationTest {
	
	//Serialized with no message
	
	@Test def void ConditionalNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ConditionalNoMessageSerialized.mwe2", 
			"exec_A")
	}
	
	@Test def void DoWhileNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/DoWhileNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_A",
			"exec_A")
	}
	
	@Test def void ForeachNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForeachNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_A")
	}
	
	@Test def void ForNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_A",
			"exec_B",
			"exec_B")
	}
	
	@Test def void RootNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/RootNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_B")
	}
	
	@Test def void SequenceNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/SequenceNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_B")
	}
	
	@Test def void WhileNoMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/WhileNoMessageSerialized.mwe2", 
			"exec_A",
			"exec_A")
	}
	
	// Serialized with messages
	
	@Test def void ConditionalMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ConditionalMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"exec_A")
	}
	
	@Test def void DoWhileMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/DoWhileMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"message_BTestTopicA",
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"message_BTestTopicA",
			"exec_A",
			"message_ATestTopicB",
			"exec_B")
	}
	
	@Test def void ForeachMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForeachMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"message_BTestTopicA",
			"exec_A",
			"message_ATestTopicB",
			"exec_B")
	}
	
	@Test def void ForMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"exec_A",
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"exec_B")
	}
	
	@Test def void RootMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/RootMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_B")
	}
	
	@Test def void SequenceMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/SequenceMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_B")
	}
	
	@Test def void WhileMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/WhileMessageSerialized.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"message_BTestTopicA",
			"exec_A",
			"message_ATestTopicB",
			"exec_B")
	}
	
	// Parallel with messages
	
	@Test def void ConditionalMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ConditionalMessageParallel.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_B")
	}
	
	@Test def void DoWhileMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/DoWhileMessageParallel.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_A",
			"exec_A",
			"exec_A",
			"exec_A",
			"exec_A")
	}
	
	@Test def void ForeachMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForeachMessageParallel.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"message_ATestTopicA",
			"exec_A",
			"exec_A",
			"exec_A")
	}
	
	@Test def void ForMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/ForMessageParallel.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_A",
			"exec_A",
			"exec_A")
	}
	
	@Test def void RootMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/RootMessageParallel.mwe2", 
			"exec_A",
			"exec_B")
	}
	
	@Test def void SequenceMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/SequenceMessageParallel.mwe2", 
			"exec_A",
			"exec_B")
	}
	
	@Test def void WhileMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/WhileMessageParallel.mwe2", 
			"exec_A",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_A",
			"exec_A",
			"exec_A")
	}
	
	// Multi message tests
	@Test def void MultiMessageRemoval() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/MultiMessageRemoval.mwe2", 
			"exec_A",
			"message_ATestTopicB",
			"exec_A",
			"message_ATestTopicB",
			"exec_B",
			"exec_A")
	}
	
	@Test def void MultiMessageSerialized() {
		testWith("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/MultiMessageSerialized.mwe2", 
			"exec_B",
			"exec_A",
			"message_BTestTopicA",
			"message_ATestTopicA",
			"exec_A",
			"message_BTestTopicA",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_A",
			"message_ATestTopicB",
			"exec_A")
	}
	
	@Test def void MultiMessageParallel() {
		testWithParallel("src/org/eclipse/viatra/emf/mwe2integration/test/workflows/MultiMessageParallel.mwe2", 
			"exec_B",
			"exec_A",
			"message_BTestTopicA",
			"message_ATestTopicA",
			"exec_A",
			"message_BTestTopicA",
			"message_ATestTopicA",
			"message_ATestTopicB",
			"exec_A",
			"exec_A",
			"message_ATestTopicB",
			"exec_A")
	}

	def private void testWith(String fileLocation, String ... expected) {
		var result = runTest(fileLocation)
		assertEquals("Result does not match with expected value",expected.toList, result.toList);
		

	}
	
	def private void testWithParallel(String fileLocation, String ... expected) {
		var result = runTest(fileLocation)
		assertEquals("Result does not match with expected value",expected.toSet, result.toSet);
		

	}
	
	private def runTest(String fileLocation){
		val setup = new Mwe2StandaloneSetup
		 		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("ecore"))
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"ecore", new EcoreResourceFactoryImpl());
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xmi"))
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"xmi", new XMIResourceFactoryImpl());
		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().containsKey("xtextbin"))
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"xtextbin", new BinaryGrammarResourceFactoryImpl());
		if (!EPackage.Registry.INSTANCE.containsKey(XtextPackage.eNS_URI))
			EPackage.Registry.INSTANCE.put(XtextPackage.eNS_URI, XtextPackage.eINSTANCE);
		 
		var injector =Guice.createInjector(new Mwe2RuntimeModule() {
			
			override ClassLoader bindClassLoaderToInstance() {
				return class.classLoader
			}
			
		})
		setup.register(injector)
		var mweRunner = injector.getInstance(Mwe2Runner)
		
		var context = new WorkflowContextImpl()
		context.put("TestOutput", new ArrayBlockingQueue<String>(100, true))
		
		mweRunner.run(URI.createURI(fileLocation),new HashMap<String, String>(), context)
			
		var result = (context.get("TestOutput") as BlockingQueue<String>)
		result
	}

}
