package net.mine_diver.spasm.test;

import com.google.common.collect.ImmutableSet;
import net.mine_diver.spasm.api.transform.RawClassTransformer;
import net.mine_diver.spasm.api.transform.TransformationPhase;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class AllPhaseTransformer implements RawClassTransformer {
    @Override
    public @NotNull ImmutableSet<TransformationPhase> getPhases() {
        return ALL_PHASES;
    }

    @Override
    public @NotNull Optional<byte[]> transform(@NotNull ClassLoader classLoader, @NotNull String className, byte @NotNull [] classBytes) {
        System.out.println("Phase transformer! " + className + " " + Arrays.hashCode(classBytes) + " " + TransformationPhase.getCurrent());
        return Optional.empty();
    }
}
