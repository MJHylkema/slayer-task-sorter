package com.mjhylkema.SlayerTaskSorter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("slayerTaskSorter")
public interface SlayerTasksSorterConfig extends Config
{
	@ConfigItem(
		keyName = "activeSortMethod",
		name = "Sort by",
		description = "The active sort method that's used for sorting slayer tasks"
	)
	default SortMethod sortingMethod() {
		return SortMethod.SORT_BY_WEIGHTING;
	}

	@ConfigItem(
		keyName = "activeSortMethod",
		name = "",
		description = ""
	)
	void setSortingMethod(SortMethod sortMethod);


	@ConfigItem(
		keyName = "reverseSort",
		name = "Reverse sort order",
		description = "Reverse the sort method"
	)
	default boolean reverseSort() {
		return false;
	}

	@ConfigItem(
		keyName = "reverseSort",
		name = "",
		description = ""
	)
	void setReverseOrder(boolean value);
}
