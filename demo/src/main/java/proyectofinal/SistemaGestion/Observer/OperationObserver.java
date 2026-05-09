package proyectofinal.SistemaGestion.Observer;

/**
 * Observer interface for business operation events.
 * Any class that needs to react to a new or updated operation
 * must implement this interface and register with OperationPublisher.
 */
public interface OperationObserver {

    /**
     * Called by the publisher whenever a business operation event occurs.
     * @param event the event containing the operation and its type
     */
    void onOperationEvent(OperationEvent event);
}