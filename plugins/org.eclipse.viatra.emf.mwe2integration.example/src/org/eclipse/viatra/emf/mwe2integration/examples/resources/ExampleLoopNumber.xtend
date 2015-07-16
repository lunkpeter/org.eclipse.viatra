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
package org.eclipse.viatra.emf.mwe2integration.examples.resources

import org.eclipse.viatra.emf.mwe2integration.providers.IIterationNumberProvider
import org.eclipse.viatra.emf.mwe2integration.providers.impl.BaseProvider

/**
 * This IIterationNumberProvider dynamically evaluates how many iterations a FOR cycle should iterate through.
 * 
 * Note that this provider is used with the examples only, and it implements no logical function.
 */
public class ExampleLoopNumber extends BaseProvider implements IIterationNumberProvider{
	override getIterationNumber() {
		return 2
	}
}