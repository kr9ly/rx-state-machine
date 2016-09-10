# Rx State Machine - StateMachine based on RxJava

[![Circle CI](https://circleci.com/gh/kr9ly/rx-state-machine/tree/master.svg?style=shield)](https://circleci.com/gh/kr9ly/rx-state-machine/tree/master)

# Dependency

Add this to `repositories` block in your build.gradle

```
jcenter()
```

And Add this to `dependencies` block in your build.gradle

```
compile 'net.kr9ly:rx-state-machine:1.0.0'
```

### Usage

Declare State Interfaces (extends same interface)

```java
/**
 * Sample State Interfaces
 */
public interface LifecycleState {

    interface Default extends LifecycleState {
        // Event: create
        // Transition: to Living
        Living create();
    }

    interface Living extends LifecycleState {
        // Event: start
        // Transition: to Visible
        Visible start();
        // Event: destroy
        // Transition: to Destroyed
        Destroyed destroy();
    }

    interface Visible extends LifecycleState {
        // Event: resume
        // Transition: to Running
        Running resume();
        // Event: stop
        // Transition: to Living
        Living stop();
    }

    interface Running extends LifecycleState {
        // Event: pause
        // Transition: to Visible
        Visible pause();
    }

    interface Destroyed extends LifecycleState {

    }
}
```

Use interface like this

```java
// create state machine with initial state interface
RxStateMachine<LifecycleState> stateMachine = new RxStateMachine<LifecycleState>(LifecycleState.Default.class);
// emit event when enter to state
stateMachine.enterObservable().subscribe(enterSubscriber);
// emit event when exit from state
stateMachine.exitObservable().subscribe(exitSubscriber);
// send event through interfaces
stateMachine.expect(LifecycleState.Default.class).create();
stateMachine.expect(LifecycleState.Living.class).start();
stateMachine.expect(LifecycleState.Visible.class).resume();
stateMachine.expect(LifecycleState.Running.class).pause();
stateMachine.expect(LifecycleState.Visible.class).stop();
stateMachine.expect(LifecycleState.Living.class).destroy();
```

### RxStateBinder

See RxLifecycleBinderTest

```java
RxStateMachine<LifecycleState> stateMachine = new RxStateMachine<LifecycleState>(LifecycleState.Default.class);
RxStateBinder<LifecycleState> binder = new RxStateBinder<LifecycleState>(stateMachine);

Observable.from(/* some observable*/)
    .compose(binder.bindToState(LifecycleState.Living.class, LifecycleState.Destroyed.class))
    .subscribe();
```

# License

```
Copyright 2016 kr9ly

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```