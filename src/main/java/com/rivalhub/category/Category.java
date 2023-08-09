package com.rivalhub.category;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class Category {
    @Id
    private long categoryId;
}
