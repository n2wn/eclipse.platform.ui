/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.internal.databinding.validation;

import org.eclipse.core.internal.databinding.conversion.NumberToByteConverter;
import org.eclipse.core.internal.databinding.conversion.StringToNumberParser;

/**
 * Validates if a Number can fit in a Byte.
 * <p>
 * Class is thread safe.
 * </p>
 * 
 * @since 1.0
 */
public class NumberToByteValidator extends NumberToNumberValidator {
	private static final Byte MAX = new Byte(Byte.MAX_VALUE);
	private static final Byte MIN = new Byte(Byte.MIN_VALUE);
	
	/**
	 * @param converter
	 */
	public NumberToByteValidator(NumberToByteConverter converter) {
		super(converter, MIN, MAX);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.databinding.validation.NumberToNumberValidator#inRange(java.lang.Number)
	 */
	protected boolean inRange(Number number) {
		return StringToNumberParser.inByteRange(number);
	}
}
