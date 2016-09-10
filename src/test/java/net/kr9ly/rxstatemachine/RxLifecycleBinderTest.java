package net.kr9ly.rxstatemachine;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
public class RxLifecycleBinderTest {

    @Test
    public void testWithColdObservable() throws InterruptedException {
        Observable<Integer> events = Observable.from(Arrays.asList(1, 2, 3, 4, 5))
                .delaySubscription(1, TimeUnit.MILLISECONDS)
                .delay(1, TimeUnit.MILLISECONDS);

        final Queue<Integer> livingQueue = new LinkedBlockingQueue<Integer>();
        final Queue<Integer> visibleQueue = new LinkedBlockingQueue<Integer>();
        final Queue<Integer> runningQueue = new LinkedBlockingQueue<Integer>();

        RxLifecycleBinder binder = new RxLifecycleBinder();

        events.compose(binder.<Integer>emitWhileLiving())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer i) {
                        livingQueue.add(i);
                    }
                });

        events.compose(binder.<Integer>emitWhileVisible())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer i) {
                        visibleQueue.add(i);
                    }
                });

        events.compose(binder.<Integer>emitWhileRunning())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer i) {
                        runningQueue.add(i);
                    }
                });

        binder.onCreate();
        Thread.sleep(20);
        {
            binder.onStart();
            Thread.sleep(20);
            {
                binder.onResume();
                Thread.sleep(20);
                binder.onPause();
            }
            {
                binder.onResume();
                Thread.sleep(20);
                binder.onPause();
            }
            binder.onStop();
        }
        {
            binder.onStart();
            Thread.sleep(20);
            {
                binder.onResume();
                Thread.sleep(20);
                binder.onPause();
            }
            binder.onStop();
        }
        binder.onDestroy();

        assertEquals(Integer.valueOf(1), livingQueue.poll());
        assertEquals(Integer.valueOf(2), livingQueue.poll());
        assertEquals(Integer.valueOf(3), livingQueue.poll());
        assertEquals(Integer.valueOf(4), livingQueue.poll());
        assertEquals(Integer.valueOf(5), livingQueue.poll());
        assertNull(livingQueue.poll());

        assertEquals(Integer.valueOf(1), visibleQueue.poll());
        assertEquals(Integer.valueOf(2), visibleQueue.poll());
        assertEquals(Integer.valueOf(3), visibleQueue.poll());
        assertEquals(Integer.valueOf(4), visibleQueue.poll());
        assertEquals(Integer.valueOf(5), visibleQueue.poll());
        assertEquals(Integer.valueOf(1), visibleQueue.poll());
        assertEquals(Integer.valueOf(2), visibleQueue.poll());
        assertEquals(Integer.valueOf(3), visibleQueue.poll());
        assertEquals(Integer.valueOf(4), visibleQueue.poll());
        assertEquals(Integer.valueOf(5), visibleQueue.poll());
        assertNull(visibleQueue.poll());

        assertEquals(Integer.valueOf(1), runningQueue.poll());
        assertEquals(Integer.valueOf(2), runningQueue.poll());
        assertEquals(Integer.valueOf(3), runningQueue.poll());
        assertEquals(Integer.valueOf(4), runningQueue.poll());
        assertEquals(Integer.valueOf(5), runningQueue.poll());
        assertEquals(Integer.valueOf(1), runningQueue.poll());
        assertEquals(Integer.valueOf(2), runningQueue.poll());
        assertEquals(Integer.valueOf(3), runningQueue.poll());
        assertEquals(Integer.valueOf(4), runningQueue.poll());
        assertEquals(Integer.valueOf(5), runningQueue.poll());
        assertEquals(Integer.valueOf(1), runningQueue.poll());
        assertEquals(Integer.valueOf(2), runningQueue.poll());
        assertEquals(Integer.valueOf(3), runningQueue.poll());
        assertEquals(Integer.valueOf(4), runningQueue.poll());
        assertEquals(Integer.valueOf(5), runningQueue.poll());
        assertNull(runningQueue.poll());
    }

    @Test
    public void testWithHotObservable() {
        Subject<String, String> events = PublishSubject.create();

        final Queue<String> livingQueue = new LinkedBlockingQueue<String>();
        final Queue<String> visibleQueue = new LinkedBlockingQueue<String>();
        final Queue<String> runningQueue = new LinkedBlockingQueue<String>();

        RxLifecycleBinder binder = new RxLifecycleBinder();

        events.compose(binder.<String>emitWhileLiving())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        livingQueue.add(s);
                    }
                });

        events.compose(binder.<String>emitWhileVisible())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        visibleQueue.add(s);
                    }
                });

        events.compose(binder.<String>emitWhileRunning())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        runningQueue.add(s);
                    }
                });

        events.onNext("a");
        {
            binder.onCreate();
            events.onNext("b");
            {
                binder.onStart();
                events.onNext("c");
                {
                    binder.onResume();
                    events.onNext("d");
                    binder.onPause();
                }
                events.onNext("e");
                {
                    binder.onResume();
                    events.onNext("f");
                    binder.onPause();
                }
                events.onNext("g");
                binder.onStop();
            }
            events.onNext("h");
            {
                binder.onStart();
                events.onNext("i");
                binder.onStop();
            }
            events.onNext("j");
            binder.onDestroy();
        }
        events.onNext("k");

        assertEquals("b", livingQueue.poll());
        assertEquals("c", livingQueue.poll());
        assertEquals("d", livingQueue.poll());
        assertEquals("e", livingQueue.poll());
        assertEquals("f", livingQueue.poll());
        assertEquals("g", livingQueue.poll());
        assertEquals("h", livingQueue.poll());
        assertEquals("i", livingQueue.poll());
        assertEquals("j", livingQueue.poll());
        assertNull(livingQueue.poll());

        assertEquals("c", visibleQueue.poll());
        assertEquals("d", visibleQueue.poll());
        assertEquals("e", visibleQueue.poll());
        assertEquals("f", visibleQueue.poll());
        assertEquals("g", visibleQueue.poll());

        assertEquals("i", visibleQueue.poll());
        assertNull(visibleQueue.poll());

        assertEquals("d", runningQueue.poll());
        assertEquals("f", runningQueue.poll());
        assertNull(runningQueue.poll());
    }
}
