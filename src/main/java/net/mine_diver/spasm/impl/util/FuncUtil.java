package net.mine_diver.spasm.impl.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@UtilityClass
public class FuncUtil {
    public <A, B, C, R> @NotNull Function<A, R> $__(final @NotNull TriFunction<A, B, C, R> function, B b, C c) {
        return a -> function.apply(a, b, c);
    }
}
