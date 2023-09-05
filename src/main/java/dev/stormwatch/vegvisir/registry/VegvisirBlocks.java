package dev.stormwatch.vegvisir.registry;

import dev.stormwatch.vegvisir.Vegvisir;
import dev.stormwatch.vegvisir.blocks.OrbBlock;
import dev.stormwatch.vegvisir.blocks.SpinningWheelBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class VegvisirBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Vegvisir.MOD_ID);

    public static final RegistryObject<Block> SPINNING_WHEEL_BLOCK = registerBlock("spinning_wheel",
            () -> new SpinningWheelBlock(BlockBehaviour.Properties.of(Material.BAMBOO)
                    .strength(1f, 5f)
                    .noParticlesOnBreak()
                    // TODO: this doesnt drop itself, currently drops itself in dropOnRemove
                    .noOcclusion()));

    public static final RegistryObject<Block> ORB_BLOCK = registerBlock("orb_block",
            () -> new OrbBlock(BlockBehaviour.Properties.of(Material.WOOD)
                    .noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(name, supplier);
        registerBlockItem(name, block);
        return block;
    }
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return VegvisirItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
