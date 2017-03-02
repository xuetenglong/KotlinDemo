package com.cardvlaue.sys.util

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RxBus2 {

    private val subjects: PublishSubject<Any> = PublishSubject.create()

    private val subject: Subject<Any> = subjects.toSerialized()

    fun send(event: Any) {
        subject.onNext(event)
    }

    fun toObservable() = subjects

    fun hasObservers() = subjects.hasObservers()

    companion object {

        private val bus: RxBus2 = RxBus2()

        @Synchronized fun get() = bus
    }

}