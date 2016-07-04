package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelChiromaw;
import thebetweenlands.client.render.render.entity.layer.LayerGlow;
import thebetweenlands.common.entity.mobs.EntityChiromaw;

@SideOnly(Side.CLIENT)
public class RenderChiromaw extends RenderLiving<EntityChiromaw> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");

	public RenderChiromaw(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChiromaw(), 0.5F);
		this.addLayer(new LayerGlow(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_glow.png")));
	}

	@Override
	protected void preRenderCallback(EntityChiromaw entitylivingbaseIn, float partialTickTime) {
		EntityChiromaw chiromaw = entitylivingbaseIn;
		if (!chiromaw.getIsHanging()) {
			float flap = MathHelper.sin((entitylivingbaseIn.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
			GlStateManager.translate(0.0F, 0F - flap * 0.5F, 0.0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromaw entity) {
		return TEXTURE;
	}
}
