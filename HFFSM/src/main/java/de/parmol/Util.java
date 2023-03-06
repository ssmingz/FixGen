/*
 * Created on Jun 14, 2004
 *
 * Copyright 2004, 2005 Thorsten Meinl
 * 
 * This file is part of ParMol.
 * ParMol is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * ParMol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ParMol; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */
package de.parmol;

import de.parmol.util.ExtendedComparator;


/**
 * This class contains some misc static methods for dealing with graphs and other stuff.
 * 
 * @author Thorsten Meinl <Thorsten.Meinl@informatik.uni-erlangen.de>
 */
public class Util {
	/**
	 * Calculates the number of digits a number has
	 * 
	 * @param number the number whose number of digits should be returned
	 * @return the number of digits
	 */
	public static int getDigits(int number) {
		int digits = 1;
		number /= 10;
		while (number > 0) {
			number /= 10;
			digits++;
		}
		return digits;
	}


	/**
	 * Inserts the <code>number</code> into the buffer and adds at most <code>length</code> blanks at the front. If <code>leftAligned</code> is <code>true</code>
	 * the blanks are added at the back.
	 * 
	 * @param number a number
	 * @param buf a buffer to which the formatted number should be written
	 * @param length the length of the formatted number
	 * @param leftAligned <code>true</code> if the number should be left aligned, <code>false</code> otherwise
	 */
	public static void format(int number, StringBuffer buf, int length, boolean leftAligned) {
		int digits = getDigits(number);
		if (leftAligned) {
			buf.append(number);
			for (int i = length - digits; i > 0; i--) {
				buf.append(' ');
			}
		} else {			
			for (int i = length - digits; i > 0; i--) {
				buf.append(' ');
			}
			buf.append(number);
		}
	}
	
	/**
	 * Shuffles the elements in the given array in place.
	 * 
	 * @param array an array of ints
	 * @return the shuffled array
	 */
	public static int[] shuffle(int[] array) {
		int temp;

		for (int i = array.length - 1; i >= 0; i--) {
			temp = array[i];
			int index = (int) (Math.random() * i);
			array[i] = array[index];
			array[index] = temp;
		}
		return array;
	}


	/**
	 * Returns the number of set bits in the given number.
	 * 
	 * @param number a number
	 * @return the number of set bits in the given number.
	 */
	public static int getSetBits(int number) {
		int count = 0;
		for (int i = 0; i < 31; i++) {
			if ((number & 1) != 0) count++;
			number >>= 1;
		}
		return count;
	}


	/**
	 * Sorts the given <code>int[]</code> -field with quicksort using the given comparator.
	 * 
	 * @param field the field to be sorted
	 * @param comp a comparator
	 * @return the sorted field (the same as <code>field</code>)
	 */
	public static int[] quickSort(int[] field, ExtendedComparator comp) {
		quickSort(field, 0, field.length - 1, comp);
		return field;
	}


	/**
	 * Sorts parts of the given array with the quicksort algorithm using the given comparator.
	 * 
	 * @param field the field to be sorted
	 * @param left the left start index in the field (inclusive)
	 * @param right the right end index in the field (inclusive)
	 * @param comp a comparator
	 */
	public static void quickSort(int[] field, final int left, final int right, ExtendedComparator comp) {
		if (right - left == 2) {
			if (comp.compare(field[left], field[left + 1]) > 0) {
				int temp = field[left];
				field[left] = field[left + 1];
				field[left + 1] = temp;
			}

			if (comp.compare(field[left + 1], field[right]) > 0) {
				int temp = field[right];
				field[right] = field[left + 1];
				field[left + 1] = temp;
			}

			if (comp.compare(field[left], field[left + 1]) > 0) {
				int temp = field[left];
				field[left] = field[left + 1];
				field[left + 1] = temp;
			}
		} else if (right - left == 1) {
			if (comp.compare(field[left], field[right]) > 0) {
				int temp = field[left];
				field[left] = field[right];
				field[right] = temp;
			}
		} else {
			int l = left, r = right;
			int pivot = (right - left) / 2 + left;

			while (l < r) {
				while ((pivot < r) && (comp.compare(field[pivot], field[r]) <= 0)) {
					r--;
				}
				if (pivot < r) {
					int temp = field[pivot];
					field[pivot] = field[r];
					field[r] = temp;
					pivot = r;
				}

				while ((l < pivot) && (comp.compare(field[l], field[pivot]) <= 0)) {
					l++;
				}
				if (l < pivot) {
					int temp = field[pivot];
					field[pivot] = field[l];
					field[l] = temp;
					pivot = l;
				}
			}

			if (l - 1 - left > 0) quickSort(field, left, l - 1, comp);
			if (right - (r + 1) > 0) quickSort(field, r + 1, right, comp);
		}
	}

	/**
	 * Sorts the given <code>int[]</code> -field with quicksort using the given comparator.
	 * 
	 * @param field the field to be sorted
	 * @param comp a comparator
	 * @return the sorted field (the same as <code>field</code>)
	 */
	public static Object[] quickSort(Object[] field, ExtendedComparator comp) {
		quickSort(field, 0, field.length - 1, comp);
		return field;
	}

	/**
	 * Sorts parts of the given array with the quicksort algorithm using the given comparator.
	 * 
	 * @param field the field to be sorted
	 * @param left the left start index in the field (inclusive)
	 * @param right the right end index in the field (inclusive)
	 * @param comp a comparator
	 */
	public static void quickSort(Object[] field, final int left, final int right, ExtendedComparator comp) {
		if (right - left == 2) {
			if (comp.compare(field[left], field[left + 1]) > 0) {
				Object temp = field[left];
				field[left] = field[left + 1];
				field[left + 1] = temp;
			}

			if (comp.compare(field[left + 1], field[right]) > 0) {
				Object temp = field[right];
				field[right] = field[left + 1];
				field[left + 1] = temp;
			}

			if (comp.compare(field[left], field[left + 1]) > 0) {
				Object temp = field[left];
				field[left] = field[left + 1];
				field[left + 1] = temp;
			}
		} else if (right - left == 1) {
			if (comp.compare(field[left], field[right]) > 0) {
				Object temp = field[left];
				field[left] = field[right];
				field[right] = temp;
			}
		} else {
			int l = left, r = right;
			int pivot = (right - left) / 2 + left;

			while (l < r) {
				while ((pivot < r) && (comp.compare(field[pivot], field[r]) <= 0)) {
					r--;
				}
				if (pivot < r) {
					Object temp = field[pivot];
					field[pivot] = field[r];
					field[r] = temp;
					pivot = r;
				}

				while ((l < pivot) && (comp.compare(field[l], field[pivot]) <= 0)) {
					l++;
				}
				if (l < pivot) {
					Object temp = field[pivot];
					field[pivot] = field[l];
					field[l] = temp;
					pivot = l;
				}
			}

			if (l - 1 - left > 0) quickSort(field, left, l - 1, comp);
			if (right - (r + 1) > 0) quickSort(field, r + 1, right, comp);
		}
	}


	/**
	 * Searches the specified array of longs for the specified value using the binary search algorithm. The array
	 * <strong>must </strong> be sorted (as by the <tt>sort</tt> method, above) prior to making this call. If it is not
	 * sorted, the results are undefined. If the array contains multiple elements with the specified value, there is no
	 * guarantee which one will be found.
	 * 
	 * @param a the array to be searched.
	 * @param key the value to be searched for.
	 * @param length length of the array
	 * @return index of the search key, if it is contained in the list; otherwise,
	 *         <tt>(-(<i>insertion point</i>) - 1)</tt>. The <i>insertion point </i> is defined as the point at which
	 *         the key would be inserted into the list: the index of the first element greater than the key, or
	 *         <tt>list.size()</tt>, if all elements in the list are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if the key is found.
	 */
	public static int binarySearch(long[] a, long key, int length) {
		int low = 0;
		int high = length - 1;

		while (low <= high) {
			int mid = (low + high) >> 1;
			long midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1); // key not found.
	}
}