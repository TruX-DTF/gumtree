package edu.lu.uni.serval.gumtree;

import java.util.Collections;
import java.util.List;

public class ListSorter<T> {

	private List<T> list;

	public ListSorter(List<T> list) {
		this.list = list;
	}

	public List<T> getList() {
		return this.list;
	}

	public List<T> sortAscending() {
		Collections.sort(this.list, Collections.reverseOrder());
		Collections.reverse(list);
		return this.list;
	}

	public List<T> sortDescending() {
		Collections.sort(this.list, Collections.reverseOrder());
		return this.list;
	}
}
