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
	public static Block blockRelayCoil;
	public static Block blockTeslarract;
	
	public static List<Block> registeredBlocks;
	
	public static void createBlocks()
	{
		registeredBlocks = new ArrayList<Block>();
		
		blockTeslaCoil  = registerCoil(new BlockTeslaCoil());
		blockRelayCoil  = registerBlock(new BlockRelayCoil());
		blockTeslarract = registerCoil(new BlockTeslarract());
	}
	
	private static Block registerCoil(Block block)
	{
		return registerBlock(block, new ItemBlockTeslaCoil(block));
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
