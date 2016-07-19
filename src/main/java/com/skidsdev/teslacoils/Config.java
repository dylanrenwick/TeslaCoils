package com.skidsdev.teslacoils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.skidsdev.teslacoils.block.BlockRegister;
import com.skidsdev.teslacoils.item.ItemRegister;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Config
{
	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_CLIENT = "client";
	public static final String CATEGORY_DEBUG = "debug";
	
	public static Configuration configuration;
	
	public static long teslaCoilTransferRate;
	
	private List<String> validOreDictEntries;
	
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
		validOreDictEntries = new ArrayList<String>();
		
		if (validateOreDict("ingotSilver") && validateOreDict("dustRedstone") && validateOreDict("rodLead"))
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemRegister.itemTuningTool),
					" #=",
					"#+#",
					"+# ",
					'=', "ingotSilver",
					'#', "dustRedstone",
					'+', "rodLead"));
		}
		else
		{
			GameRegistry.addRecipe(new ItemStack(ItemRegister.itemTuningTool),
					" #=",
					"#+#",
					"+# ",
					'=', Items.GOLD_INGOT,
					'#', Items.REDSTONE,
					'+', Items.STICK);
		}
		
		if (validateOreDict("ingotCopper") && validateOreDict("rodSilver") && validateOreDict("plateLead") && validateOreDict("dustRedstone"))
		{
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockRegister.blockTeslaCoil, 2),
					"#=#",
					"#=#",
					"+-+",
					'#', "ingotCopper",
					'=', "rodSilver",
					'+', "plateLead",
					'-', "dustRedstone"));
		}
		else
		{
			GameRegistry.addRecipe(new ItemStack(BlockRegister.blockTeslaCoil, 2),
					"#=#",
					"#=#",
					"+-+",
					'=', Items.STICK,
					'#', Items.REDSTONE,
					'+', Items.IRON_INGOT,
					'-', Items.GOLD_INGOT);
		}
	}
	
	public boolean validateOreDict(String entry)
	{
		if (validOreDictEntries.contains(entry)) return true;
		
		if(OreDictionary.doesOreNameExist(entry))
		{
			logOreDictEntry(entry);
			validOreDictEntries.add(entry);
			return true;
		}
		else
		{
			logMissingOreDictEntry(entry);
			return false;
		}
	}

	
	private void logOreDictEntry(String oreDict)
	{
		FMLLog.log(Level.INFO, "Found oreDict entry %s", oreDict);
	}
	
	private void logMissingOreDictEntry(String oreDict)
	{
		FMLLog.log(Level.WARN, "OreDict entry %s could not be found", oreDict);
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
