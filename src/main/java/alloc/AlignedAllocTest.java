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

        var layout = ValueLayout.JAVA_LONG;
        final long alignment = layout.byteAlignment();
        final long size = layout.byteSize();
        MemorySegment segment = ((MemorySegment) MH_aligned_alloc.invokeExact(PFN_aligned_alloc, alignment, size))
                .reinterpret(size);
        System.out.println(segment);
        System.out.println(segment.get(layout, 0));
        segment.set(layout, 0, 42);
        System.out.println(segment.get(layout, 0));
        MH_free.invokeExact(PFN_free, segment);
    }
}
