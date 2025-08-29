package com.mjhylkema.SlayerTaskSorter;

public enum TaskStatus {
	AVAILABLE("Available", 2848, 1),
	BLOCKED("Blocked", 6412, 2),
	UNAVAILABLE("Cannot Assign", 6411, 3),
	CURRENT("Current Task", 6413, 4);

	private final String description;
	private final int id;
	private final int sortOrder;

	TaskStatus(String description, int id, int sortOrder) {
		this.description = description;
		this.id = id;
		this.sortOrder = sortOrder;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public static TaskStatus fromId(int id) {
		for (TaskStatus status : values()) {
			if (status.getId() == id) {
				return status;
			}
		}
		throw new IllegalArgumentException("No TaskStatus with id: " + id);
	}
}
