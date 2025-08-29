package com.mjhylkema.SlayerTaskSorter;

public enum SortMethod {
	SORT_BY_NAME("Sort by name"),
	SORT_BY_WEIGHTING("Sort by weighting");

	String name;
	int actionIndex = -1;

	SortMethod(String name) {
		this.name = name;
	}

}
