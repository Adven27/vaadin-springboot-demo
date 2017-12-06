package com.sberbank.cms.ui.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class ModifiedEvent<T> implements Serializable {
    private final T entity;
}