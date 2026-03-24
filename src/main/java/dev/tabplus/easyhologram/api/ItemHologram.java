package dev.tabplus.easyhologram.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * A hologram that shows a floating item in the world.
 * <p>
 * Example usage:
 * <pre>{@code
 * ItemHologram hologram = ItemHologram.builder(position)
 *     .item(Items.DIAMOND_SWORD)
 *     .billboard(true)
 *     .build(world);
 *
 * EasyHologram.getManager().register(hologram);
 * }</pre>
 */
public class ItemHologram extends Hologram {

    private ItemStack itemStack;
    private boolean billboard;

    private ItemHologram(Builder builder, ServerWorld world) {
        super(builder.id, world, builder.position);
        this.itemStack = builder.itemStack;
        this.billboard = builder.billboard;
    }

    @Override
    protected Entity createEntity() {
        ItemDisplayEntity entity = new ItemDisplayEntity(EntityType.ITEM_DISPLAY, world);
        applyProperties(entity);
        return entity;
    }

    @Override
    public String getType() {
        return "item";
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack stack) {
        this.itemStack = stack;
        if (displayEntity instanceof ItemDisplayEntity itemDisplay) {
            itemDisplay.getDataTracker().set(ItemDisplayEntity.ITEM, stack);
        }
    }

    public boolean isBillboard() {
        return billboard;
    }

    // -------------------------------------------------------------------
    //  Internal
    // -------------------------------------------------------------------

    private void applyProperties(ItemDisplayEntity entity) {
        entity.getDataTracker().set(ItemDisplayEntity.ITEM, itemStack);

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
        private ItemStack itemStack = new ItemStack(Items.STONE);
        private boolean billboard = true;

        private Builder(Vec3d position) {
            this.position = position;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder item(Item item) {
            this.itemStack = new ItemStack(item);
            return this;
        }

        public Builder itemStack(ItemStack stack) {
            this.itemStack = stack.copy();
            return this;
        }

        public Builder billboard(boolean billboard) {
            this.billboard = billboard;
            return this;
        }

        public ItemHologram build(ServerWorld world) {
            if (id == null || id.isEmpty()) {
                id = "item_" + System.currentTimeMillis();
            }
            return new ItemHologram(this, world);
        }
    }
}
