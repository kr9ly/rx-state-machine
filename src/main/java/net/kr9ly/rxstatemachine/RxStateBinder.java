package net.kr9ly.rxstatemachine;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

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
public class RxStateBinder<State> {

    private static final Object NULL = new Object();

    private final RxStateMachine<State> stateMachine;

    public RxStateBinder(RxStateMachine<State> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public <T> Observable.Transformer<? super T, ? extends T> bindToState(final Class<? extends State> start, final Class<? extends State> end) {
        final Observable<Boolean> stateObservable = stateMachine.enterObservable().filter(new Func1<Class<? extends State>, Boolean>() {
            @Override
            public Boolean call(Class<? extends State> state) {
                return start == state || end == state;
            }
        }).distinctUntilChanged().map(new Func1<Class<? extends State>, Boolean>() {
            @Override
            public Boolean call(Class<? extends State> state) {
                return start == state;
            }
        });

        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> source) {
                return Observable.concat(
                        stateObservable.filter(new Func1<Boolean, Boolean>() {
                            @Override
                            public Boolean call(Boolean isActive) {
                                return isActive;
                            }
                        })
                                .map(new Func1<Boolean, Observable<? extends T>>() {
                                    @Override
                                    public Observable<? extends T> call(Boolean isActive) {
                                        return Observable.combineLatest(
                                                source,
                                                stateObservable.startWith(true),
                                                new Func2<T, Boolean, Object>() {
                                                    @Override
                                                    public Object call(T elem, Boolean isActive) {
                                                        return isActive ? elem : NULL;
                                                    }
                                                }
                                        ).takeWhile(new Func1<Object, Boolean>() {
                                            @Override
                                            public Boolean call(Object o) {
                                                return o != NULL;
                                            }
                                        }).map(new Func1<Object, T>() {
                                            @Override
                                            @SuppressWarnings("unchecked")
                                            public T call(Object o) {
                                                return (T) o;
                                            }
                                        });
                                    }
                                })
                );
            }
        };
    }
}
