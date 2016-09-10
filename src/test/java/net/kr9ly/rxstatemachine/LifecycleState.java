package net.kr9ly.rxstatemachine;

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
