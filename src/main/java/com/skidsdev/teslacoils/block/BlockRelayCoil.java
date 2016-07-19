package com.skidsdev.teslacoils.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRelayCoil extends BlockBaseCoil
{
	public BlockRelayCoil()
	{
		super("blockRelayCoil");
	}
	
	@Override
	public TileEntity createTileEntity(World worldIn, BlockPos pos)
	{
		
	}

	@Override
	protected void destroyBlock(World worldIn, BlockPos pos)
	{
		
	}
}
