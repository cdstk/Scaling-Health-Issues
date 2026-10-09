package scalinghealthissues.client.renderer;

import com.google.common.collect.Ordering;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;

public abstract class InventoryEntityEffectRenderer extends InventoryEffectRenderer {

    protected final EntityLivingBase entityLivingBase;

    public InventoryEntityEffectRenderer(Container inventorySlotsIn, Entity entity) {
        super(inventorySlotsIn);
        if(entity instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase) entity;
        }
        else {
            this.entityLivingBase = null;
        }
    }

    protected void updateActivePotionEffects() {
        if(this.entityLivingBase == null) return;

        if (this.entityLivingBase.getActivePotionEffects().isEmpty()) {
            this.guiLeft = (this.width - this.xSize) / 2;
        }
        else {
            if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.PotionShiftEvent(this)))
                this.guiLeft = (this.width - this.xSize) / 2;
            else
                this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.drawActivePotionEffects();
    }

    public void drawActivePotionEffects() {
        if(this.entityLivingBase == null) return;

        int xPos = this.guiLeft - 124;
        int yPos = this.guiTop;
        Collection<PotionEffect> collection = this.entityLivingBase.getActivePotionEffects();

        if (!collection.isEmpty()) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            int yOffset = 33;

            if (collection.size() > 5) {
                yOffset = 132 / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : Ordering.natural().sortedCopy(collection)) {
                Potion potion = potioneffect.getPotion();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
                this.drawTexturedModalRect(xPos, yPos, 0, 166, 140, 32);

                if (potion.hasStatusIcon()) {
                    int iconIndex = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(xPos + 6, yPos + 7, iconIndex % 8 * 18, 198 + iconIndex / 8 * 18, 18, 18);
                }

                potion.renderInventoryEffect(potioneffect, this, xPos, yPos, this.zLevel);
                String name = I18n.format(potion.getName());

                if (potioneffect.getAmplifier() == 1) {
                    name = name + " " + I18n.format("enchantment.level.2");
                }
                else if (potioneffect.getAmplifier() == 2) {
                    name = name + " " + I18n.format("enchantment.level.3");
                }
                else if (potioneffect.getAmplifier() == 3) {
                    name = name + " " + I18n.format("enchantment.level.4");
                }

                this.fontRenderer.drawStringWithShadow(name, (float)(xPos + 10 + 18), (float)(yPos + 6), 16777215);
                String s = Potion.getPotionDurationString(potioneffect, 1.0F);
                this.fontRenderer.drawStringWithShadow(s, (float)(xPos + 10 + 18), (float)(yPos + 6 + 10), 8355711);
                yPos += yOffset;
            }
        }
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, Entity ent) {
        if(ent instanceof EntityLivingBase) {
            GuiInventory.drawEntityOnScreen(posX, posY, scale, mouseX, mouseY, (EntityLivingBase) ent);
        }
        else {
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)posX, (float)posY, 50.0F);
            GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            float originalYaw = ent.rotationYaw;
            float originalPitch = ent.rotationPitch;
            GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
            ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
            ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
            GlStateManager.translate(0.0F, 0.0F, 0.0F);
            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.setPlayerViewY(180.0F);
            rendermanager.setRenderShadow(false);
            rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
            rendermanager.setRenderShadow(true);
            ent.rotationYaw = originalYaw;
            ent.rotationPitch = originalPitch;
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        }
    }
}
