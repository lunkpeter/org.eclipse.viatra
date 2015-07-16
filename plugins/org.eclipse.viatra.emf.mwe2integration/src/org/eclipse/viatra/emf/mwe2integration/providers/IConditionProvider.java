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
package org.eclipse.viatra.emf.mwe2integration.providers;

/**
 * Objects implementing this interface can be used for providing dynamically computed conditions for IF statements
 * @author Peter Lunk
 *
 */
public interface IConditionProvider extends IProvider{
    /**
     * Applies the specified condition and returns its result.
     * @return
     */
    public boolean apply();
}
