package com.newj.model;

import org.jetbrains.annotations.Nullable;
import org.babyfish.jimmer.sql.*;

import java.util.List;

/**
 * @ClassName BookStore
 * @Description TODO
 * @Author Caleb
 * @Date 2025-08-02 17:47:01
 */
@Entity
public interface BookStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id();

    @Key
    String name();

    @Nullable
    String website();

    @OneToMany(mappedBy = "store")
    List<Book> books();
}