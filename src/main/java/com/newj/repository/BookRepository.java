package com.newj.repository;

import com.newj.model.Gender;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.babyfish.jimmer.Page;

import com.newj.model.Book;
import com.newj.model.BookTable;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class BookRepository {

    private final JSqlClient jsqlClient;
    private static final BookTable T = BookTable.$;

    public Page<Book> findBooks(

            int pageIndex, // 从0开始
            int pageSize,

            @Nullable Fetcher<Book> fetcher,

            // 例如: "name asc, edition desc"
            @Nullable String sortCode,

            @Nullable String name,
            @Nullable BigDecimal minPrice,
            @Nullable BigDecimal maxPrice,
            @Nullable String storeName,
            @Nullable String storeWebsite,
            @Nullable String authorName,
            @Nullable Gender authorGender
    ) {
        return jsqlClient
                .createQuery(T)
                .where(T.name().ilikeIf(name))
                .where(T.price().betweenIf(minPrice, maxPrice))
                .where(T.store().name().ilikeIf(storeName))
                .where(T.store().website().ilikeIf(storeWebsite))
                .where(
                        T.authors(author ->
                                Predicate.or(
                                        author.firstName().ilikeIf(authorName),
                                        author.lastName().ilikeIf(authorName)
                                )
                        )
                )
                .where(T.authors(author -> author.gender().eqIf(authorGender)))
                .orderBy(
                        Order.makeOrders(
                                T,
                                sortCode != null ?
                                        sortCode :
                                        "name asc, edition desc"
                        )
                )
                .select(
                        T.fetch(fetcher)
                )
                .fetchPage(pageIndex, pageSize);
    }

    public long saveBook(BookInput input) {
        return jsqlClient
                .save(input)
                .execute()
                .getModifiedEntity()
                // 如果`input.id`为null，返回自动分配的id
                .getId();
    }
}