package net.mine_diver.spasm.api.transform;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {
    @NotNull TransformationResult transform(final @NotNull ClassLoader classLoader, final @NotNull ClassNode classNode);
}
