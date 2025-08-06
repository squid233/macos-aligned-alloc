package alloc;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

public class AlignedAllocTest {
    private static final Linker LINKER = Linker.nativeLinker();
    private static final MethodHandle MH_aligned_alloc = LINKER.downcallHandle(FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_LONG, ValueLayout.JAVA_LONG));
    private static final MethodHandle MH_free = LINKER.downcallHandle(FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

    public static void main(String[] args) throws Throwable {
        SymbolLookup lookup = Linker.nativeLinker().defaultLookup();
        MemorySegment PFN_aligned_alloc = lookup.findOrThrow("aligned_alloc");
        MemorySegment PFN_free = lookup.findOrThrow("free");

        MemorySegment segment = (MemorySegment) MH_aligned_alloc.invokeExact(PFN_aligned_alloc, 1024L, 1024L * 4L);
        System.out.println(segment);
        MH_free.invokeExact(PFN_free, segment);
    }
}
