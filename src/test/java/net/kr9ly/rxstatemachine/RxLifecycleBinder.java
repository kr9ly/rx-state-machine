package net.kr9ly.rxstatemachine;

import rx.Observable;

public class RxLifecycleBinder {

    private RxStateMachine<LifecycleState> stateMachine = new RxStateMachine<LifecycleState>(LifecycleState.Default.class);

    private RxStateBinder<LifecycleState> binder = new RxStateBinder<LifecycleState>(stateMachine);

    public <T> Observable.Transformer<? super T, ? extends T> emitWhileLiving() {
        return binder.bindToState(LifecycleState.Living.class, LifecycleState.Destroyed.class);
    }

    public <T> Observable.Transformer<? super T, ? extends T> emitWhileVisible() {
        return binder.bindToState(LifecycleState.Visible.class, LifecycleState.Living.class);
    }

    public <T> Observable.Transformer<? super T, ? extends T> emitWhileRunning() {
        return binder.bindToState(LifecycleState.Running.class, LifecycleState.Visible.class);
    }

    public void onCreate() {
        stateMachine.expect(LifecycleState.Default.class).create();
    }

    public void onStart() {
        stateMachine.expect(LifecycleState.Living.class).start();
    }

    public void onResume() {
        stateMachine.expect(LifecycleState.Visible.class).resume();
    }

    public void onPause() {
        stateMachine.expect(LifecycleState.Running.class).pause();
    }

    public void onStop() {
        stateMachine.expect(LifecycleState.Visible.class).stop();
    }

    public void onDestroy() {
        stateMachine.expect(LifecycleState.Living.class).destroy();
    }
}
