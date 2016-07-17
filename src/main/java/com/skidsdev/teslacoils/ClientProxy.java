package com.skidsdev.teslacoils;

import com.skidsdev.teslacoils.client.render.blocks.BlockRenderRegister;
import com.skidsdev.teslacoils.client.render.items.ItemRenderRegister;
import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;
import com.skidsdev.teslacoils.tile.tesr.TESRTeslaCoil;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		
		BlockRenderRegister.registerBlockRenderer();
		ItemRenderRegister.registerItemRenderer();
	}
	
	@Override
	public void init(FMLInitializationEvent e)
	{
		super.init(e);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTeslaCoil.class, new TESRTeslaCoil());
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent e)
	{
		super.postInit(e);
	}
}