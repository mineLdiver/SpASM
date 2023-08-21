package net.mine_diver.spasm.api.transform;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public interface PhaseListener {
    ImmutableSet<TransformationPhase>
            DEFAULT_PHASES = Sets.immutableEnumSet(TransformationPhase.BEFORE_MIXINS),
            ALL_PHASES = Sets.immutableEnumSet(EnumSet.allOf(TransformationPhase.class));


    default @NotNull ImmutableSet<TransformationPhase> getPhases() {
        return DEFAULT_PHASES;
    }
}
