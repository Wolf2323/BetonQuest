package org.betonquest.betonquest.kernel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simple helper class for testing to generate subsets.
 */
final class SimpleSubSetHelper {

    /**
     * Private constructor.
     */
    private SimpleSubSetHelper() {

    }

    /**
     * Generate in order subsets of length subSetLength from a list.
     *
     * @param initialList  the initial list
     * @param subSetLength the subset length
     * @param <T>          the type of the elements in the list
     * @return a list of subsets
     */
    public static <T> List<Set<T>> kSubSets(final List<T> initialList, final int subSetLength) {
        return IntStream.iterate(0, i -> i + 1)
                .limit(initialList.size() - subSetLength + 1)
                .boxed()
                .map(i -> new HashSet<>(initialList.subList(i, i + subSetLength)))
                .collect(Collectors.toList());
    }

    /**
     * Generate subsets of all possible lengths.
     *
     * @param initialList the initial list
     * @param <T>         the type of the elements in the list
     * @return a list of subsets
     */
    public static <T> List<Set<T>> allKSubSets(final List<T> initialList) {
        return IntStream.range(1, initialList.size())
                .boxed()
                .flatMap(i -> kSubSets(initialList, i).stream())
                .collect(Collectors.toList());
    }
}
