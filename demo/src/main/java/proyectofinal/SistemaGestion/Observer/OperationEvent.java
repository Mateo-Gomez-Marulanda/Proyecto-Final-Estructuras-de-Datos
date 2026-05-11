package proyectofinal.SistemaGestion.Observer;

import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;

/**
 * Carries all the data relevant to an operation event.
 * Passed to every observer when the publisher notifies.
 */
public class OperationEvent {

    public enum EventType {
        OPERATION_CREATED,   // A brand-new operation was registered
        OPERATION_UPDATED,   // Status or data on an existing operation changed
        OPERATION_CANCELLED  // An operation was explicitly cancelled
    }

    private final BusinessOperation operation;
    private final EventType eventType;

    public OperationEvent(BusinessOperation operation, EventType eventType) {
        this.operation = operation;
        this.eventType = eventType;
    }

    public BusinessOperation getOperation() { return operation; }
    public EventType getEventType()         { return eventType; }

    @Override
    public String toString() {
        return "OperationEvent [type=" + eventType
                + ", operation=" + operation.getIdentifier() + "]";
    }
}