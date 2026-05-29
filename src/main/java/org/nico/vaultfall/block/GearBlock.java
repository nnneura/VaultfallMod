package org.nico.vaultfall.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult; // <-- NUEVO IMPORT
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GearBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);
    private final boolean isOxidized;

    public GearBlock(Settings settings, boolean isOxidized) {
        super(settings);
        this.isOxidized = isOxidized;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Evaluamos si el bloque está oxidado y lo tocamos con un hacha
        if (this.isOxidized && stack.getItem() instanceof AxeItem) {
            if (!world.isClient) {
                // Reemplaza el bloque por la versión limpia
                world.setBlockState(pos, ModBlocks.ENGRANAJE_BLOCK.getDefaultState());

                // Sonido oficial de raspado
                world.playSound(null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);

                // Desgasta el hacha en 1 punto
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    EquipmentSlot slot = (hand == Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    stack.damage(1, serverPlayer, slot);
                }
            }
            // Retornamos el éxito con el nuevo formato ItemActionResult
            return ItemActionResult.SUCCESS;
        }
        // Fallback al comportamiento por defecto de la superclase
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}