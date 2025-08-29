package com.mjhylkema.SlayerTaskSorter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("slayerTaskSorter")
public interface SlayerTaskSorterConfig extends Config
{
	static String KEY_ACTIVE_SORT_METHOD = "activeSortMethod";
	static String KEY_REVERSE_SORT = "reverseSort";

	@ConfigItem(
		keyName = KEY_ACTIVE_SORT_METHOD,
		name = "Sort by",
		description = "The active sort method that's used for sorting slayer tasks"
	)
	default SortMethod sortingMethod() {
		return SortMethod.SORT_BY_WEIGHTING;
	}

	@ConfigItem(
		keyName = KEY_ACTIVE_SORT_METHOD,
		name = "",
		description = ""
	)
	void setSortingMethod(SortMethod sortMethod);


	@ConfigItem(
		keyName = KEY_REVERSE_SORT,
		name = "Reverse sort order",
		description = "Reverse the sort method"
	)
	default boolean reverseSort() {
		return false;
	}

	@ConfigItem(
		keyName = KEY_REVERSE_SORT,
		name = "",
		description = ""
	)
	void setReverseOrder(boolean value);
}
