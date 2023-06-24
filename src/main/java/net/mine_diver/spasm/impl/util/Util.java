package net.mine_diver.spasm.impl.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

@UtilityClass
public class Util {
    @Contract("_, _ -> param1")
    public <T> T make(final T instance, final Consumer<T> initializer) {
        initializer.accept(instance);
        return instance;
    }
}
