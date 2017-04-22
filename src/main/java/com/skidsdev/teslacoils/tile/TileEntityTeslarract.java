package com.skidsdev.teslacoils.tile;

import java.util.ArrayList;
import java.util.List;

import com.skidsdev.teslacoils.Config;
import com.skidsdev.teslacoils.block.BlockTeslaCoil;
import com.skidsdev.teslacoils.utils.ItemNBTHelper;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityTeslarract extends TileEntity implements ITickable
{
	public List<TileEntityTeslarract> connectedTiles;
	public TileEntity attachedTile;
	
	public TileEntityTeslarract()
	{
		connectedTiles = new ArrayList<TileEntityTeslarract>();
	}
	
	public void onTuningToolUse(EntityPlayer player, ItemStack stack)
	{
		if (!player.isSneaking())
		{
			NBTTagCompound tag = ItemNBTHelper.getCompound(stack, "StartPos", true);
			
			if (tag != null)
			{
				int type = tag.getInteger("coiltype");
				if (type != 0) return;
				
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				
				TileEntityTeslarract newConnection = (TileEntityTeslarract) world.getTileEntity(new BlockPos(x, y, z));
				
				connectedTiles.add(newConnection);
				newConnection.addConnectedTile(this);
				
				stack.setTagCompound(null);
			}
			else
			{
				tag = new NBTTagCompound();
				
				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				tag.setInteger("coiltype", 0);
				
				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			if (connectedTiles != null) clearConnections();
		}
	}
	
	public void addConnectedTile(TileEntityTeslarract tileEntity)
	{
		if(connectedTiles != null && !connectedTiles.contains(tileEntity)) connectedTiles.add(tileEntity);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.hasCapability(capability, face.getOpposite());
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.getCapability(capability, face.getOpposite());
	}

	@Override
	public void update()
	{
		if (attachedTile == null)
		{
			getAttachedTile();
		}
		
		if (connectedTiles != null)
		{
			IBlockState state = world.getBlockState(pos);
			EnumFacing facing = state.getValue(BlockTeslaCoil.FACING);
			
			ITeslaHolder holder = attachedTile.getCapability(TeslaCapabilities.CAPABILITY_HOLDER, facing.getOpposite()); 
					
			if (hasConnectedCapability(TeslaCapabilities.CAPABILITY_CONSUMER) && 
					attachedTile.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, facing.getOpposite()) && 
					holder.getStoredPower() > 0)
			{
				List<ITeslaConsumer> consumers = getConnectedCapabilities(TeslaCapabilities.CAPABILITY_CONSUMER);
				ITeslaProducer producer = attachedTile.getCapability(TeslaCapabilities.CAPABILITY_PRODUCER, facing.getOpposite());
				
				for(ITeslaConsumer consumer : consumers)
				{
					producer.takePower(consumer.givePower(producer.takePower(Config.teslaCoilTransferRate, true), false), false);
				}
				
				updateConnectedBlocks();
			}
		}
	}
	
	public void updateBlock()
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockTeslaCoil.FACING);
		BlockPos attachedPos = pos.offset(facing);
		world.markBlockRangeForRenderUpdate(pos, attachedPos);
	}
	
	public void disconnect(TileEntityTeslarract tileEntity)
	{
		if (connectedTiles.contains(tileEntity)) connectedTiles.remove(tileEntity);
	}
	
	public void destroyTile()
	{
		clearConnections();
	}
	
	private void getAttachedTile()
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockTeslaCoil.FACING);
		BlockPos attachedPos = pos.offset(facing);
		
		TileEntity te = world.getTileEntity(attachedPos);
		
		if (te != null) attachedTile = te;
	}
	
	private boolean hasConnectedCapability(Capability<?> capability)
	{		
		for(TileEntityTeslarract tileEntity : connectedTiles)
		{
			if (tileEntity.hasCapability(capability, null)) return true;
		}
		
		return false;
	}
	private <T> List<T> getConnectedCapabilities(Capability<T> capability)
	{
		List<T> connectedCaps = new ArrayList<T>();
		
		for(TileEntityTeslarract tileEntity : connectedTiles)
		{
			if (tileEntity.hasCapability(capability, null))
			{
				connectedCaps.add(tileEntity.getCapability(capability, null));
			}
		}
		
		return connectedCaps;
	}
	private void updateConnectedBlocks()
	{
		for(TileEntityTeslarract tileEntity : connectedTiles)
		{
			tileEntity.updateBlock();
		}
	}
	private void terminateConnection(TileEntityTeslarract tileEntity)
	{
		if (connectedTiles.contains(tileEntity)) connectedTiles.remove(tileEntity);
		tileEntity.disconnect(this);
	}
	private void clearConnections()
	{
		List<TileEntityTeslarract> temp = new ArrayList<TileEntityTeslarract>(connectedTiles);
		
		for(TileEntityTeslarract tileEntity : temp)
		{
			terminateConnection(tileEntity);
		}
	}
}
