package com.skidsdev.teslacoils.item;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockTeslaCoil extends ItemBlock
{
	public ItemBlockTeslaCoil(Block block)
	{
		super(block);
		this.setMaxDamage(0);
		this.setRegistryName(block.getRegistryName().toString());
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing facing, EntityPlayer player, ItemStack stack)
	{
		if (hasValidTileEntity(worldIn, pos, facing))
		{
			return true;
		}
		else return false;
	}
	
	private boolean hasValidTileEntity(World worldIn, BlockPos pos, EnumFacing facing)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity != null)
		{
			if (tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, facing)) return true;
			if (tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, facing)) return true;
		}
		
		return false;
	}
}
