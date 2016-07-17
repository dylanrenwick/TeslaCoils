package com.skidsdev.teslacoils;

import com.skidsdev.teslacoils.utils.VersionInfo;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
		modid = VersionInfo.ModId,
		name = VersionInfo.ModName,
		version = VersionInfo.Version
)
public class TeslaCoils
{
	@SidedProxy(clientSide="com.skidsdev.teslacoils.ClientProxy", serverSide="com.skidsdev.teslacoils.ServerProxy")
	public static CommonProxy proxy;
	
	@Instance
	public static TeslaCoils instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		proxy.preInit(e);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
    {
    	proxy.init(e);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent e)
    {
    	proxy.postInit(e);
    }	
}
