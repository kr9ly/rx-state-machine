package net.kr9ly.rxstatemachine;

import org.junit.Before;
import org.junit.Test;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

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
public class RxStateMachineTest {

    private TestSubscriber<Class<? extends LifecycleState>> enterSubscriber;

    private TestSubscriber<Class<? extends LifecycleState>> exitSubscriber;

    private RxStateMachine<LifecycleState> stateMachine;

    @Before
    public void prepareStateMachine() {
        enterSubscriber = new TestSubscriber<Class<? extends LifecycleState>>();
        exitSubscriber = new TestSubscriber<Class<? extends LifecycleState>>();
        stateMachine = new RxStateMachine<LifecycleState>(LifecycleState.Default.class);
        stateMachine.enterObservable().subscribe(enterSubscriber);
        stateMachine.exitObservable().subscribe(exitSubscriber);
    }

    @Test
    public void testStateTransition() {
        stateMachine.expect(LifecycleState.Default.class).create();
        stateMachine.expect(LifecycleState.Living.class).start();
        stateMachine.expect(LifecycleState.Visible.class).resume();
        stateMachine.expect(LifecycleState.Running.class).pause();
        stateMachine.expect(LifecycleState.Visible.class).stop();
        stateMachine.expect(LifecycleState.Living.class).destroy();

        enterSubscriber.assertValueCount(7);
        assertEquals(LifecycleState.Default.class, enterSubscriber.getOnNextEvents().get(0));
        assertEquals(LifecycleState.Living.class, enterSubscriber.getOnNextEvents().get(1));
        assertEquals(LifecycleState.Visible.class, enterSubscriber.getOnNextEvents().get(2));
        assertEquals(LifecycleState.Running.class, enterSubscriber.getOnNextEvents().get(3));
        assertEquals(LifecycleState.Visible.class, enterSubscriber.getOnNextEvents().get(4));
        assertEquals(LifecycleState.Living.class, enterSubscriber.getOnNextEvents().get(5));
        assertEquals(LifecycleState.Destroyed.class, enterSubscriber.getOnNextEvents().get(6));

        exitSubscriber.assertValueCount(6);
        assertEquals(LifecycleState.Default.class, exitSubscriber.getOnNextEvents().get(0));
        assertEquals(LifecycleState.Living.class, exitSubscriber.getOnNextEvents().get(1));
        assertEquals(LifecycleState.Visible.class, exitSubscriber.getOnNextEvents().get(2));
        assertEquals(LifecycleState.Running.class, exitSubscriber.getOnNextEvents().get(3));
        assertEquals(LifecycleState.Visible.class, exitSubscriber.getOnNextEvents().get(4));
        assertEquals(LifecycleState.Living.class, exitSubscriber.getOnNextEvents().get(5));
    }

    @Test
    public void testIllegalTransition() {
        stateMachine.expect(LifecycleState.Living.class).start();
        stateMachine.expect(LifecycleState.Visible.class).resume();
        stateMachine.expect(LifecycleState.Running.class).pause();
        stateMachine.expect(LifecycleState.Visible.class).stop();
        stateMachine.expect(LifecycleState.Living.class).destroy();

        enterSubscriber.assertValueCount(1);
        assertEquals(LifecycleState.Default.class, enterSubscriber.getOnNextEvents().get(0));

        exitSubscriber.assertNoValues();
    }
}
