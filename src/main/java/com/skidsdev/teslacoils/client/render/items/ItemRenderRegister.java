package com.skidsdev.teslacoils.client.render.items;

import com.skidsdev.teslacoils.item.ItemRegister;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemRenderRegister
{
	public static void registerItemRenderer()
	{
		for(Item item : ItemRegister.registeredItems)
		{
			reg(item);
		}
	}
	
	private static void reg(Item item) { reg(item, 0); }
	private static void reg(Item item, int meta) { reg(item, item.getRegistryName().toString(), meta); }
	private static void reg(Item item, String regName, int meta)
	{
		ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(regName, "inventory");
		ModelLoader.setCustomModelResourceLocation(item, meta, itemModelResourceLocation);
	}
}
