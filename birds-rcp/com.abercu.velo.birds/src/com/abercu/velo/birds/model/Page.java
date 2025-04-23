package com.abercu.velo.birds.model;

import java.util.List;

public class Page<T> {
    public List<T> content;
    public int totalPages;
    public int totalElements;
    public int size;
    public int number;
}
