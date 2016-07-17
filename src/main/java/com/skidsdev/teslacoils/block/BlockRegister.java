package com.skidsdev.teslacoils.block;

import java.util.ArrayList;
import java.util.List;

import com.skidsdev.teslacoils.item.ItemBlockTeslaCoil;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockRegister
{
	public static Block blockTeslaCoil;
	public static Block blockTeslarract;
	
	public static List<Block> registeredBlocks;
	
	public static void createBlocks()
	{
		registeredBlocks = new ArrayList<Block>();
		
		Block x;
		blockTeslaCoil  = registerBlock(x = new BlockTeslaCoil(),  new ItemBlockTeslaCoil(x));
		blockTeslarract = registerBlock(x = new BlockTeslarract(), new ItemBlockTeslaCoil(x));
	}
	
	private static Block registerBlock(Block block)
	{
		return registerBlock(block, (ItemBlock)new ItemBlock(block).setRegistryName(block.getRegistryName().toString()));
	}
	private static Block registerBlock(Block block, ItemBlock itemBlock)
	{
		GameRegistry.register(block);
		GameRegistry.register(itemBlock);
		
		registeredBlocks.add(block);
		
		return block;
	}
}
