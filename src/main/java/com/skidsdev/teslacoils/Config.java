package com.skidsdev.teslacoils;

import java.io.File;

import com.skidsdev.teslacoils.block.BlockRegister;
import com.skidsdev.teslacoils.item.ItemRegister;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config
{
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_CLIENT = "client";
	public static final String CATEGORY_DEBUG = "debug";
	
	public static Configuration configuration;
	
	public static long teslaCoilTransferRate;
	
	public Config(File configFile)
	{
		configuration = new Configuration(configFile);
		configuration.load();
		processConfigFile();
		
		configuration.save();
	}
	
	public void setupBlocks()
	{
		BlockRegister.createBlocks();
	}
	
	public void setupItems()
	{
		ItemRegister.createItems();
	}
		
	public void setupCrafting()
	{
		
	}
	
	private void processConfigFile()
	{
		doGeneralConfigs();
		doDebugConfigs();
	}
	
	private void doDebugConfigs()
	{
		
	}
	
	private void doGeneralConfigs()
	{
		Property p;
		p = configuration.get(CATEGORY_GENERAL, "teslaCoilTransferRate", 20);
		p.setComment("The base power transfer rate per tick of Tesla Coils");
		teslaCoilTransferRate = p.getLong();
	}
}
