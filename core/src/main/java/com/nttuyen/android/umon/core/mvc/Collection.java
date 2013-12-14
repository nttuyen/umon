package com.nttuyen.android.umon.core.mvc;

import java.util.ArrayList;

/**
 * @author nttuyen266@gmail.com
 */
public class Collection<M extends Model> extends ArrayList<M> {
	/**
	 * Param for this event:
	 * 1 => Model raise event
	 * 2 => Child object
	 * 3 => Index
	 */
	public static final String ON_ADD = "onAdd";

	/**
	 * Param for this event
	 * 1 => model
	 * 2 => Model[] array model to add
	 * 3 => fromIndex
	 * 4 => length
	 */
	public static final String ON_ADD_ALL = "onAddAll";

	/**
	 * Param for this event
	 * 1 => Model raise event
	 * 2 => Child object
	 * 3 => index
	 */
	public static final String ON_REMOVE = "onRemove";

	/**
	 * Params for this event:
	 * 1 => model
	 * 2 => child to update
	 * 3 => index
	 */
	public static final String ON_UPDATE = "onUpdate";

	/**
	 * Param for this event:
	 * 1 => Model
	 */
	public static final String ON_CLEAN = "onClean";

	/**
	 * Param for this event:
	 * 1 => Model
	 */
	public static final String ON_CHANGE = "onChange";

	//EventManager
	private final EventManager eventManager = new EventManager();
	EventManager getEventManager() {
		return this.eventManager;
	}

	protected void trigger(String name, Object... params) {
		eventManager.trigger(name, params);
	}

	@Override
	public boolean add(M child) {
		int index = this.size();
		boolean result = super.add(child);
		if(result) {
			trigger(ON_ADD, this, child, index);
		}
		return result;
	}

	@Override
	public void add(int index, M element) {
		super.add(index, element);
		trigger(ON_ADD, this, element, index);
	}

	@Override
	public boolean addAll(java.util.Collection<? extends M> c) {
		int fromIndex = this.size();
		int length = c.size();
		boolean result = super.addAll(c);
		if(result) {
			trigger(ON_ADD_ALL, this, c.toArray(new Model[0]), fromIndex, length);
		}
		return result;
	}

	@Override
	public boolean addAll(int index, java.util.Collection<? extends M> c) {
		int length = c.size();
		boolean result = super.addAll(index, c);
		if(result) {
			trigger(ON_ADD_ALL, this, c.toArray(new Model[0]), index, length);
		}
		return result;
	}

	@Override
	public M set(int index, M element) {
		M m = super.set(index, element);
		trigger(ON_UPDATE, this, element, index);
		return m;
	}

	@Override
	public M remove(int index) {
		M m = super.remove(index);
		eventManager.trigger(ON_REMOVE, this, m, index);
		return m;
	}

	@Override
	public boolean remove(Object o) {
		int index = this.indexOf(o);
		if(index != -1) {
			M m = this.remove(index);
			if(m != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(java.util.Collection<?> c) {
		boolean result = true;
		for(Object o : c) {
			result = result && this.remove(o);
			if(!result) {
				break;
			}
		}
		return result;
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		java.util.Collection<M> models = new ArrayList<M>();
		for(int i = fromIndex; i <= toIndex; i++) {
			models.add(this.get(i));
		}
		this.remove(models);
	}


	@Override
	public M get(int index) {
		return super.get(index);
	}

	@Override
	public void clear() {
		super.clear();
		trigger(ON_CLEAN, this);
	}
}
