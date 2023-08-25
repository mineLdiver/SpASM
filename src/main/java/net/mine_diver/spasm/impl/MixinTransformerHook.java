package net.mine_diver.spasm.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.mine_diver.spasm.api.transform.TransformationPhase;
import net.mine_diver.spasm.api.transform.TransformationResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import static net.mine_diver.spasm.api.transform.TransformationResult.PASS;
import static net.mine_diver.spasm.impl.SpASM.RAW_TRANSFORMERS;
import static net.mine_diver.spasm.impl.SpASM.TRANSFORMERS;

@FieldDefaults(
        level = AccessLevel.PRIVATE,
        makeFinal = true
)
class MixinTransformerHook<T extends TreeTransformer & IMixinTransformer> extends MixinTransformerDelegate<T> {
    Deque<String> transformationStack = new ArrayDeque<>();

    MixinTransformerHook(T delegate) {
        super(delegate);
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || Objects.equals(transformationStack.peek(), name)) return super.transformClassBytes(name, transformedName, basicClass);
        transformationStack.push(name);
        val classLoader = Thread.currentThread().getContextClassLoader();
        basicClass = transform(name, basicClass, classLoader, TransformationPhase.BEFORE_MIXINS);
        basicClass = super.transformClassBytes(name, transformedName, basicClass);
        basicClass = transform(name, basicClass, classLoader, TransformationPhase.AFTER_MIXINS);
        transformationStack.pop();
        return basicClass;
    }

    private static byte[] transform(String name, byte[] basicClass, ClassLoader classLoader, TransformationPhase phase) {
        SpASM.currentPhase = phase;
        for (int i = 0; i < RAW_TRANSFORMERS.size(); i++) {
            val transformer = RAW_TRANSFORMERS.get(i);
            if (!transformer.getPhases().contains(phase)) continue;
            val transformationResult = transformer.transform(classLoader, name, basicClass);
            if (transformationResult.isPresent()) basicClass = transformationResult.get();
        }
        val classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);
        if (TRANSFORMERS
                .stream()
                .filter(transformer -> transformer.getPhases().contains(phase))
                .map(classTransformer -> classTransformer.transform(classLoader, classNode))
                .reduce(PASS, TransformationResult::choose)
                == TransformationResult.SUCCESS) {
            val classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(classWriter);
            basicClass = classWriter.toByteArray();
        }
        SpASM.currentPhase = null;
        return basicClass;
    }
}
