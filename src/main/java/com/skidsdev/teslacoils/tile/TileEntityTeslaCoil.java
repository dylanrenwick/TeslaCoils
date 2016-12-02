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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTeslaCoil extends TileEntity implements ITickable, ITeslaCoil
{
	private static final int CHAT_ID = 47201173;
	
	public List<ITeslaCoil> connectedCoils;
	public TileEntity attachedTile;
	
	@Nullable
	private List<BlockPos> loadedTiles;
	private TeslaContainerCoil container;
	private BlockTeslaCoil.EnumCoilTier tier = BlockTeslaCoil.EnumCoilTier.BASIC;
	
	// Constructors
	
	public TileEntityTeslaCoil()
	{}
	
	public TileEntityTeslaCoil(BlockTeslaCoil.EnumCoilTier tier)
	{
		connectedCoils = new ArrayList<ITeslaCoil>();
		container = new TeslaContainerCoil(tier.getTransferRate());
		this.tier = tier;
	}
	
	// Overrides
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(compound.hasKey("Connections")) loadedTiles = deserializeConnections((NBTTagCompound)compound.getTag("Connections"));
		if(compound.hasKey("TeslaContainer")) container = new TeslaContainerCoil(compound.getTag("TeslaContainer"));
		if(compound.hasKey("CoilTier")) tier = BlockTeslaCoil.EnumCoilTier.values()[compound.getInteger("CoilTier")];
		connectedCoils = new ArrayList<ITeslaCoil>();
	}
	
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound)
	{
		if (connectedCoils != null && !connectedCoils.isEmpty())
		{
			compound.setTag("Connections", getConnectionNBT());
		}
		compound.setTag("TeslaContainer", container.serializeNBT());
		compound.setInteger("CoilTier", tier.ordinal());
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
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
	    super.onDataPacket(net, packet);
	    this.readFromNBT(packet.getNbtCompound());
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return (capability == TeslaCapabilities.CAPABILITY_CONSUMER ||
			    capability == TeslaCapabilities.CAPABILITY_HOLDER);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == TeslaCapabilities.CAPABILITY_CONSUMER ||
		   capability == TeslaCapabilities.CAPABILITY_HOLDER)
			return (T) container;
		
		return null;
	}

	@Override
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
				if (type == 2) return;
				if (type == 0)
				{
					throwToolNBTError(player, "Invalid coiltype NBT tag in Tuning Tool, connection not formed!");
					return;
				}
				
				int x = tag.getInteger("x");
				int xDif = pos.getX() - x;
				if (xDif > 16 || xDif < -16)
				{
					throwToolNBTError(player, "Out of range!");
					return;
				}
				
				int y = tag.getInteger("y");
				int yDif = pos.getY() - y;
				if (yDif > 16 || yDif < -16)
				{
					throwToolNBTError(player, "Out of range!");
					return;
				}
				
				int z = tag.getInteger("z");
				int zDif = pos.getZ() - z;
				if (zDif > 16 || zDif < -16)
				{
					throwToolNBTError(player, "Out of range!");
					return;
				}
				
				TileEntity newConnection = this.worldObj.getTileEntity(new BlockPos(x, y, z));
				if (newConnection == null && !(newConnection instanceof ITeslaCoil))
				{
					throwToolNBTError(player, "No Tesla Coil TileEntity found to connect to, connection not formed!");
					return;
				}
				if (newConnection == this)
				{
					throwToolNBTError(player, "You can't connect a Tesla Coil to itself!");
					return;
				}
				
				this.connectedCoils.add((ITeslaCoil)newConnection);
				((ITeslaCoil)newConnection).addConnectedTile(this);
				
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
				tag.setInteger("coiltype", 1);
				
				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			if (connectedCoils != null) this.clearConnections();
		}
	}

	@Override
	public void disconnect(ITeslaCoil coil)
	{
		if (connectedCoils.contains(coil))
		{
			connectedCoils.remove(coil);
			this.markDirty();
		}
	}

	@Override
	public void addConnectedTile(ITeslaCoil coil)
	{
		if(this.connectedCoils != null && !this.connectedCoils.contains(coil))
		{
			connectedCoils.add(coil);
			this.markDirty();
		}
	}

	@Override
	public boolean hasCoilCapability(Capability<?> capability, ITeslaCoil requester)
	{
		if (attachedTile == null) return false;
		IBlockState state = worldObj.getBlockState(pos);
		if (!state.getProperties().containsKey(BlockTeslaCoil.FACING)) return false;
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.hasCapability(capability, face.getOpposite());
	}

	@Override
	public <T> T getCoilCapability(Capability<T> capability, ITeslaCoil requester)
	{
		if (attachedTile == null) return null;
		IBlockState state = worldObj.getBlockState(pos);
		if (!state.getProperties().containsKey(BlockTeslaCoil.FACING)) return null;
		EnumFacing face = state.getValue(BlockTeslaCoil.FACING);
		return attachedTile.getCapability(capability, face.getOpposite());
	}

	@Override
	public TileEntity getTileEntity()
	{
		return this;
	}

	@Override
	public boolean validateCoil()
	{
		if(isInvalid()) return false;
		
		IBlockState state = worldObj.getBlockState(pos);
		
		if(state.getBlock() != BlockRegister.blockTeslaCoil) return false;
		
		if(!this.hasValidAttachedTile()) return false;
		
		return true;
	}

	@Override
	public void update()
	{
		if(this.attachedTile == null || this.attachedTile.isInvalid())
		{
			getAttachedTile();
		}
		else
		{
			if(connectedCoils != null && !connectedCoils.isEmpty())
			{
				validateConnections();
				
				if(container.getStoredPower() > 0)
				{
					List<ITeslaConsumer> connectedConsumers = new ArrayList<ITeslaConsumer>();
					for(ITeslaCoil coil : connectedCoils)
					{
						if (coil.hasCoilCapability(TeslaCapabilities.CAPABILITY_CONSUMER, this))
						{
							connectedConsumers.add(coil.getCoilCapability(TeslaCapabilities.CAPABILITY_CONSUMER, this));
						}
					}
					
					if (!connectedConsumers.isEmpty())
					{
						for(int i = 0; i < connectedConsumers.size() && container.getStoredPower() > 0; i++)
						{
							container.takePower(connectedConsumers.get(i).givePower(container.takePower(getTransferRate(), true), false), false);
						}
					}
				}
			}
			if(loadedTiles != null)
			{
				connectedCoils = new ArrayList<ITeslaCoil>();
				
				for(BlockPos loadedPos : loadedTiles)
				{
					TileEntity tileEntity = worldObj.getTileEntity(loadedPos);
					
					if (tileEntity != null && tileEntity instanceof ITeslaCoil)
					{
						connectedCoils.add((ITeslaCoil)tileEntity);
					}
				}
				
				loadedTiles = null;
			}
		}
	}
	
	@Override
	public BlockPos getCoilPos()
	{
		return this.pos;
	}
	
	// Public Methods
	
	public void destroyTile()
	{
		this.clearConnections();
	}
	
	public long getTransferRate()
	{
		return this.tier.getTransferRate();
	}
	
	// Private Methods
	
	private void validateConnections()
	{
		while(connectedCoils.remove(null)) { }
		
		List<ITeslaCoil> temp = new ArrayList<ITeslaCoil>(connectedCoils);
		
		for(ITeslaCoil coil : temp)
		{
			if(!coil.validateCoil())
			{
				this.disconnect(coil);
				coil.disconnect(this);
			}
		}
	}
	
	private boolean hasValidAttachedTile()
	{
		if (attachedTile == null)
		{
			getAttachedTile();
			if (attachedTile == null) return false;
		}
		
		return true;
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
	
	private void clearConnections()
	{
		List<ITeslaCoil> temp = new ArrayList<ITeslaCoil>(connectedCoils);
		
		for(ITeslaCoil connectedCoil : temp)
		{
			connectedCoil.disconnect(this);
			this.disconnect(connectedCoil);
		}
	}
	
	private NBTTagCompound getConnectionNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		for(int i = 0; i < connectedCoils.size(); i++)
		{
			BlockPos connectionPos = connectedCoils.get(i).getCoilPos();
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
	
	private void throwToolNBTError(EntityPlayer player, String details)
	{
		if (worldObj.isRemote)
			sendSpamlessMessage(CHAT_ID, new TextComponentString(details));
	}
	
	// Static Methods
	
    @SideOnly(Side.CLIENT)
    private static void sendSpamlessMessage (int messageID, ITextComponent message)
    {        
        final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        chat.printChatMessageWithOptionalDeletion(message, messageID);
    }
}
