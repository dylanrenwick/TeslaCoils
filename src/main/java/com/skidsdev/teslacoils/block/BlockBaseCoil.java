package com.skidsdev.teslacoils.block;

import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBaseCoil extends Block
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	
	public BlockBaseCoil(String regName)
	{
		super(Material.IRON);
		setRegistryName(regName);
		setUnlocalizedName(this.getRegistryName().toString());
		setHardness(1.5f);
		setResistance(10.0f);
		setCreativeTab(CreativeTabs.REDSTONE);
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	@SuppressWarnings("deprecation")
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, facing.getOpposite());
	}

	/* Might need to be fixed and updated later, but currently unused
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getStateFromMeta(meta).withProperty(FACING, facing.getOpposite());
	}*/
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		EnumFacing facing = state.getValue(FACING);
		
		double pixelInc = 1.0 / 16.0;
		
		double startX = 0;
		double startY = 0;
		double startZ = 0;
		
		double endX = 0;
		double endY = 0;
		double endZ = 0;
		
		switch(facing)
		{
			case NORTH:
				startX = 5;
				startY = 5;
				startZ = 0;
				endX = 11;
				endY = 11;
				endZ = 11;
				break;
			case SOUTH:
				startX = 5;
				startY = 5;
				startZ = 5;
				endX = 11;
				endY = 11;
				endZ = 16;
				break;
			case EAST:
				startX = 5;
				startY = 5;
				startZ = 5;
				endX = 16;
				endY = 11;
				endZ = 11;
				break;
			case WEST:
				startX = 0;
				startY = 5;
				startZ = 5;
				endX = 11;
				endY = 11;
				endZ = 11;
				break;
			case UP:
				startX = 5;
				startY = 5;
				startZ = 5;
				endX = 11;
				endY = 16;
				endZ = 11;
				break;
			case DOWN:
				startX = 5;
				startY = 0;
				startZ = 5;
				endX = 11;
				endY = 11;
				endZ = 11;
				break;
		}
		
		return new AxisAlignedBB(startX * pixelInc, startY * pixelInc, startZ * pixelInc, endX * pixelInc, endY * pixelInc, endZ * pixelInc);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int facingMeta = ((EnumFacing)state.getValue(FACING)).ordinal();
		
		return facingMeta;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing;
		
		facing = EnumFacing.values()[meta];
		
		return getDefaultState().withProperty(FACING, facing);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) { return false; }
	
	@Override
	public boolean isFullCube(IBlockState state) { return false; }
	
	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }
}
