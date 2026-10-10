package scalinghealthissues.client.gui.inventory;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.inventory.Slot;
import scalinghealthissues.client.renderer.InventoryEntityEffectRenderer;
import scalinghealthissues.inventory.ContainerEntity;
import scalinghealthissues.inventory.SlotConditionalEdit;

import java.util.List;

import static net.minecraft.item.ItemStack.DECIMALFORMAT;

public class GuiScreenEntityInventory extends InventoryEntityEffectRenderer {

    private final Entity entity;
    private float mousePosx;
    private float mousePosY;

    public GuiScreenEntityInventory(ContainerEntity containerEntity) {
        super(containerEntity, containerEntity.entity);
        this.entity = containerEntity.entity;
        this.allowUserInput = false;
    }

    protected static void addAttributeInfo(List<String> list, EntityLivingBase entityLivingBase, IAttribute attribute) {
        IAttributeInstance attributeInstance = entityLivingBase.getEntityAttribute(attribute);
        if(attributeInstance != null) {
            String attributeName = I18n.format("attribute.name." + attributeInstance.getAttribute().getName());
            if(GuiScreen.isShiftKeyDown()) {
                list.add("[" + DECIMALFORMAT.format(attributeInstance.getBaseValue()) + " " + attributeName);
            }
            else if(!entityLivingBase.isAddedToWorld()) {
                list.add(I18n.format("entity.generic.name") + " " + attributeName);
            }
            else {
                list.add(DECIMALFORMAT.format(attributeInstance.getAttributeValue()) + " " + attributeName);
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        List<String> info = Lists.newArrayList();
        info.add(this.entity.getName());

        if(this.entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) this.entity;

            addAttributeInfo(info, entityLivingBase, SharedMonsterAttributes.MAX_HEALTH);
            addAttributeInfo(info, entityLivingBase, SharedMonsterAttributes.ATTACK_DAMAGE);

        }
        this.drawHoveringText(info, 68, 20);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        // Mainhand
        this.drawTexturedModalRect(i + 76, j + 43, 76, 61, 18, 18);
        // Cover up the crafting grid
        this.drawTexturedModalRect(i + 97, j + 17, 94, 53, 74, 18);
        this.drawTexturedModalRect(i + 97, j + 17 + 18, 94, 53, 74, 18);

        // Cover up any disabled slots
        for (Slot slot : this.inventorySlots.inventorySlots) {
            if (slot instanceof SlotConditionalEdit && ((SlotConditionalEdit) slot).isDisabled()) {
                this.drawTexturedModalRect(
                        i + slot.xPos - 1,
                        j + slot.yPos - 1,
                        76, 43,
                        18, 18
                );
            }
        }
        InventoryEntityEffectRenderer.drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - this.mousePosx, (float) (j + 75 - 50) - this.mousePosY, this.entity);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}