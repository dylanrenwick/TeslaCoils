package com.skidsdev.teslacoils.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemRegister
{
	public static List<Item> registeredItems;
	
	public static Item itemTuningTool;
	
	public static void createItems()
	{
		registeredItems = new ArrayList<Item>();
		
		itemTuningTool = registerItem(new ItemTuningTool());
	}
	
	private static Item registerItem(Item reg)
	{
		GameRegistry.register(reg);
		registeredItems.add(reg);
		
		return reg;
	}
}
