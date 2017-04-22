package com.skidsdev.teslacoils.tile.tesr;

import org.lwjgl.opengl.GL11;

import com.skidsdev.teslacoils.tile.ITeslaCoil;
import com.skidsdev.teslacoils.tile.TileEntityRelayCoil;
import com.skidsdev.teslacoils.utils.RenderHelper;
import com.skidsdev.teslacoils.utils.RenderHelper.Vector;
import com.skidsdev.teslacoils.utils.VersionInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class TESRRelayCoil extends TileEntitySpecialRenderer<TileEntityRelayCoil>
{
	private static final ResourceLocation laser = new ResourceLocation(VersionInfo.ModId, "textures/particle/laser.png");
	
	@Override
	public void renderTileEntityAt(TileEntityRelayCoil te, double x, double y, double z, float partialTicks, int destroyStage)
	{
        if (te.firstConnection != null)
        {
        	renderLaser(te, te.firstConnection, x, y, z, partialTicks, destroyStage);
        }
        if (te.secondConnection != null)
        {
        	renderLaser(te, te.secondConnection, x, y, z, partialTicks, destroyStage);
        }
	}
	
	private void renderLaser(TileEntityRelayCoil te, ITeslaCoil coil, double x, double y, double z, float partialTicks, int destroyStage)
	{
        GlStateManager.pushMatrix();

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
        
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP p = mc.player;
        double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX);
        double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY);
        double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ);

        Vector start = new Vector(te.getPos().getX() + .5f, te.getPos().getY() + .5f, te.getPos().getZ() + .5f);
        Vector player = new Vector((float) doubleX, (float) doubleY + p.getEyeHeight(), (float) doubleZ);

        GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();

        this.bindTexture(laser);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        
        BlockPos destination = new BlockPos(coil.getCoilPos());
        Vector end = new Vector(destination.getX() + .5f, destination.getY() + .5f, destination.getZ() + .5f);
        
        int a = 128;
        
        RenderHelper.drawBeam(start, end, player, .1f, a);

        tessellator.draw();
        
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
	}
}
