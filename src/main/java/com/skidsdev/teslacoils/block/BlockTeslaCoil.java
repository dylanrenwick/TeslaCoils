package com.skidsdev.teslacoils.block;

import java.util.List;

import com.skidsdev.teslacoils.Config;
import com.skidsdev.teslacoils.item.ItemRegister;
import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;
import com.skidsdev.teslacoils.tile.TileEntityTeslarract;
import com.skidsdev.teslacoils.utils.ItemNBTHelper;

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
	private static final PropertyEnum COIL_TIER = PropertyEnum.create("tier", EnumCoilTier.class);
	
	public BlockTeslaCoil()
	{
		super("blockTeslaCoil");
		this.setDefaultState(blockState.getBaseState().withProperty(COIL_TIER, EnumCoilTier.BASIC));
	}
	
	@Override
	public TileEntity createTileEntity(World worldIn, IBlockState state)
	{
		return new TileEntityTeslaCoil(((EnumCoilTier)state.getValue(COIL_TIER)).getTransferRate());
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
	{
		for(EnumCoilTier tier : EnumCoilTier.values())
		{
			ItemStack stack = new ItemStack(item, 1, 0);
			ItemNBTHelper.setInt(stack, "CoilType", tier.ordinal());
			list.add(stack);
		}
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		long rate = EnumCoilTier.BASIC.getTransferRate();
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity != null && tileEntity instanceof TileEntityTeslaCoil)
		{
			rate = ((TileEntityTeslaCoil)tileEntity).getTransferRate();
		}
		EnumCoilTier tier = EnumCoilTier.getTierFromTransferRate(rate);
		return state.withProperty(COIL_TIER, tier != null ? tier : EnumCoilTier.BASIC);
	}
	
	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, COIL_TIER);
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	}
	
	public enum EnumCoilTier implements IStringSerializable
	{
		BASIC("basic", Config.teslaCoilTransferRate),
		ADVANCED("advanced", Config.teslaCoilTransferRate * 4),
		INDUSTRIAL("industrial", Config.teslaCoilTransferRate * 4 * 4),
		CREATIVE("creative", Long.MAX_VALUE);
		
		private long transferRate;
		private String name;
		
		private EnumCoilTier(String name, long transferRate)
		{
			this.name = name;
			this.transferRate = transferRate;
		}

		@Override
		public String getName()
		{
			return name;
		}
		
		public long getTransferRate()
		{
			return transferRate;
		}
		
		public static EnumCoilTier getTierFromTransferRate(long rate)
		{
			for(EnumCoilTier tier : EnumCoilTier.values())
			{
				if (tier.getTransferRate() == rate) return tier;
			}
			
			return null;
		}
	}
}
