package com.blakebr0.mysticalagriculture.augment;

import com.blakebr0.cucumber.helper.ColorHelper;
import com.blakebr0.mysticalagriculture.api.tinkering.Augment;
import com.blakebr0.mysticalagriculture.api.tinkering.AugmentType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

public class TillingAOEAugment extends Augment {
    private static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));
    private final int range;

    public TillingAOEAugment(ResourceLocation id, int tier, int range) {
        super(id, tier, EnumSet.of(AugmentType.HOE), getColor(0xB9855C, tier), getColor(0x593D29, tier));
        this.range = range;
    }

    @Override
    public boolean onItemUse(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null)
            return false;

        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        InteractionHand hand = context.getHand();

        boolean playedSound = false;

        if (tryTill(stack, player, world, pos, direction, hand)) {
            world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

            playedSound = true;

            if (!player.isCrouching())
                return false;
        }

        if (player.isCrouching()) {
            Iterator<BlockPos> positions = BlockPos.betweenClosedStream(pos.offset(-this.range, 0, -this.range), pos.offset(this.range, 0, this.range)).iterator();

            while (positions.hasNext()) {
                BlockPos aoePos = positions.next();

                if (tryTill(stack, player, world, aoePos, direction, hand) && !playedSound) {
                    world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                    playedSound = true;
                }
            }
        }

        return true;
    }

    // TODO: ForgeHooks.onUseHoe
    private static boolean tryTill(ItemStack stack, Player player, Level world, BlockPos pos, Direction direction, InteractionHand hand) {
        if (direction != Direction.DOWN && world.isEmptyBlock(pos.above())) {
            BlockState state = HOE_LOOKUP.get(world.getBlockState(pos).getBlock());
            if (state != null) {
                if (!world.isClientSide()) {
                    world.setBlock(pos, state, 11);
                    if (player != null) {
                        stack.hurtAndBreak(1, player, (entity) -> {
                            entity.broadcastBreakEvent(hand);
                        });
                    }
                }

                return true;
            }
        }

        return false;
    }

    private static int getColor(int color, int tier) {
        return ColorHelper.saturate(color, Math.min((float) tier / 5, 1));
    }
}
