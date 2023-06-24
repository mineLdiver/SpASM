package net.mine_diver.spasm.test;

import lombok.val;
import net.mine_diver.spasm.api.transform.ClassTransformer;
import net.mine_diver.spasm.api.transform.TransformationResult;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.PrintStream;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

public class TestTransformer implements ClassTransformer {
    private static final String CLASS_SYSTEM_NAME = Type.getInternalName(System.class);
    private static final String FIELD_SYSTEM_OUT_NAME = "out";
    private static final String FIELD_SYSTEM_OUT_DESC = Type.getDescriptor(PrintStream.class);

    private static final String CLASS_PRINTSTREAM_NAME = Type.getInternalName(PrintStream.class);
    private static final String METHOD_PRINTSTREAM_PRINTLN_NAME = "println";
    private static final String METHOD_PRINTSTREAM_PRINTLN_DESC = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class));

    private static final int METHOD_CLINIT_ACCESS = Modifier.STATIC;
    private static final String METHOD_CLINIT_NAME = "<clinit>";
    private static final String METHOD_CLINIT_DESC = Type.getMethodDescriptor(Type.VOID_TYPE);

    @Override
    public @NotNull TransformationResult transform(final @NotNull ClassLoader classLoader, final @NotNull ClassNode classNode) {
        val insns = new InsnList();
        insns.add(new FieldInsnNode(GETSTATIC, CLASS_SYSTEM_NAME, FIELD_SYSTEM_OUT_NAME, FIELD_SYSTEM_OUT_DESC));
        insns.add(new LdcInsnNode("Loading class: " + classNode.name));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, CLASS_PRINTSTREAM_NAME, METHOD_PRINTSTREAM_PRINTLN_NAME, METHOD_PRINTSTREAM_PRINTLN_DESC));
        classNode.methods
                .stream()
                .filter(methodNode -> METHOD_CLINIT_NAME.equals(methodNode.name))
                .findAny()
                .orElseGet(() -> {
                    val clinit = new MethodNode(METHOD_CLINIT_ACCESS, METHOD_CLINIT_NAME, METHOD_CLINIT_DESC, null, null);
                    clinit.instructions.add(new InsnNode(RETURN));
                    classNode.methods.add(clinit);
                    return clinit;
                })
                .instructions.insert(insns);
        return TransformationResult.SUCCESS;
    }
}
