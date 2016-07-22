package com.skidsdev.teslacoils.block;

import java.util.List;

import com.skidsdev.teslacoils.item.ItemRegister;
import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;
import com.skidsdev.teslacoils.tile.TileEntityTeslarract;

import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTeslaCoil extends BlockBaseCoil
{
	public BlockTeslaCoil()
	{
		super("blockTeslaCoil");
	}
	
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityTeslaCoil();
	}

	@Override
	protected void destroyBlock(World worldIn, BlockPos pos)
	{
		
	}
}
