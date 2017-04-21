package com.skidsdev.teslacoils.item;

import com.skidsdev.teslacoils.block.BlockTeslaCoil;
import com.skidsdev.teslacoils.block.BlockTeslaCoil.EnumCoilTier;
import com.skidsdev.teslacoils.block.BlockTeslarract;
import com.skidsdev.teslacoils.utils.ItemNBTHelper;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import java.util.List;

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
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		EnumCoilTier tier = EnumCoilTier.values()[ItemNBTHelper.getInt(stack, "CoilTier", 0)];
		return super.getUnlocalizedName() + "_" + tier.getName();
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		EnumCoilTier tier = EnumCoilTier.values()[ItemNBTHelper.getInt(stack, "CoilTier", 0)];
		if (getBlock() instanceof BlockTeslaCoil)
		{
			tooltip.add(tier.getTransferRate() + " Tesla/t");
		}
		else if (getBlock() instanceof BlockTeslarract)
		{
			tooltip.add("!! WIP !!");
		}
	}

	private boolean hasValidTileEntity(World worldIn, BlockPos pos, EnumFacing facing)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		
		if (tileEntity != null)
		{
			if (tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, facing)) return true;
			if (tileEntity.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, facing)) return true;
			if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, facing)) return true;
		}
		
		return false;
	}
}
