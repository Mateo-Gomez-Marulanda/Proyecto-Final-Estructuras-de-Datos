package proyectofinal.SistemaGestion.Observer;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;

/**
 * Publisher (Subject) in the Observer pattern.
 *
 * Implemented as a Singleton so any class in the system
 * can publish or subscribe without passing references around.
 *
 * Usage:
 *   // Subscribe:
 *   OperationPublisher.getInstance().subscribe(myObserver);
 *
 *   // Publish when a new operation is created:
 *   OperationPublisher.getInstance().publish(operation, EventType.OPERATION_CREATED);
 */
public class OperationPublisher {

    // ─────────────────────────────────────────────
    // Singleton
    // ─────────────────────────────────────────────
    private static OperationPublisher instance;

    private OperationPublisher() {
        subscribers = new SimpleLinkedList<>();
    }

    public static OperationPublisher getInstance() {
        if (instance == null) {
            instance = new OperationPublisher();
        }
        return instance;
    }

    // ─────────────────────────────────────────────
    // Subscriber registry — uses the project's own SimpleLinkedList
    // ─────────────────────────────────────────────
    private final SimpleLinkedList<OperationObserver> subscribers;

    /**
     * Registers an observer to receive future operation events.
     */
    public void subscribe(OperationObserver observer) {
        if (observer == null) return;
        if (!subscribers.contains(observer)) {
            subscribers.add(observer);
        }
    }

    /**
     * Removes an observer so it no longer receives events.
     */
    public void unsubscribe(OperationObserver observer) {
        subscribers.remove(observer);
    }

    // ─────────────────────────────────────────────
    // Publishing
    // ─────────────────────────────────────────────

    /**
     * Publishes an event to all registered observers.
     * Call this every time a BusinessOperation is created or modified.
     *
     * @param operation the operation that triggered the event
     * @param eventType what happened (CREATED, UPDATED, CANCELLED)
     */
    public void publish(BusinessOperation operation, OperationEvent.EventType eventType) {
        OperationEvent event = new OperationEvent(operation, eventType);
        for (OperationObserver observer : subscribers) {
            observer.onOperationEvent(event);
        }
    }
}