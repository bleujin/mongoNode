/*****************************************************************************
 * Copyright (C) Codehaus.org                                                *
 * ------------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 * http://www.apache.org/licenses/LICENSE-2.0                                *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 *****************************************************************************/
package net.ion.rosetta;

/**
 * Always fails with an error message.
 * 
 * @author Ben Yu
 */
final class FailureParser<T> extends Parser<T> {
	private final String message;

	FailureParser(String msg) {
		this.message = msg;
	}

	@Override
	boolean apply(ParseContext ctxt) {
		ctxt.fail(message);
		return false;
	}

	@Override
	public String toString() {
		return message;
	}
}