package com.skidsdev.teslacoils.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public interface ITeslaCoil
{
	/**
	 * Called when the ITeslaCoil is right clicked with a Tuning Tool 
	 * @param player The EntityPlayer right clicking
	 * @param stack The ItemStack of the TuningTOol
	 */
	public void onTuningToolUse(EntityPlayer player, ItemStack stack);
	
	/**
	 * Removes the connection from this coil to another
	 * @param coil The ITeslaCoil to disconnect from
	 */
	public void disconnect(ITeslaCoil coil);
	
	/**
	 * Creates a connection between this coil and another
	 * @param coil The ITeslaCoil to connect to
	 */
	public void addConnectedTile(ITeslaCoil coil);
	
	/**
	 * Called when a connected ITeslaCoil wants to know if this 
	 * ITeslaCoil has a given capability
	 * @param capability The capability to check
	 * @param requester The ITeslaCoil calling the method
	 * @return
	 */
	public boolean hasCoilCapability(Capability<?> capability, ITeslaCoil requester);
	
	/**
	 * Called when a connected ITeslaCoil wants to get the container 
	 * of a given capability from this ITeslaCoil
	 * @param capability The capability to return
	 * @param requester The ITeslaCoil calling the method
	 * @return
	 */
	public <T> T getCoilCapability(Capability<T> capability, ITeslaCoil requester);
	
	/**
	 * 
	 * @return The BlockPos of the TileEntity implementing ITeslaCoil
	 */
	public BlockPos getPos();
	
	/**
	 * 
	 * @return The TileEntity implementing ITeslaCoil
	 */
	public TileEntity getTileEntity();
	
	/**
	 * Validates the position of the ITeslaCoil
	 * @return Whether the ITeslaCoil is still valid
	 */
	public boolean validateCoil();
}
