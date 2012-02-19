/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.model.utils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class FSM<S, A> {

	private final HashMap<S, StateEntry> states = new HashMap<S, StateEntry>();;
	private final StateEntry initialState;


	public FSM(S state) { 
		initialState = new StateEntry(state);
	}


	public void addAction(S fromState, A action, S toState) {
		StateEntry entry = null;
		if (fromState == null) {
			return;
		}

		entry = states.get(fromState);
		if (entry == null) {
			entry = new StateEntry(fromState);
			states.put(fromState, entry);
		}

		entry.addAction(action, toState);
		entry = states.get(toState);
		if (entry == null) {
			entry = new StateEntry(toState);
			states.put(toState, entry);
		}
		entry.addFromAction(action, fromState);
	}

	/**
	 * Get possible operations from state @s
	 */
	public Set<A> getActionsAtState(S s) {
		StateEntry entry = states.get(s);
		return entry.nextStates.keySet();
	}
	

	/**
	 * Get next state reached when starting from @s and executing
	 * action @a
	 */
	public S getNextState(S s, A a) {

		StateEntry entry = null;
		if (s == null) {
			return null;
		}
		entry = states.get(s);
		return entry.nextStates.get(a);
	}

	private class StateEntry {
		public S state;
		public HashMap<A, S> nextStates = new HashMap<A, S>();
		public HashMap<A, List<S>> prevStates = new HashMap<A, List<S>>();

		public StateEntry(S state) {
			this.state = state;
		}

		public void addAction(A action, S state) {
			nextStates.put(action, state);
		}

		public void addFromAction(A action, S state) {
			List<S> prev = prevStates.get(action);
			if (prev == null) {
				prev = new ArrayList<S>();
			}
			prev.add(state);
		}
	}	
}




