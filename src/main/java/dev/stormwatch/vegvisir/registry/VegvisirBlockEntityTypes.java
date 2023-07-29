package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.blockentities.OrbBlockEntity;
import dev.stormwatch.vegvisir.blockentities.SpinningWheelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class VegvisirBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Vegvisir.MOD_ID);

    public static final RegistryObject<BlockEntityType<SpinningWheelBlockEntity>> SPINNING_WHEEL_BLOCK_ENTITY = BLOCK_ENTITIES.register("spinning_wheel_block_entity",
            () -> BlockEntityType.Builder.of(SpinningWheelBlockEntity::new, VegvisirBlocks.SPINNING_WHEEL_BLOCK.get()).build(null));

    public static final RegistryObject<BlockEntityType<OrbBlockEntity>> ORB_BLOCK_ENTITY = BLOCK_ENTITIES.register("orb_block_entity",
            () -> BlockEntityType.Builder.of(OrbBlockEntity::new, VegvisirBlocks.ORB_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
