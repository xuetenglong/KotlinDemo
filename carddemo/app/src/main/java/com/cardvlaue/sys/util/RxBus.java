package com.cardvlaue.sys.util;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 此类废弃，使用 RxBus2 {@link RxBus2}
 */
@Deprecated
public class RxBus {

    private static RxBus defaultBus = new RxBus();

    private final Subject<Object, Object> subject = new SerializedSubject<>(
        PublishSubject.create());

    private RxBus() {
    }

    public synchronized static RxBus getDefaultBus() {
        return defaultBus;
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public void send(Object o) {
        if (hasObservers()) {
            subject.onNext(o);
        }
    }

    public Observable<Object> toObserverable() {
        return subject;
    }
}
