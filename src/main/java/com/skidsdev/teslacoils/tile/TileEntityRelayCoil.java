package com.skidsdev.teslacoils.tile;

import javax.annotation.Nullable;

import com.skidsdev.teslacoils.utils.ItemNBTHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityRelayCoil extends TileEntity implements ITickable, ITeslaCoil
{
	private static final int CHAT_ID = 47201174;
	
	public ITeslaCoil firstConnection;
	public ITeslaCoil secondConnection;
	
	private BlockPos firstPos;
	private BlockPos secondPos;
	
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
				
				if (firstConnection == null)
				{
					firstConnection = (ITeslaCoil)newConnection;
					firstConnection.addConnectedTile(this);
				}
				else if (secondConnection == null)
				{
					secondConnection = (ITeslaCoil)newConnection;
					secondConnection.addConnectedTile(this);
				}
				else
				{
					throwToolNBTError(player, "Relay Coils can only handle 2 connections!");
					return;
				}
				
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
				tag.setInteger("coiltype", 3);
				
				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			if (firstConnection  != null) disconnect(firstConnection);
			if (secondConnection != null) disconnect(secondConnection);
			this.markDirty();
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		if (compound.hasKey("Connections")) deserializeConnections((NBTTagCompound)compound.getTag("Connections"));
		super.readFromNBT(compound);
	}
	
	@Override
	public NBTTagCompound writeToNBT (NBTTagCompound compound)
	{
		compound.setTag("Connections", getConnectionNBT());
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
	public void disconnect(ITeslaCoil coil)
	{
		if (firstConnection == coil)
		{
			firstConnection = null;
			coil.disconnect(this);
			this.markDirty();
		}
		else if (secondConnection == coil)
		{
			secondConnection = null;
			coil.disconnect(this);
			this.markDirty();
		}
	}

	@Override
	public void addConnectedTile(ITeslaCoil coil)
	{
		if (firstConnection == null) firstConnection = coil;
		else if (secondConnection == null) secondConnection = coil;
		this.markDirty();
	}

	@Override
	public boolean hasCoilCapability(Capability<?> capability, ITeslaCoil requester)
	{
		if (requester == firstConnection && secondConnection != null) return secondConnection.hasCoilCapability(capability, this);
		else if (requester == secondConnection && firstConnection != null) return firstConnection.hasCoilCapability(capability, this);
		else return false;
	}

	@Override
	public <T> T getCoilCapability(Capability<T> capability, ITeslaCoil requester)
	{
		if (requester == firstConnection && secondConnection != null) return secondConnection.getCoilCapability(capability, this);
		else if (requester == secondConnection && firstConnection != null) return firstConnection.getCoilCapability(capability, this);
		else return null;
	}

	@Override
	public TileEntity getTileEntity()
	{
		return this;
	}

	@Override
	public boolean validateCoil()
	{
		return true;
	}
	
	private NBTTagCompound getConnectionNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		if (firstConnection != null)
		{
			BlockPos connectionPos = firstConnection.getCoilPos();
			tag.setLong("FirstConnection", connectionPos.toLong());
		}
		if (secondConnection != null)
		{
			BlockPos connectionPos = secondConnection.getCoilPos();
			tag.setLong("SecondConnection", connectionPos.toLong());
		}
		
		return tag;
	}
	
	private void deserializeConnections(NBTTagCompound tag)
	{
		if (tag.hasKey("FirstConnection")) firstPos = BlockPos.fromLong(tag.getLong("FirstConnection"));
		if (tag.hasKey("SecondConnection")) secondPos = BlockPos.fromLong(tag.getLong("SecondConnection"));
	}

	@Override
	public void update()
	{
		if (firstPos != null)
		{
			if (firstConnection == null)
			{
				TileEntity tileEntity = worldObj.getTileEntity(firstPos);
				if (tileEntity != null && tileEntity instanceof ITeslaCoil)
				{
					firstConnection = (ITeslaCoil)tileEntity;
				}
			}
			firstPos = null;
		}
		if (secondPos != null)
		{
			if (secondConnection == null)
			{
				TileEntity tileEntity = worldObj.getTileEntity(secondPos);
				if (tileEntity != null && tileEntity instanceof ITeslaCoil)
				{
					secondConnection = (ITeslaCoil)tileEntity;
				}
			}
			secondPos = null;
		}
		
		if (firstConnection != null)
		{
			if (((TileEntity)firstConnection).isInvalid()) firstConnection = null;
		}
		if (secondConnection != null)
		{
			if (((TileEntity)secondConnection).isInvalid()) secondConnection = null;
		}
	}
	
	@Override
	public BlockPos getCoilPos()
	{
		return this.pos;
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
