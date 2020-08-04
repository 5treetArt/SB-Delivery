package ru.skillbranch.sbdelivery.extensions


fun <T, R> Iterable<T>.filterMapIndexed(
    predicate: (index: Int, item: T) -> Boolean,
    transform: (index: Int, item: T) -> R
): Iterable<R> {
    val result = mutableListOf<R>()
    forEachIndexed { index, item ->
        if (predicate(index, item)) result.add(transform(index, item))
    }
    return result
}