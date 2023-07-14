package net.mine_diver.spasm.impl;

import com.google.common.collect.ImmutableList;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.mine_diver.spasm.api.transform.ClassTransformer;
import net.mine_diver.spasm.api.transform.RawClassTransformer;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;

import java.util.List;
import java.util.Set;

public class SpASM implements IMixinConfigPlugin {
    static final ImmutableList<ClassTransformer> TRANSFORMERS = entrypoint("transformer", ClassTransformer.class);
    static final ImmutableList<RawClassTransformer> RAW_TRANSFORMERS = entrypoint("raw_transformer", RawClassTransformer.class);

    static {
        try {
            hook();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <ENTRYPOINT> ImmutableList<ENTRYPOINT> entrypoint(final @NotNull String key, final @NotNull Class<ENTRYPOINT> entrypointClass) {
        return ImmutableList.copyOf(FabricLoader.getInstance().getEntrypoints("spasm:" + key, entrypointClass));
    }

    private static <T extends TreeTransformer & IMixinTransformer> void hook() throws NoSuchFieldException, IllegalAccessException {
        val knotClassLoader = SpASM.class.getClassLoader();
        val knotClassDelegateField = knotClassLoader.getClass().getDeclaredField("delegate");
        knotClassDelegateField.setAccessible(true);
        val knotClassDelegate = knotClassDelegateField.get(knotClassLoader);
        val mixinTransformerField = knotClassDelegate.getClass().getDeclaredField("mixinTransformer");
        mixinTransformerField.setAccessible(true);
        //noinspection unchecked
        mixinTransformerField.set(knotClassDelegate, new MixinTransformerHook<>((T) mixinTransformerField.get(knotClassDelegate)));
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
