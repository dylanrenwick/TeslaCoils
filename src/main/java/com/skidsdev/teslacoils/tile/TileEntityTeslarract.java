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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTeslarract extends TileEntity implements ITickable, ITeslaCoil
{
    private static final int CHAT_ID = 47201175;

    private final long transferRate = Config.teslaCoilTransferRate * 32;

	public List<ITeslaCoil> connectedCoils;
	public TileEntity attachedTile;
	
	public TileEntityTeslarract()
	{
        connectedCoils = new ArrayList<ITeslaCoil>();
	}
	
	public void onTuningToolUse(EntityPlayer player, ItemStack stack)
	{
		if (!player.isSneaking())
		{
			NBTTagCompound tag = ItemNBTHelper.getCompound(stack, "StartPos", true);

			if (tag != null)
			{
				int dimID = tag.getInteger("world");
				if (dimID != world.provider.getDimension()) return;

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

				TileEntity newConnection = world.getTileEntity(new BlockPos(x, y, z));
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

				connectedCoils.add((ITeslaCoil)newConnection);
				((ITeslaCoil)newConnection).addConnectedTile(this);

				markDirty();

				stack.setTagCompound(null);
			}
			else
			{
				tag = new NBTTagCompound();

				tag.setInteger("x", pos.getX());
				tag.setInteger("y", pos.getY());
				tag.setInteger("z", pos.getZ());
				tag.setInteger("world", world.provider.getDimension());
				tag.setInteger("coiltype", 1);

				ItemNBTHelper.setCompound(stack, "StartPos", tag);
			}
		}
		else
		{
			if (connectedCoils != null) clearConnections();
		}

		/*if (!player.isSneaking())
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
				
				connectedCoils.add(newConnection);
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
			if (connectedCoils != null) clearConnections();
		}*/
	}

    @Override
    public void disconnect(ITeslaCoil coil)
    {
        if (connectedCoils.contains(coil))
        {
            connectedCoils.remove(coil);
            markDirty();
        }
    }

    @Override
    public void addConnectedTile(ITeslaCoil coil)
    {

    }

    @Override
    public boolean hasCoilCapability(Capability<?> capability, ITeslaCoil requester)
    {
        return false;
    }

    @Override
    public <T> T getCoilCapability(Capability<T> capability, ITeslaCoil requester)
    {
        return null;
    }

    @Override
    public BlockPos getCoilPos()
    {
        return null;
    }

    @Override
    public TileEntity getTileEntity()
    {
        return null;
    }

    @Override
    public boolean validateCoil()
    {
        return false;
    }

    public void addConnectedTile(TileEntityTeslarract tileEntity)
	{
		if(connectedCoils != null && !connectedCoils.contains(tileEntity)) connectedCoils.add(tileEntity);
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
		
		if (connectedCoils != null)
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
		for(ITeslaCoil coil : connectedCoils)
		{
			if (coil.hasCoilCapability(capability, null)) return true;
		}
		
		return false;
	}

	private <T> List<T> getConnectedCapabilities(Capability<T> capability)
	{
		List<T> connectedCaps = new ArrayList<T>();
		
		for(ITeslaCoil coil : connectedCoils)
		{
			if (coil.hasCoilCapability(capability, null))
			{
				connectedCaps.add(coil.getCoilCapability(capability, null));
			}
		}
		
		return connectedCaps;
	}

	private void terminateConnection(ITeslaCoil coil)
	{
		if (connectedCoils.contains(coil)) connectedCoils.remove(coil);
		coil.disconnect(this);
	}

	private void clearConnections()
	{
		List<ITeslaCoil> temp = new ArrayList<ITeslaCoil>(connectedCoils);
		
		for(ITeslaCoil coil : temp)
		{
			terminateConnection(coil);
		}
	}

	private void throwToolNBTError(EntityPlayer player, String details)
	{
		if (world.isRemote)
			sendSpamlessMessage(CHAT_ID, new TextComponentString(details));
	}

	// Static Methods
	// Audiatorix: I really want to factor this shit out. Any reason we can't?

	@SideOnly(Side.CLIENT)
	private static void sendSpamlessMessage(int messageID, ITextComponent message)
	{
		final GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
		chat.printChatMessageWithOptionalDeletion(message, messageID);
	}
}
