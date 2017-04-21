package com.skidsdev.teslacoils.block;

import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;
import com.skidsdev.teslacoils.tile.TileEntityTeslarract;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTeslarract extends BlockBaseCoil
{
	public BlockTeslarract()
	{
		super("blockTeslarract");
	}

	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityTeslarract();
	}
}
