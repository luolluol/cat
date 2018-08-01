package com.dianping.cat.consumer.forward.service;

public interface ForwardService<T> {

    int forward(T t);
}
