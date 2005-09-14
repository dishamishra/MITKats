/*
 * Created on 05.07.2005
 */
package model;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import dom.ReadDOMTree;

/**
 * @author Daniel
 */
public class StateMachinesDiagram extends ModelElement {

	/** Property ID to use when a child is added to this diagram. */
	public static final String CHILD_ADDED_PROP = "StateMachinesDiagram.ChildAdded";

	/** Property ID to use when a child is removed from this diagram. */
	public static final String CHILD_REMOVED_PROP = "StateMachinesDiagram.ChildRemoved";

	private static final long serialVersionUID = 1;

	private List states = new ArrayList();
	
	private Element stateMachine;
	
	public StateMachinesDiagram(Element machine) {
		List statesList = machine.getChildren("state", machine.getNamespace());
        for (int i = 0; i < statesList.size(); i++) {
        	Element ele1 = (Element) statesList.get(i);
        	List attributes = ele1.getAttributes();
        	boolean startState = false;
        	for (int j = 0; j < attributes.size(); j++) {
    			Attribute attr = (Attribute) attributes.get(j);
    			if (attr.getName().equals("START_STATE")) {
    				StartState state = new StartState();
    				state.setStateElement(ele1);
        			this.addChildAtCreation(state);
        			startState = true;
    			}
    		}
        	if (!startState) {
        		NormalState state = new NormalState();
        		state.setStateElement(ele1);
    			this.addChildAtCreation(state);
        	}
        }
        stateMachine = machine;
		for (int j = 0; j < states.size(); j++) {
			//create all transitions
			States state = (States) states.get(j);
			List transitions = state.getAllTransitions();
			for (int k = 0; k < transitions.size(); k++) {
				Element trans = (Element) transitions.get(k);
				new Connection(trans, state, this);
			}
		}
	}

	public StateMachinesDiagram() {
		
	}

	/**
	 * Add a state to this diagram.
	 * 
	 * @param s a non-null states instance
	 * @return true, if the state was added, false otherwise
	 */
	public boolean addChild(States s) {
		if (s != null && states.add(s)) {
			Element eleState = s.getStateElement();
			eleState.setNamespace(stateMachine.getNamespace());
			stateMachine.addContent(eleState);
			firePropertyChange(CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	/**
	 * Add a state to this diagram.
	 * 
	 * @param s a non-null states instance
	 * @return true, if the state was added, false otherwise
	 */
	public boolean addChildAtCreation(States s) {
		if (s != null && states.add(s)) {
			firePropertyChange(CHILD_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/**
	 * Return a List of States in this diagram. The returned List should not be
	 * modified.
	 */
	public List getChildren() {
		return states;
	}
	
	/**
	 * Remove a state from this diagram.
	 * 
	 * @param s a non-null states instance;
	 * @return true, if the state was removed, false otherwise
	 */
	public boolean removeChild(States s) {
		if (s != null && states.remove(s)) {
			Element eleState = s.getStateElement();
			stateMachine.removeContent(eleState);
			firePropertyChange(CHILD_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	public void addToRoot(ReadDOMTree tree) {
		tree.addStateMachine(this, stateMachine);
	}
	
	public void removeFromRoot(ReadDOMTree tree) {
		tree.removeStateMachine(this, stateMachine);
	}
	
	public void changeName(String name) {
		stateMachine.setAttribute("NAME", name);
	}
	
	public String getStateMachineName() {
		return stateMachine.getAttributeValue("NAME");
	}
}
