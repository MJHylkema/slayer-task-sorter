package com.mjhylkema.SlayerTaskSorter;

import com.google.inject.Provides;
import static com.mjhylkema.SlayerTaskSorter.TaskEntry.extractFriendlyName;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Slayer Task Sorter"
)
public class SlayerTaskSorterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SlayerTaskSorterConfig config;

	@Inject
	ClientThread clientThread;

	private Widget taskListsClickable;
	private Widget taskListsDrawable;
	private Widget taskListFrame;
	private Widget taskListTitle;

	private final static int HEIGHT = 22;

	private final List<TaskEntry> entries = new ArrayList<>();

	@Override
	public void startUp() {
		clientThread.invokeLater(this::initWidgets);
	}

	@Provides
	SlayerTaskSorterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SlayerTaskSorterConfig.class);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event) {
		if (event.getGroupId() == InterfaceID.SLAYER_REWARDS_TASK_LIST) {
			initWidgets();

			if (taskListsDrawable == null || taskListsClickable == null) {
				return;
			}

			sortLater();
		}
	}
	@Subscribe
	public void onConfigChanged(ConfigChanged e)
	{
		switch (e.getKey())
		{
			case SlayerTaskSorterConfig.KEY_ACTIVE_SORT_METHOD:
				reorderSortButton(config.sortingMethod());
				break;
			case SlayerTaskSorterConfig.KEY_REVERSE_SORT:
				sortLater();
				break;
			default:
				return;
		}
	}

	private void sortLater() {

		// Delay sorting to the next game tick after the widget updates
		clientThread.invokeLater(() -> {
			if (taskListsDrawable == null || taskListsClickable == null) {
				return;
			}

			entries.clear();

			Widget[] drawableChildren = taskListsDrawable.getChildren();
			if (drawableChildren == null) {
				return;
			}

			Widget[] clickableChildren = taskListsClickable.getChildren();
			if (clickableChildren == null) {
				return;
			}

			for (int i = 0; i < drawableChildren.length - 3; i++) {
				if (isTaskRow(drawableChildren, i)) {
					String friendlyName = extractFriendlyName(drawableChildren[i].getText());
					entries.add(new TaskEntry(
						this, getClickableWidgetFromName(clickableChildren, friendlyName),
						drawableChildren[i],
						drawableChildren[i + 1],
						drawableChildren[i + 2],
						drawableChildren[i + 3]
					));
				}
			}

			sort();
		});
	}

	private void initWidgets() {
		taskListsDrawable = client.getWidget(InterfaceID.SlayerRewardsTaskList.DRAWABLE);
		taskListsClickable = client.getWidget(InterfaceID.SlayerRewardsTaskList.CLICKABLE);
		taskListFrame = client.getWidget(InterfaceID.SlayerRewardsTaskList.FRAME);

		clientThread.invokeLater(() -> {
			if (taskListFrame == null) {
				return;
			}
			taskListTitle = taskListFrame.getChild(1);

			if (taskListTitle == null) {
				return;
			}

			taskListTitle.setOnOpListener((JavaScriptCallback) this::handleSortButtonOp);
			taskListTitle.setHasListener(true);
			reorderSortButton(config.sortingMethod());
			taskListTitle.revalidate();
		});
	}

	private Widget getClickableWidgetFromName(Widget[] clickableWidgets, String name) {
		for (Widget widget : clickableWidgets) {
			if (Text.removeTags(widget.getName()).equals(name)) {
				return widget;
			}
		}
		return null;
	}

	private boolean isTaskRow(Widget[] widgets, int index) {
		return (widgets[index].getType() == 4 && widgets[index].getText() != null) &&
				widgets[index + 1].getType() == 3 &&
				widgets[index + 2].getType() == 5 &&
			   (widgets[index + 3].getType() == 4 && widgets[index + 3].getText() != null);
	}

	private void sort() {
		Comparator<TaskEntry> comparator = null;
		switch (config.sortingMethod()) {
			case SORT_BY_NAME:
				comparator = Comparator.comparing(TaskEntry::getFriendlyName, config.reverseSort()
					? Comparator.nullsLast(Comparator.reverseOrder())   // ascending
					: Comparator.nullsLast(Comparator.naturalOrder())   // descending
					);
				break;
			case SORT_BY_WEIGHTING:
				comparator = Comparator.comparing(TaskEntry::getWeighting, config.reverseSort()
					? Comparator.nullsLast(Comparator.naturalOrder())   // ascending
					: Comparator.nullsLast(Comparator.reverseOrder())   // descending
					)
					.thenComparing(entry -> entry.getStatus().getSortOrder())
					.thenComparing(TaskEntry::getFriendlyName);
				break;
		}

		if (comparator != null) {
			entries.sort(comparator);
		}

		for (int i = 0; i < entries.size(); i++) {
			entries.get(i).setOriginalYAndRevalidate(HEIGHT * i);
		}
	}

	private void handleSortButtonOp(ScriptEvent event) {
		// Special case for first index - reverse sort order
		if (event.getOp() == 1) {
			config.setReverseOrder(!config.reverseSort());
			sortLater();
			return;
		}
		for (SortMethod method : SortMethod.values()) {
			if (method.actionIndex == event.getOp()) {
				config.setSortingMethod(method);
				reorderSortButton(method);
				return;
			}
		}
	}

	private void reorderSortButton(SortMethod firstMethod) {
		int index = 0;
		taskListTitle.setAction(index, "Reverse sort order");
		firstMethod.actionIndex = 1;
		for (SortMethod method : SortMethod.values()) {
			if (method != firstMethod) {
				taskListTitle.setAction(++index, method.name);
				method.actionIndex = index + 1;
			}
		}
		sortLater();
	}
}
