package com.mjhylkema.SlayerTaskSorter;

import net.runelite.api.widgets.Widget;

public class TaskEntry
{
	SlayerTaskSorterPlugin plugin;
	Widget clickable;
	Widget text;
	Widget item;
	Widget icon;
	Widget percent;

	String friendlyName;
	Double weighting;
	TaskStatus status;

	private static int ICON_OFFSET = 2;

	public TaskEntry(SlayerTaskSorterPlugin plugin, Widget clickable, Widget text, Widget item, Widget icon, Widget percent) {
		this.plugin = plugin;
		this.clickable = clickable;
		this.text = text;
		this.item = item;
		this.icon = icon;
		this.percent = percent;

		friendlyName = extractFriendlyName(this.text.getText());
		weighting = extractPercentage(this.percent.getText());
		status = TaskStatus.fromId(icon.getSpriteId());

		icon.setName(status.getDescription());
		icon.setAction(0, "TEST");
		icon.revalidate();
	}

	public static String extractFriendlyName(String nameWithCount)
	{
		if (nameWithCount == null)
			return null;

		int index = nameWithCount.indexOf("(");

		if (index == -1)
			return nameWithCount.trim();

		return nameWithCount.substring(0, index).trim();
	}

	public static double extractPercentage(String input) {
		if (input == null || !input.endsWith("%")) {
			throw new IllegalArgumentException("Input must be a percentage string like '3.3%'");
		}
		String numericPart = input.replace("%", "").trim();
		return Double.parseDouble(numericPart);
	}

	public String getFriendlyName() {
		return this.friendlyName;
	}

	public Double getWeighting() {
		return this.weighting;
	}

	public TaskStatus getStatus() {
		return this.status;
	}

	public void setOriginalYAndRevalidate(int y) {
		if (clickable != null) {
			clickable.setOriginalY(y);
			clickable.revalidate();
		}
		text.setOriginalY(y);
		text.revalidate();
		item.setOriginalY(y);
		item.revalidate();
		icon.setOriginalY(y + ICON_OFFSET);
		icon.revalidate();
		percent.setOriginalY(y);
		percent.revalidate();
	}
}
