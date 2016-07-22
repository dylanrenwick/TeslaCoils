package com.skidsdev.teslacoils.block;

import com.skidsdev.teslacoils.tile.TileEntityRelayCoil;

import net.minecraft.block.state.IBlockState;
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
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityRelayCoil();
	}

	@Override
	protected void destroyBlock(World worldIn, BlockPos pos)
	{
		
	}
}
