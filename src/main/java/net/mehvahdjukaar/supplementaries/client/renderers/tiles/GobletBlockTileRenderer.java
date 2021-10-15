package net.mehvahdjukaar.supplementaries.client.renderers.tiles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.supplementaries.block.tiles.GobletBlockTile;
import net.mehvahdjukaar.supplementaries.client.renderers.RendererUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;


public class GobletBlockTileRenderer extends BlockEntityRenderer<GobletBlockTile> {

    public GobletBlockTileRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public static void renderFluid(float h, int color, int luminosity, ResourceLocation texture, PoseStack matrixStackIn, MultiBufferSource bufferIn, int light, int combinedOverlayIn, boolean shading){
        matrixStackIn.pushPose();
        float opacity = 1;//tile.liquidType.opacity;
        if(luminosity!=0) light = light & 15728640 | luminosity << 4;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(texture);
        // TODO:remove breaking animation
        VertexConsumer builder = bufferIn.getBuffer(RenderType.translucentMovingBlock());
        matrixStackIn.translate(0.5, 0.0625, 0.5);

        float w = 0.25f;


        int lu = light & '\uffff';
        int lv = light >> 16 & '\uffff'; // ok
        float atlasscaleU = sprite.getU1() - sprite.getU0();
        float atlasscaleV = sprite.getV1() - sprite.getV0();
        float minu = sprite.getU0();
        float minv = sprite.getV0();
        float maxu = minu + atlasscaleU * w;
        float maxv = minv + atlasscaleV * h;
        float maxv2 = minv + atlasscaleV * w;

        float r = (float) ((color >> 16 & 255)) / 255.0F;
        float g = (float) ((color >> 8 & 255)) / 255.0F;
        float b = (float) ((color & 255)) / 255.0F;



        float hw = w / 2f;

        RendererUtil.addQuadTop(builder, matrixStackIn, -hw, h, hw, hw, h, -hw, minu, minv, maxu, maxv2, r, g, b, opacity, lu, lv, 0, 1, 0);

        matrixStackIn.popPose();
    }


    @Override
    public void render(GobletBlockTile tile, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

        if(!tile.fluidHolder.isEmpty()){

            renderFluid(7/16f, tile.fluidHolder.getTintColor(tile.getLevel(),tile.getBlockPos()), tile.fluidHolder.getFluid().getLuminosity(),
                    tile.fluidHolder.getFluid().getStillTexture(), matrixStackIn,bufferIn,combinedLightIn,combinedOverlayIn,true);
        }
    }
}
