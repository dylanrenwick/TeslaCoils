package com.skidsdev.teslacoils.item;

import com.skidsdev.teslacoils.block.BlockRegister;
import com.skidsdev.teslacoils.tile.TileEntityTeslaCoil;
import com.skidsdev.teslacoils.tile.TileEntityTeslarract;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTuningTool extends Item
{
	public ItemTuningTool()
	{
		super();
		
		this.setRegistryName("itemTuningTool");
		this.setUnlocalizedName(this.getRegistryName().toString());
		this.setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player, EnumHand hand)
	{
		if(player.isSneaking())
		{
			stack.setTagCompound(null);
		}
		
		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		IBlockState state = worldIn.getBlockState(pos);
		Block type = state.getBlock();
		
		if (state.getBlock() == BlockRegister.blockTeslaCoil)
		{
			TileEntity tileEntity = worldIn.getTileEntity(pos);
			
			if (tileEntity != null)
			{
				if (type == BlockRegister.blockTeslaCoil)
				{
					TileEntityTeslaCoil coilEntity = (TileEntityTeslaCoil)tileEntity;
					coilEntity.onTuningToolUse(player, stack);
				}
				else if (type == BlockRegister.blockTeslarract)
				{
					TileEntityTeslarract coilEntity = (TileEntityTeslarract)tileEntity;
					coilEntity.onTuningToolUse(player, stack);
				}			
			}
		}
		else
		{
			this.onItemRightClick(stack, worldIn, player, hand);
		}
		
		return EnumActionResult.PASS;
	}
}
