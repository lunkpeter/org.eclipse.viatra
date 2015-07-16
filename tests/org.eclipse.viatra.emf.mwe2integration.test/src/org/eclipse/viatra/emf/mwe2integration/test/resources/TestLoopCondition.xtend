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
package org.eclipse.viatra.emf.mwe2integration.test.resources

import org.eclipse.viatra.emf.mwe2integration.providers.IConditionProvider
import org.eclipse.viatra.emf.mwe2integration.providers.impl.BaseProvider

/**
 * 
 */
public class TestLoopCondition extends BaseProvider implements IConditionProvider{
	private int reference = 2;
	private int actual = 0;
	override apply() {
		
		if(reference > actual){
			actual++
			return true
		}
		false
	}
}