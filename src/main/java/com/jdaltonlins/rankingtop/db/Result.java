package com.jdaltonlins.rankingtop.db;

public class Result<T> {

    private static Result EMPTY = new Result(null, null);

    private T value;
    private String raw;

    public static <T> Result<T> empty() {
        return EMPTY;
    }

    public static <T> Result<T> of(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> ofRaw(String value) {
        return new Result<>(null, value);
    }

    private Result(T value, String raw) {
        this.value = value;
        this.raw = raw;
    }

    public Result<T> or(String raw) {
        return this == EMPTY ? new Result<>(null, raw) : this;
    }

    public Result<T> or(T value) {
        return this == EMPTY ? new Result<>(value, null) : this;
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public T getValue() {
        return value;
    }

    public String toString() {
        return raw != null ? raw : value.toString();
    }
}
