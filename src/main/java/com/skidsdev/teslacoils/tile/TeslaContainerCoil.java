package com.skidsdev.teslacoils.tile;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class TeslaContainerCoil implements ITeslaHolder, ITeslaConsumer, ITeslaProducer, INBTSerializable<NBTTagCompound>
{
	private long capacity;
	private long storedPower;
	
	public TeslaContainerCoil(NBTBase tag)
	{
		this.deserializeNBT((NBTTagCompound)tag);
	}
	public TeslaContainerCoil(long capacity)
	{
		this.capacity = capacity;
		this.storedPower = 0;
	}
	
	@Override public long getCapacity() { return capacity; }

	@Override public long getStoredPower() { return storedPower; }
	
	@Override
	public long givePower(long power, boolean simulated)
	{
		long powerChange = Math.min(power, capacity - storedPower);
		
		if(!simulated) storedPower += powerChange;
		
		return powerChange;
	}

	@Override
	public long takePower(long power, boolean simulated)
	{
		long powerChange = Math.min(power, storedPower);
		
		if(!simulated) storedPower -= powerChange;
		
		return powerChange;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.setLong("Capacity", capacity);
		tag.setLong("StoredPower", storedPower);
		
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("Capacity")) capacity = nbt.getLong("Capacity");
		if (nbt.hasKey("StoredPower")) storedPower = nbt.getLong("StoredPower");
	}
}
