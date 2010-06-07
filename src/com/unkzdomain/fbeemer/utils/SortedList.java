/*
    FBeemer is a Facebook chat implementation for Android over XMPP

    Copyright (C) 2010  Ankush Gupta (UnkzDomain) unk@unkzdomain.com

    Originally based on BEEM Copyright (C) 2009 by
                          Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of FBeemer.

    FBeemer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FBeemer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FBeemer.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.unkzdomain.fbeemer.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

// TODO: Auto-generated Javadoc
/**
 * The Class SortedList.
 * 
 * @param <E>
 *            the element type
 */
public class SortedList<E> implements List<E> {

	/**
	 * The Class SortedListIterator.
	 * 
	 * @param <T>
	 *            the generic type
	 */
	private class SortedListIterator<T> implements ListIterator<E> {

		/** The m it. */
		private transient final ListIterator<E>	mIt;

		/**
		 * Instantiates a new sorted list iterator.
		 * 
		 * @param iterator
		 *            the iterator
		 */
		SortedListIterator(final ListIterator<E> iterator) {
			this.mIt = iterator;
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#add(java.lang.Object)
		 */
		public void add(final E addObject) {
			throw new UnsupportedOperationException(
					"add() not supported in SortedList iterator");
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#hasNext()
		 */
		public boolean hasNext() {
			return this.mIt.hasNext();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#hasPrevious()
		 */
		public boolean hasPrevious() {
			return this.mIt.hasPrevious();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#next()
		 */
		public E next() {
			return this.mIt.next();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#nextIndex()
		 */
		public int nextIndex() {
			return this.mIt.nextIndex();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#previous()
		 */
		public E previous() {
			return this.mIt.previous();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#previousIndex()
		 */
		public int previousIndex() {
			return this.mIt.previousIndex();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#remove()
		 */
		public void remove() {
			this.mIt.remove();
		}

		/*
		 * (non-Javadoc)
		 * @see java.util.ListIterator#set(java.lang.Object)
		 */
		public void set(final E setObject) {
			throw new UnsupportedOperationException(
					"set () not supported in SortedList iterator");
		}
	}

	/** The backend. */
	private transient final List<E>					backend;

	/** The m comparator. */
	private transient final Comparator<? super E>	mComparator;

	/**
	 * Instantiates a new sorted list.
	 * 
	 * @param list
	 *            the list
	 * @param mComparator
	 *            the m comparator
	 */
	public SortedList(final List<E> list,
			final Comparator<? super E> mComparator) {
		this.mComparator = mComparator;
		this.backend = list;
		Collections.sort(this.backend, mComparator);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(final E addObject) {
		for (final ListIterator<E> it = this.backend.listIterator(); it
				.hasNext();) {
			if (this.mComparator.compare(addObject, it.next()) < 0) {
				if (it.hasPrevious()) {
					it.previous();
				}
				it.add(addObject);
				return true;
			}
		}
		this.backend.add(addObject);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(final int index, final E element) {
		throw new UnsupportedOperationException(
				"add at specific index is not supported in SortedList");
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(final Collection<? extends E> collection) {
		boolean result = false;
		for (final E e : collection) {
			final boolean bool = add(e);
			if (bool) {
				result = bool;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(final int index,
			final Collection<? extends E> collection) {
		return addAll(collection);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#clear()
	 */
	public void clear() {
		this.backend.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(final Object obj) {
		return this.backend.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(final Collection<?> collection) {
		return this.backend.containsAll(collection);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return this.backend.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public E get(final int index) {
		return this.backend.get(index);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.backend.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(final Object obj) {
		return this.backend.indexOf(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return this.backend.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#iterator()
	 */
	public Iterator<E> iterator() {
		return new SortedListIterator<E>(this.backend.listIterator());
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(final Object obj) {
		return this.backend.lastIndexOf(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<E> listIterator() {
		return new SortedListIterator<E>(this.backend.listIterator());
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<E> listIterator(final int index) {
		return new SortedListIterator<E>(this.backend.listIterator(index));
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public E remove(final int index) {
		return this.backend.remove(index);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(final Object obj) {
		return this.backend.remove(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(final Collection<?> collection) {
		return this.backend.removeAll(collection);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(final Collection<?> collection) {
		return this.backend.retainAll(collection);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public E set(final int index, final E element) {
		throw new UnsupportedOperationException(
				"set() is not supported in SortedList");
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#size()
	 */
	public int size() {
		return this.backend.size();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List<E> subList(final int fromIndex, final int toIndex) {
		return this.backend.subList(fromIndex, toIndex);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return this.backend.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.List#toArray(T[])
	 */
	public <T> T[] toArray(final T[] array) {
		return this.backend.toArray(array);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.backend.toString();
	}
}
