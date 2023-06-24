package net.mine_diver.spasm.impl;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import lombok.var;
import net.mine_diver.spasm.api.transform.ClassTransformer;
import net.mine_diver.spasm.api.transform.TransformationResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;

import static net.mine_diver.spasm.api.transform.TransformationResult.PASS;
import static net.mine_diver.spasm.impl.util.FuncUtil.*;
import static net.mine_diver.spasm.impl.util.Util.*;

@FieldDefaults(
        level = AccessLevel.PRIVATE,
        makeFinal = true
)
class MixinTransformerHook<T extends TreeTransformer & IMixinTransformer> extends MixinTransformerDelegate<T> {
    @NotNull ImmutableList<ClassTransformer> transformers;

    MixinTransformerHook(final @NotNull T delegate, final @NotNull ImmutableList<ClassTransformer> transformers) {
        super(delegate);
        this.transformers = transformers;
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        var transformedClass = basicClass;
        if (basicClass != null && !name.startsWith("org.objectweb.asm.") && !name.startsWith("net.mine_diver.spasm.")) {
            val classNode = make(new ClassNode(), classNode1 -> new ClassReader(basicClass).accept(classNode1, 0));
            switch (transformers.stream()
                    .map($__(ClassTransformer::transform, Thread.currentThread().getContextClassLoader(), classNode))
                    .reduce(PASS, TransformationResult::choose)) {
                case SUCCESS:
                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    classNode.accept(classWriter);
                    transformedClass = classWriter.toByteArray();
                    break;
            }
        }
        return super.transformClassBytes(name, transformedName, transformedClass);
    }
}
