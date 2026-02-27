package org.betonquest.betonquest.lib.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Simple helper class for testing to generate subsets.
 */
public final class SimpleSubSetHelper {

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

    /**
     * Generate all permutations of a list.
     *
     * @param items the items to permute
     * @param <T>   the type of the items in the list
     * @return a list of permutations
     */
    public static <T> Stream<List<T>> permutations(final Collection<T> items) {
        return LongStream.range(0, factorial(items.size()))
                .mapToObj(i -> permutationHelper(i, new LinkedList<>(items), new ArrayList<>()));
    }

    private static <T> List<T> permutationHelper(final long position, final List<T> input, final List<T> output) {
        if (input.isEmpty()) {
            return output;
        }
        final long subFactorial = factorial(input.size() - 1);
        output.add(input.remove((int) (position / subFactorial)));
        return permutationHelper((int) (position % subFactorial), input, output);
    }

    private static long factorial(final int number) {
        return LongStream.rangeClosed(2, number).reduce(1, (acc, fac) -> acc * fac);
    }
}
