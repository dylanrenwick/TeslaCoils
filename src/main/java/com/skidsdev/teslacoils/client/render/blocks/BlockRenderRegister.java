package com.skidsdev.teslacoils.client.render.blocks;

import com.skidsdev.teslacoils.block.BlockRegister;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class BlockRenderRegister
{
	public static void registerBlockRenderer()
	{
		for(Block block : BlockRegister.registeredBlocks)
		{
			reg(block);
		}
	}
	
	private static void reg(Block block)
	{
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName().toString(), "inventory"));
	}
}
