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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FSM<S, A> implements Serializable {

    private final HashMap<S, StateEntry> states = new HashMap<S, StateEntry>();;

    private final StateEntry initialState;

    public FSM(final S state) {
        this.initialState = new StateEntry(state);
    }

    public void addAction(final S fromState, final A action, final S toState) {
        StateEntry entry = null;
        if (fromState == null) {
            return;
        }

        entry = this.states.get(fromState);
        if (entry == null) {
            entry = new StateEntry(fromState);
            this.states.put(fromState, entry);
        }

        entry.addAction(action, toState);
        entry = this.states.get(toState);
        if (entry == null) {
            entry = new StateEntry(toState);
            this.states.put(toState, entry);
        }
        entry.addFromAction(action, fromState);
    }

    /**
     * Get possible operations from state @s
     */
    public Set<A> getActionsAtState(final S s) {
        StateEntry entry = this.states.get(s);
        return entry.nextStates.keySet();
    }

    /**
     * Get next state reached when starting from @s and executing action @a
     */
    public S getNextState(final S s, final A a) {

        StateEntry entry = null;
        if (s == null) {
            return null;
        }
        entry = this.states.get(s);
        return entry.nextStates.get(a);
    }

    private class StateEntry implements Serializable {
        public S state;

        public HashMap<A, S> nextStates = new HashMap<A, S>();

        public HashMap<A, List<S>> prevStates = new HashMap<A, List<S>>();

        public StateEntry(final S state) {
            this.state = state;
        }

        public void addAction(final A action, final S state) {
            this.nextStates.put(action, state);
        }

        public void addFromAction(final A action, final S state) {
            List<S> prev = this.prevStates.get(action);
            if (prev == null) {
                prev = new ArrayList<S>();
            }
            prev.add(state);
        }
    }
}
