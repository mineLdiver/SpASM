package net.mine_diver.spasm.api.transform;

import net.mine_diver.spasm.impl.SpASM;

public enum TransformationPhase {
    BEFORE_MIXINS,
    AFTER_MIXINS;

    public static TransformationPhase getCurrent() {
        return SpASM.getCurrentPhase();
    }
}
