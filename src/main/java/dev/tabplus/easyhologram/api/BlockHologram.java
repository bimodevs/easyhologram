package dev.tabplus.easyhologram.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.BlockDisplayEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * A hologram that shows a floating block in the world.
 * <p>
 * Example usage:
 * <pre>{@code
 * BlockHologram hologram = BlockHologram.builder(position)
 *     .block(Blocks.DIAMOND_BLOCK)
 *     .billboard(true)
 *     .build(world);
 *
 * EasyHologram.getManager().register(hologram);
 * }</pre>
 */
public class BlockHologram extends Hologram {

    private BlockState blockState;
    private boolean billboard;

    private BlockHologram(Builder builder, ServerWorld world) {
        super(builder.id, world, builder.position);
        this.blockState = builder.blockState;
        this.billboard = builder.billboard;
    }

    @Override
    protected Entity createEntity() {
        BlockDisplayEntity entity = new BlockDisplayEntity(EntityType.BLOCK_DISPLAY, world);
        applyProperties(entity);
        return entity;
    }

    @Override
    public String getType() {
        return "block";
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public void setBlockState(BlockState state) {
        this.blockState = state;
        if (displayEntity instanceof BlockDisplayEntity blockDisplay) {
            blockDisplay.getDataTracker().set(BlockDisplayEntity.BLOCK_STATE, state);
        }
    }

    public boolean isBillboard() {
        return billboard;
    }

    // -------------------------------------------------------------------
    //  Internal
    // -------------------------------------------------------------------

    private void applyProperties(BlockDisplayEntity entity) {
        entity.getDataTracker().set(BlockDisplayEntity.BLOCK_STATE, blockState);

        if (billboard) {
            entity.getDataTracker().set(
                    DisplayEntity.BILLBOARD,
                    DisplayEntity.BillboardMode.CENTER.getIndex()
            );
        }
    }

    // -------------------------------------------------------------------
    //  Builder
    // -------------------------------------------------------------------

    public static Builder builder(Vec3d position) {
        return new Builder(position);
    }

    public static class Builder {
        private final Vec3d position;
        private String id;
        private BlockState blockState = Blocks.STONE.getDefaultState();
        private boolean billboard = false;

        private Builder(Vec3d position) {
            this.position = position;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder block(Block block) {
            this.blockState = block.getDefaultState();
            return this;
        }

        public Builder blockState(BlockState state) {
            this.blockState = state;
            return this;
        }

        public Builder billboard(boolean billboard) {
            this.billboard = billboard;
            return this;
        }

        public BlockHologram build(ServerWorld world) {
            if (id == null || id.isEmpty()) {
                id = "block_" + System.currentTimeMillis();
            }
            return new BlockHologram(this, world);
        }
    }
}
