package com.skidsdev.teslacoils.tile;

import com.skidsdev.teslacoils.utils.ItemNBTHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class TileEntityRelayCoil extends TileEntity
{
	private TileEntity firstConnection;
	private TileEntity secondConnection;
	
	public void onTuningToolUse(EntityPlayer player, ItemStack stack)
	{
		if (!player.isSneaking())
		{
			NBTTagCompound tag = ItemNBTHelper.getCompound(stack, "StartPos", true);
			
			if (tag != null)
			{
				int dimID = tag.getInteger("world");
				if (dimID != worldObj.provider.getDimension()) return;
				
				int type = tag.getInteger("coiltype");
				if (type == 1) return;
				
				int x = tag.getInteger("x");
				int y = tag.getInteger("y");
				int z = tag.getInteger("z");
				
				TileEntityTeslaCoil newConnection = (TileEntityTeslaCoil)this.worldObj.getTileEntity(new BlockPos(x, y, z));
				
				if (firstConnection == null) firstConnection = newConnection;
				else if (secondConnection == null) secondConnection = newConnection;
				else return;
				
				this.markDirty();
				
				stack.setTagCompound(null);
			}
			else
			{
				tag = new NBTTagCompound();
				
				tag.setInteger("x", this.pos.getX());
				tag.setInteger("y", this.pos.getY());
				tag.setInteger("z", this.pos.getZ());
				tag.setInteger("world", worldObj.provider.getDimension());
				tag.setInteger("coiltype", 2);
				
				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			//if (firstConnection != null || secondConnection != null) this.clearConnections();
		}
	}
	
	/*public void disconnect(TileEntityTeslaCoil tileEntity)
	{
		if (connectedTiles.contains(tileEntity))
		{
			connectedTiles.remove(tileEntity);
			this.markDirty();
		}
	}
	
	private void terminateConnection(TileEntityTeslaCoil tileEntity)
	{
		if (connectedTiles.contains(tileEntity)) connectedTiles.remove(tileEntity);
		tileEntity.disconnect(this);
	}
	
	private void clearConnections()
	{
		
	}*/
}
