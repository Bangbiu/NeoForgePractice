package com.empty.nfpractice.util;

import net.minecraft.core.Direction;

public interface LocalFrameData<T extends LocalFrameData> {
    public T faceTo(Direction dir);
}
