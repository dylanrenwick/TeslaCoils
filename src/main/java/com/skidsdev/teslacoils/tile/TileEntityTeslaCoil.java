package com.skidsdev.teslacoils.tile;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.skidsdev.teslacoils.Config;
import com.skidsdev.teslacoils.block.BlockRegister;
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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityTeslaCoil extends TileEntity implements ITickable
{
	public List<TileEntityTeslaCoil> connectedTiles;
	public TileEntity attachedTile;
	
	@Nullable
	private List<BlockPos> loadedTiles;
	
	public TileEntityTeslaCoil()
	{
		connectedTiles = new ArrayList<TileEntityTeslaCoil>();
	}
	
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
				
				this.connectedTiles.add(newConnection);
				newConnection.addConnectedTile(this);
				
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
				tag.setInteger("coiltype", 0);
				
				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			if (connectedTiles != null) this.clearConnections();
		}
	}
	
	public void addConnectedTile(TileEntityTeslaCoil tileEntity)
	{
		if(this.connectedTiles != null && !this.connectedTiles.contains(tileEntity))
		{
			this.connectedTiles.add(tileEntity);
			this.markDirty();
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if (compound.hasKey("Connections")) loadedTiles = deserializeConnections((NBTTagCompound)compound.getTag("Connections"));
		connectedTiles = new ArrayList<TileEntityTeslaCoil>();
	}
	
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound)
	{
		if (connectedTiles != null && !connectedTiles.isEmpty())
		{
			compound.setTag("Connections", getConnectionNBT());
		}
		return super.writeToNBT(compound);
	}

    @Override
    public NBTTagCompound getUpdateTag()
    {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
        return tag;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }
    
	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
	    return new SPacketUpdateTileEntity(this.pos, 0, getUpdateTag());
	}
	
	@Override
	public void onDataPacket (NetworkManager net, SPacketUpdateTileEntity packet)
	{
	    super.onDataPacket(net, packet);
	    this.readFromNBT(packet.getNbtCompound());
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if (attachedTile == null) return false;
		IBlockState state = worldObj.getBlockState(pos);
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.hasCapability(capability, face.getOpposite());
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (attachedTile == null) return null;
		IBlockState state = worldObj.getBlockState(pos);
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.getCapability(capability, face.getOpposite());
	}

	@Override
	public void update()
	{
		if (attachedTile == null || attachedTile.isInvalid())
		{
			getAttachedTile();
		}
		
		if (connectedTiles != null)
		{
			validateConnections();
			
			IBlockState state = worldObj.getBlockState(pos);
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
		if (loadedTiles != null)
		{
			connectedTiles = new ArrayList<TileEntityTeslaCoil>();
			
			for(BlockPos loadedPos : loadedTiles)
			{
				TileEntity tileEntity = worldObj.getTileEntity(loadedPos);
				
				if (tileEntity != null && tileEntity instanceof TileEntityTeslaCoil)
				{
					connectedTiles.add((TileEntityTeslaCoil)tileEntity);
				}
			}
			
			loadedTiles = null;
		}
	}
	
	public void updateBlock()
	{
		IBlockState state = worldObj.getBlockState(pos);
		EnumFacing facing = state.getValue(BlockTeslaCoil.FACING);
		BlockPos attachedPos = pos.offset(facing);
		this.worldObj.markBlockRangeForRenderUpdate(pos, attachedPos);
	}
	
	public void disconnect(TileEntityTeslaCoil tileEntity)
	{
		if (connectedTiles.contains(tileEntity))
		{
			connectedTiles.remove(tileEntity);
			this.markDirty();
		}
	}
	
	public void destroyTile()
	{
		clearConnections();
	}
	
	public boolean hasValidAttachedTile()
	{
		if (attachedTile == null)
		{
			getAttachedTile();
			if (attachedTile == null) return false;
		}
		
		return true;
	}
	
	private NBTTagCompound getConnectionNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		for(int i = 0; i < connectedTiles.size(); i++)
		{
			BlockPos connectionPos = connectedTiles.get(i).getPos();
			tag.setLong("Connection" + i, connectionPos.toLong());
		}
		
		return tag;
	}
	private List<BlockPos> deserializeConnections(NBTTagCompound tag)
	{
		List<BlockPos> connections = new ArrayList<BlockPos>();
		int i = 0;
		
		while(tag.hasKey("Connection" + i))
		{
			BlockPos connectionPos = BlockPos.fromLong(tag.getLong("Connection" + i));
			
			connections.add(connectionPos);
			
			i++;
		}
		
		return connections;
	}
	
	private void getAttachedTile()
	{
		IBlockState state = worldObj.getBlockState(pos);
		if (state.getBlock() != BlockRegister.blockTeslaCoil) return;
		EnumFacing facing = state.getValue(BlockTeslaCoil.FACING);
		BlockPos attachedPos = pos.offset(facing);
		
		TileEntity te = worldObj.getTileEntity(attachedPos);
		
		if (te != null && !te.isInvalid()) attachedTile = te;
	}
	
	private void validateConnections()
	{
		while(connectedTiles.remove(null)) { }
		
		List<TileEntityTeslaCoil> temp = new ArrayList<TileEntityTeslaCoil>(connectedTiles);
		
		for(TileEntityTeslaCoil tileEntity : temp)
		{
			IBlockState state = worldObj.getBlockState(tileEntity.getPos());
			
			if (state.getBlock() != BlockRegister.blockTeslaCoil)
			{
				disconnect(tileEntity);
				continue;
			}
			
			if (!tileEntity.hasValidAttachedTile())
			{
				disconnect(tileEntity);
				continue;
			}
		}
	}
	
	private boolean hasConnectedCapability(Capability<?> capability)
	{		
		for(TileEntityTeslaCoil tileEntity : connectedTiles)
		{
			if (tileEntity.hasCapability(capability, null)) return true;
		}
		
		return false;
	}
	private <T> List<T> getConnectedCapabilities(Capability<T> capability)
	{
		List<T> connectedCaps = new ArrayList<T>();
		
		for(TileEntityTeslaCoil tileEntity : connectedTiles)
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
		for(TileEntityTeslaCoil tileEntity : connectedTiles)
		{
			tileEntity.updateBlock();
		}
	}
	private void terminateConnection(TileEntityTeslaCoil tileEntity)
	{
		if (connectedTiles.contains(tileEntity)) connectedTiles.remove(tileEntity);
		tileEntity.disconnect(this);
	}
	private void clearConnections()
	{
		List<TileEntityTeslaCoil> temp = new ArrayList<TileEntityTeslaCoil>(connectedTiles);
		
		for(TileEntityTeslaCoil tileEntity : temp)
		{
			terminateConnection(tileEntity);
		}
		
		this.markDirty();
	}
}
