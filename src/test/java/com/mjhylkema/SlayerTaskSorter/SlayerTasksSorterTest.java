package com.mjhylkema.SlayerTaskSorter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SlayerTasksSorterTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SlayerTaskSorterPlugin.class);
		RuneLite.main(args);
	}
}