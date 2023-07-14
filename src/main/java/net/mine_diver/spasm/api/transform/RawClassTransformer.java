package net.mine_diver.spasm.api.transform;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface RawClassTransformer {
    @NotNull Optional<byte[]> transform(final @NotNull ClassLoader classLoader, final @NotNull String className, final byte @NotNull [] classBytes);
}
