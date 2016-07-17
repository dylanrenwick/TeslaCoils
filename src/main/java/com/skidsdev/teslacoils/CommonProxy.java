package com.skidsdev.teslacoils;

import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
	private Config modConfig;
	
	public void preInit(FMLPreInitializationEvent e)
	{		
		this.modConfig = new Config(e.getSuggestedConfigurationFile());
		
		this.modConfig.setupItems();
		this.modConfig.setupBlocks();
		
		GameRegistry.registerTileEntity(TileEntityTeslaCoil.class, "teslacoil");
	}
	
	public void init(FMLInitializationEvent e)
	{
		this.modConfig.setupCrafting();
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
