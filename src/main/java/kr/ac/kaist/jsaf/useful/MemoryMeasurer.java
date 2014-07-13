/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.useful;

import java.util.List;
import java.lang.management.*;

public class MemoryMeasurer {
	private static final Runtime runtime = Runtime.getRuntime();
	private static int gcRepeat = 4;
	
	/* peak memory */
	public static double peakMemory() {
		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		Long peakMemory = 0l;
		for (MemoryPoolMXBean pool : pools) {
			if (pool.getType() == MemoryType.HEAP) {
				peakMemory += pool.getPeakUsage().getUsed();
			}
		}
		return ((double) peakMemory) / 1048576.0d;
	}
	
	/* heap */
	public static double measureHeap() {
		for (int i = 0; i < gcRepeat; i++)
			_runGC();
		return ((double) (usedMemory())) / 1048576.0d;
	}
	private static void _runGC() {
		long usedMem1 = usedMemory(), usedMem2 = Long.MAX_VALUE;
		for (int i = 0; (usedMem1 < usedMem2) && (i < 500); i++) {
			runtime.runFinalization();
			runtime.gc();
			Thread.yield();
			usedMem2 = usedMem1;
			usedMem1 = usedMemory();
		}
	}
	private static long usedMemory() {
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
