package thebetweenlands.client.render.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import paulscode.sound.Vector3D;
import thebetweenlands.client.render.models.tile.ModelDruidAltar;
import thebetweenlands.client.render.models.tile.ModelStone;
import thebetweenlands.common.tileentity.TileEntityDruidAltar;
import thebetweenlands.util.LightingUtil;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class RendererTileEntityDruidAltar extends TileEntitySpecialRenderer<TileEntityDruidAltar> {
    public static RendererTileEntityDruidAltar instance;
    private final ModelDruidAltar model = new ModelDruidAltar();
    private final ModelStone stone = new ModelStone();
    private final ResourceLocation ACTIVE = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarActive.png");
    private final ResourceLocation ACTIVEGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarActiveGlow.png");
    private final ResourceLocation NORMAL = new ResourceLocation("thebetweenlands:textures/tiles/druidAltar.png");
    private final ResourceLocation NORMALGLOW = new ResourceLocation("thebetweenlands:textures/tiles/druidAltarGlow.png");

    public RendererTileEntityDruidAltar() {
    }

    @Override
    public void setRendererDispatcher(TileEntityRendererDispatcher renderer) {
        super.setRendererDispatcher(renderer);
        instance = this;
    }

    public void renderTileAsItem(double x, double y, double z) {
        bindTexture(NORMAL);

        GlStateManager.pushMatrix();
        renderMainModel(x, y, z);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        renderStones(x, y, z, 0);
        GlStateManager.popMatrix();

        LightingUtil.INSTANCE.setLighting(255);

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(true);
        bindTexture(NORMALGLOW);

        GlStateManager.pushMatrix();
        renderMainModel(x, y, z);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        renderStones(x, y, z, 0);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();

        LightingUtil.INSTANCE.revert();
    }

    private void renderTile(TileEntityDruidAltar tile, double x, double y, double z, float partialTicks) {
        //Render main model
        if (tile.getBlockMetadata() == 1) {
            bindTexture(ACTIVE);
        } else {
            bindTexture(NORMAL);
        }
        GlStateManager.pushMatrix();
        renderMainModel(x, y, z);
        GlStateManager.popMatrix();

        //Update rotation
        float renderRotation = tile.rotation + (tile.rotation - tile.prevRotation) * partialTicks;

        //Render floating stones
        GlStateManager.pushMatrix();
        renderStones(x, y, z, renderRotation);
        GlStateManager.popMatrix();

        //Full brightness for items
        LightingUtil.INSTANCE.setLighting(255);

        //Animate the 4 talisman pieces
        if (tile.getBlockMetadata() == 1 && tile.craftingProgress != 0) {
            System.out.println("rip");
            double yOff = tile.renderYOffset + (tile.renderYOffset - tile.prevRenderYOffset) * partialTicks;
            if (yOff > TileEntityDruidAltar.FINAL_HEIGHT + 1.0D) {
                yOff = TileEntityDruidAltar.FINAL_HEIGHT + 1.0D;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 3.1D, z + 0.5D);
            GlStateManager.rotate(renderRotation * 2.0f, 0f, 1f, 0f);
            double shineScale = 0.04f * Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 12);
            GlStateManager.scale(shineScale, shineScale, shineScale);
            this.renderShine((float) Math.sin(Math.toRadians(renderRotation)) / 2.0f - 0.2f, (int) (80 * Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 12)));
            GlStateManager.popMatrix();
            boolean exit = false;
            for (int xi = 0; xi < 2; xi++) {
                for (int zi = 0; zi < 2; zi++) {
                    ItemStack item = tile.getStackInSlot(zi * 2 + xi + 1);
                    if (item == null) {
                        exit = true;
                        break;
                    }
                    float xOff = xi == 0 ? -0.18f : 1.18f;
                    float zOff = zi == 0 ? -0.18f : 1.18f;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x + xOff, y + 1, z + zOff);
                    this.renderCone(5);
                    GlStateManager.popMatrix();
                    Vector3D midVec = new Vector3D((float) x + 0.5f, 0, (float) z + 0.5f);
                    Vector3D diffVec = new Vector3D((float) x + xOff, 0, (float) z + zOff);
                    diffVec.subtract(midVec);
                    double rProgress = 1.0D - Math.pow(1.0D - (TileEntityDruidAltar.FINAL_HEIGHT + 1.0D - yOff) / TileEntityDruidAltar.FINAL_HEIGHT, 6);
                    diffVec.x *= rProgress;
                    diffVec.z *= rProgress;
                    midVec.add(diffVec);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(midVec.x, y + yOff, midVec.z);
                    GlStateManager.scale(0.3f, 0.3f, 0.3f);
                    GlStateManager.rotate(-renderRotation * 2.0f, 0, (float) y, 0);
                    Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.GROUND);
                    GlStateManager.popMatrix();
                }
                if (exit) {
                    break;
                }
            }
        }

        //Render swamp talisman
        ItemStack itemTalisman = tile.getStackInSlot(0);
        if (itemTalisman != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 3.1D, z + 0.5D);
            GlStateManager.rotate(renderRotation * 2.0f, 0, 1, 0);
            double shineScale = 0.04f;
            GlStateManager.scale(shineScale, shineScale, shineScale);
            this.renderShine((float) Math.sin(Math.toRadians(renderRotation)) / 2.0f - 0.2f, 80);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 3.0D, z + 0.5D);
            GlStateManager.scale(0.3f, 0.3f, 0.3f);
            GlStateManager.rotate(-renderRotation * 2.0f, 0, 1, 0);
            Minecraft.getMinecraft().getRenderItem().renderItem(itemTalisman, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
        }

        //Revert to prev lighting
        LightingUtil.INSTANCE.revert();

        //Render glow overlay
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(true);

        float lighting = 150f;
        if (tile.getBlockMetadata() == 1) {
            lighting = (float) Math.sin(Math.toRadians(renderRotation) * 4.0f) * 105.0f + 150.0f;
        }
        LightingUtil.INSTANCE.setLighting((int) lighting);

        if (tile.getBlockMetadata() == 1) {
            bindTexture(ACTIVEGLOW);
        } else {
            bindTexture(NORMALGLOW);
        }

        GlStateManager.pushMatrix();
        renderMainModel(x, y, z);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        renderStones(x, y, z, renderRotation);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();

        LightingUtil.INSTANCE.revert();
    }

    private void renderMainModel(double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        model.renderAll(0.0625F);
        GlStateManager.popMatrix();
    }

    private void renderStones(double x, double y, double z, float rotation) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 1.5F, z + 0.5F);
        GlStateManager.rotate(180F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        stone.renderAll();
        GlStateManager.popMatrix();
    }

    private void renderShine(float rotation, int iterations) {
        Random random = new Random(432L);
        GlStateManager.disableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();
        float f1 = rotation;
        float f2 = 0.0f;
        if (f1 > 0.8F) {
            f2 = (f1 - 0.8F) / 0.2F;
        }
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer buffer = tessellator.getBuffer();
        for (int i = 0; (float) i < iterations; ++i) {
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
            buffer.begin(6, DefaultVertexFormats.POSITION);
            float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            buffer.color(255, 255, 255, (int) (255.0F * (1.0F - f2)));
            buffer.addVertexData(new int[]{0, 0, 0});
            buffer.color(0, 0, 255, 0);
            buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).endVertex();
            buffer.pos(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).endVertex();
            buffer.pos(0.0D, (double) pos1, (double) (1.0F * pos2)).endVertex();
            buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).endVertex();
            tessellator.draw();
        }
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
    }

    private void renderCone(int faces) {
        GlStateManager.pushMatrix();
        float step = 360.0f / (float) faces;

        GlStateManager.disableTexture2D();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.disableAlpha();
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);

        for (float i = 0; i < 360.0f; i += step) {
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            double lr = 0.1D;
            double ur = 0.3D;
            double height = 0.2D;
            double sin = Math.sin(Math.toRadians(i));
            double cos = Math.cos(Math.toRadians(i));
            double sin2 = Math.sin(Math.toRadians(i + step));
            double cos2 = Math.cos(Math.toRadians(i + step));

            buffer.begin(6, DefaultVertexFormats.POSITION);
            buffer.color(255, 255, 255, 0);
            buffer.pos(sin * lr, 0, cos * lr).endVertex();
            buffer.pos(sin2 * lr, 0, cos2 * lr).endVertex();

            buffer.color(0, 0, 255, 60);
            buffer.pos(sin2 * ur, height, cos2 * ur).endVertex();
            buffer.pos(sin * ur, height, cos * ur).endVertex();

            buffer.color(0, 0, 255, 60);
            buffer.pos(sin * ur, height, cos * ur).endVertex();
            buffer.pos(sin2 * ur, height, cos2 * ur).endVertex();

            buffer.color(255, 255, 255, 0);
            buffer.pos(sin2 * lr, 0, cos2 * lr).endVertex();
            buffer.pos(sin * lr, 0, cos * lr).endVertex();
            tessellator.draw();
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GL11.glShadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }


    @Override
    public void renderTileEntityAt(TileEntityDruidAltar te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderTile(te, x, y, z, partialTicks);
    }
}