package net.mine_diver.spasm.test;

import net.mine_diver.spasm.api.transform.RawClassTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class TestRawTransformer implements RawClassTransformer {
    @Override
    public @NotNull Optional<byte[]> transform(@NotNull ClassLoader classLoader, @NotNull String className, byte @NotNull [] classBytes) {
        System.out.println("Raw transformer! " + className + " " + Arrays.hashCode(classBytes));
        return Optional.empty();
    }
}
