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
package org.eclipse.viatra.emf.mwe2integration.debug

import org.eclipse.incquery.runtime.evm.api.Context
import org.eclipse.incquery.runtime.evm.specific.event.IncQueryEventRealm
import org.eclipse.viatra.emf.runtime.adapter.impl.AdaptableExecutorBuilder

import static com.google.common.base.Preconditions.checkArgument

/**
 * Builder class that is responsible for creating an adapter supporting executor, that can be incorporated in MWE2 workflows.
 * 
 * @author Peter Lunk
 */
class MWE2AdaptableExecutorBuilder extends AdaptableExecutorBuilder{
	override build(){
		checkArgument(engine!=null, "Engine cannot be null")
		if(eventRealm == null){
			eventRealm = IncQueryEventRealm.create(engine)
		}
		if(context == null){
			context = Context.create
		}
		
		new MWE2ControlledAdaptableExecutor(eventRealm, context, adapters)
	}
}