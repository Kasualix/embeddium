package me.jellysquid.mods.sodium.mixin.features.render_layer.leaves;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import net.minecraft.block.Block;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Predicate;

@Mixin(RenderLayers.class)
public class MixinRenderLayers {
    @Mutable
    @Shadow
    @Final
    private static Map<IRegistryDelegate<Block>, Predicate<RenderLayer>> blockRenderChecks;

    @Mutable
    @Shadow
    @Final
    private static Map<IRegistryDelegate<Block>, Predicate<RenderLayer>> fluidRenderChecks;

    static {
        // Replace the backing collection types with something a bit faster, since this is a hot spot in chunk rendering.
        blockRenderChecks = new Object2ObjectOpenHashMap<>(blockRenderChecks);
        fluidRenderChecks = new Object2ObjectOpenHashMap<>(fluidRenderChecks);
    }

    @Unique
    private static boolean embeddium$leavesFancy;

    @Redirect(
            method = { "getBlockLayer", "getMovingBlockLayer", "canRenderInLayer(Lnet/minecraft/block/BlockState;Lnet/minecraft/client/render/RenderLayer;)Z" },
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderLayers;fancyGraphicsOrBetter:Z"))
    private static boolean redirectLeavesShouldBeFancy() {
        return embeddium$leavesFancy;
    }

    @Inject(method = "setFancyGraphicsOrBetter", at = @At("RETURN"))
    private static void onSetFancyGraphicsOrBetter(boolean fancyGraphicsOrBetter, CallbackInfo ci) {
        embeddium$leavesFancy = SodiumClientMod.options().quality.leavesQuality.isFancy(fancyGraphicsOrBetter ? GraphicsMode.FANCY : GraphicsMode.FAST);
    }
}
