package org.nico.vaultfall.client.render;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.nico.vaultfall.item.ModItems;

/**
 * Renderer para la pieza de Exo-Pecho (EXO_TORSO).
 * Lee dinámicamente el ContainerComponent para decidir qué módulos visuales renderizar.
 */
@Environment(EnvType.CLIENT)
public class ExoTorsoRenderer implements TrinketRenderer {
    private static final Identifier ELYTRA_TEXTURE = Identifier.ofVanilla("textures/entity/elytra.png");

    // Lazy initialization para el modelo
    private ElytraEntityModel<LivingEntity> elytraModel;

    public ExoTorsoRenderer() {
        // Constructor vacío: el modelo se carga solo al primer render
    }

    private void ensureModelLoaded() {
        if (this.elytraModel == null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null) {
                var loader = client.getEntityModelLoader();
                if (loader != null) {
                    ModelPart modelPart = loader.getModelPart(EntityModelLayers.ELYTRA);
                    this.elytraModel = new ElytraEntityModel<>(modelPart);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, SlotReference slot,
                       EntityModel<? extends LivingEntity> contextModel,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                       int light, LivingEntity entity,
                       float limbAngle, float limbDistance, float tickDelta,
                       float animationProgress, float headYaw, float headPitch) {

        // 1. Leer el ContainerComponent de la Exo-Pechera
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return;

        // 2. Buscar si el módulo Elytra está dentro del contenedor
        ItemStack module = container.stream().findFirst().orElse(ItemStack.EMPTY);
        if (!module.isOf(ModItems.MODULO_ELYTRA)) return;

        // 3. Renderizar las alas
        this.ensureModelLoaded();
        if (this.elytraModel == null) return;

        matrices.push();

        // Copiar el estado del modelo del jugador (para que las alas sigan el movimiento del torso)
        contextModel.copyStateTo((EntityModel) this.elytraModel);

        // Posicionar las alas en la espalda
        matrices.translate(0.0F, 0.0F, 0.125F);

        // Configurar los ángulos del modelo
        this.elytraModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

        // Obtener el buffer de renderizado
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
                RenderLayer.getEntityTranslucent(ELYTRA_TEXTURE)
        );

        // Renderizar el modelo
        this.elytraModel.render(
                matrices,
                vertexConsumer,
                light,
                OverlayTexture.DEFAULT_UV,
                0xFFFFFFFF
        );

        matrices.pop();
    }
}
