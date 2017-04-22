package com.skidsdev.teslacoils;

import com.skidsdev.teslacoils.tile.TileEntityRelayCoil;
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
		modConfig = new Config(e.getSuggestedConfigurationFile());
		
		modConfig.setupItems();
		modConfig.setupBlocks();
		
		GameRegistry.registerTileEntity(TileEntityTeslaCoil.class, "teslacoil");
		GameRegistry.registerTileEntity(TileEntityRelayCoil.class, "relaycoil");
	}
	
	public void init(FMLInitializationEvent e)
	{
		modConfig.setupCrafting();
	}
	
	public void postInit(FMLPostInitializationEvent e)
	{
		
	}
}
