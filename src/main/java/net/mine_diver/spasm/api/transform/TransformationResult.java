package net.mine_diver.spasm.api.transform;

public enum TransformationResult {
    /**
     * Transformer didn't apply any changes.
     */
    PASS,
    /**
     * Transformer successfully applied changes.
     */
    SUCCESS;

    public static TransformationResult choose(TransformationResult result1, TransformationResult result2) {
        return result1 == SUCCESS || result2 == SUCCESS ? SUCCESS : PASS;
    }
}
