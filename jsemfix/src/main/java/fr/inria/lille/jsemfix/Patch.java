/*
 * Copyright (C) 2013 INRIA
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package fr.inria.lille.jsemfix;

/**
 * @author Favio D. DeMarco
 *
 */
public interface Patch {

	/**
	 * Class that represents the inability to find a working patch.
	 * 
	 * @author Favio D. DeMarco
	 */
	static final class NoPatch implements Patch {

		private NoPatch() {}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "No viable patch found.";
		}
	}

	/**
	 * Singleton that represents the inability to find a working patch.
	 */
	public static final Patch NO_PATCH = new NoPatch();
}
