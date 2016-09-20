package net.kr9ly.rxstatemachine;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Copyright 2016 kr9ly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RxStateMachine<T> {

    private final Subject<Class<? extends T>, Class<? extends T>> stateQueue = new SerializedSubject<Class<? extends T>, Class<? extends T>>(BehaviorSubject.<Class<? extends T>>create());

    private final Observable<Class<? extends T>> prevStateQueue = stateQueue.skipLast(1);

    public RxStateMachine(Class<? extends T> initialState) {
        stateQueue.onNext(initialState);
    }

    @SuppressWarnings("unchecked")
    public <U extends T> U expect(final Class<U> expected) {
        return (U) Proxy.newProxyInstance(expected.getClassLoader(), new Class[]{expected}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                final Class<? extends T> nextState = (Class<? extends T>) method.getReturnType();
                stateQueue
                        .take(1)
                        .filter(new Func1<Class<? extends T>, Boolean>() {
                            @Override
                            public Boolean call(Class<? extends T> state) {
                                return expected.equals(state);
                            }
                        })
                        .subscribe(new Action1<Class<? extends T>>() {
                            @Override
                            public void call(Class<? extends T> state) {
                                stateQueue.onNext(nextState);
                            }
                        });
                return expect(nextState);
            }
        });
    }

    public Observable<Class<? extends T>> enterObservable() {
        return stateQueue
                .onBackpressureBuffer();
    }

    public Observable<Class<? extends T>> exitObservable() {
        return prevStateQueue
                .onBackpressureBuffer();
    }
}
