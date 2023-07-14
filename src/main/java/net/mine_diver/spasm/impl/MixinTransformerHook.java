package net.mine_diver.spasm.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.mine_diver.spasm.api.transform.TransformationResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.transformers.TreeTransformer;

import static net.mine_diver.spasm.api.transform.TransformationResult.PASS;
import static net.mine_diver.spasm.impl.SpASM.RAW_TRANSFORMERS;
import static net.mine_diver.spasm.impl.SpASM.TRANSFORMERS;

@FieldDefaults(
        level = AccessLevel.PRIVATE,
        makeFinal = true
)
class MixinTransformerHook<T extends TreeTransformer & IMixinTransformer> extends MixinTransformerDelegate<T> {
    MixinTransformerHook(T delegate) {
        super(delegate);
    }

    @Override
    public byte[] transformClassBytes(String name, String transformedName, byte[] basicClass) {
        if (basicClass != null && !name.startsWith("org.objectweb.asm.") && !name.startsWith("net.mine_diver.spasm.")) {
            val classLoader = Thread.currentThread().getContextClassLoader();
            for (int i = 0; i < RAW_TRANSFORMERS.size(); i++) {
                val transformationResult = RAW_TRANSFORMERS.get(i).transform(classLoader, name, basicClass);
                if (transformationResult.isPresent()) basicClass = transformationResult.get();
            }
            val classNode = new ClassNode();
            new ClassReader(basicClass).accept(classNode, 0);
            switch (TRANSFORMERS.stream()
                    .map(classTransformer -> classTransformer.transform(classLoader, classNode))
                    .reduce(PASS, TransformationResult::choose)) {
                case SUCCESS:
                    val classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    classNode.accept(classWriter);
                    basicClass = classWriter.toByteArray();
                    break;
            }
        }
        return super.transformClassBytes(name, transformedName, basicClass);
    }
}
