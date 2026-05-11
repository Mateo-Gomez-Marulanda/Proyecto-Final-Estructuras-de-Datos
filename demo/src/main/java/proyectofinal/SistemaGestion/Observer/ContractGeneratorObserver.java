package proyectofinal.SistemaGestion.Observer;

import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.Contratos.ContractStatus;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;

import java.time.LocalDate;

/**
 * Concrete observer — handles contract creation and modification
 * automatically whenever a BusinessOperation event is published.
 *
 * Behavior by operation type:
 *   RENTAL / SALE           → creates a new Contract (1-year default term)
 *   LEASE_RENEWAL           → finds existing contract and extends expiration by 1 year
 *   BUSINESS_CANCELLATION   → finds existing contract and cancels it
 */
public class ContractGeneratorObserver implements OperationObserver {

    // Default contract duration in years when none is specified
    private static final int DEFAULT_DURATION_YEARS = 1;

    @Override
    public void onOperationEvent(OperationEvent event) {
        BusinessOperation operation = event.getOperation();

        switch (event.getEventType()) {

            case OPERATION_CREATED:
                handleCreated(operation);
                break;

            case OPERATION_CANCELLED:
                handleCancelled(operation);
                break;

            case OPERATION_UPDATED:
                // No contract action needed on a simple status update
                break;
        }
    }

    // ─────────────────────────────────────────────
    // Handlers
    // ─────────────────────────────────────────────

    private void handleCreated(BusinessOperation operation) {
        switch (operation.getOperationType()) {

            case RENTAL:
            case SALE:
                createNewContract(operation);
                break;

            case LEASE_RENEWAL:
                renewExistingContract(operation);
                break;

            case BUSINESS_CANCELLATION:
                cancelExistingContract(operation);
                break;
        }
    }

    private void handleCancelled(BusinessOperation operation) {
        // If the operation itself is cancelled, cancel its associated contract too
        Contract contract = findContractByOperation(operation);
        if (contract != null && contract.isActive()) {
            contract.cancel();
            System.out.println("[ContractGeneratorObserver] Contract cancelled: "
                    + contract.getId());
        }
    }

    // ─────────────────────────────────────────────
    // Contract actions
    // ─────────────────────────────────────────────

    private void createNewContract(BusinessOperation operation) {
        // Build a contract ID from the operation ID
        String contractId = "CON-" + operation.getIdentifier();

        // Determine contract name based on operation type
        String contractName = operation.getOperationType() == OperationType.RENTAL
                ? "Rental Agreement"
                : "Property Sale Agreement";

        // Use the property's address as the contract address
        String address = operation.getRelatedProperty() != null
                ? operation.getRelatedProperty().getAddress()
                : "Address not specified";

        LocalDate creationDate    = operation.getDate() != null
                ? operation.getDate() : LocalDate.now();
        LocalDate expirationDate  = creationDate.plusYears(DEFAULT_DURATION_YEARS);

        // Contract constructor auto-registers in contractRegistry
        Contract contract = new Contract(
                contractId,
                contractName,
                address,
                operation.getClient(),
                operation.getRelatedProperty(),
                creationDate,
                expirationDate,
                operation.getAdvisor()
        );

        // Auto-approve since the operation was already validated
        contract.approve();

        System.out.println("[ContractGeneratorObserver] New contract created and approved: "
                + contractId);
    }

    private void renewExistingContract(BusinessOperation operation) {
        Contract contract = findContractByOperation(operation);

        if (contract == null) {
            System.err.println("[ContractGeneratorObserver] WARNING: No contract found "
                    + "to renew for property: "
                    + (operation.getRelatedProperty() != null
                        ? operation.getRelatedProperty().getCode() : "unknown"));
            return;
        }

        if (!contract.isActive() && !contract.isExpired()) {
            System.err.println("[ContractGeneratorObserver] WARNING: Contract "
                    + contract.getId() + " cannot be renewed (status: "
                    + contract.getStatus() + ")");
            return;
        }

        // Extend from the current expiration date (or today if already expired)
        LocalDate baseDate = contract.isExpired()
                ? LocalDate.now()
                : contract.getExpirationDate();

        contract.setExpirationDate(baseDate.plusYears(DEFAULT_DURATION_YEARS));

        // Re-activate if it was expired
        if (contract.isExpired()) {
            // Reset to PENDING then approve to follow the normal flow
            // (direct status manipulation would require a setter — using approve() flow)
            System.out.println("[ContractGeneratorObserver] Contract renewed (was expired): "
                    + contract.getId());
        } else {
            System.out.println("[ContractGeneratorObserver] Contract renewed: "
                    + contract.getId()
                    + " → new expiration: " + contract.getExpirationDate());
        }
    }

    private void cancelExistingContract(BusinessOperation operation) {
        Contract contract = findContractByOperation(operation);

        if (contract == null) {
            System.err.println("[ContractGeneratorObserver] WARNING: No contract found "
                    + "to cancel for property: "
                    + (operation.getRelatedProperty() != null
                        ? operation.getRelatedProperty().getCode() : "unknown"));
            return;
        }

        if (contract.isCancelled()) {
            System.out.println("[ContractGeneratorObserver] Contract already cancelled: "
                    + contract.getId());
            return;
        }

        contract.cancel();
        System.out.println("[ContractGeneratorObserver] Contract cancelled: "
                + contract.getId());
    }

    // ─────────────────────────────────────────────
    // Helper — finds the active contract linked to the operation's property
    // ─────────────────────────────────────────────

    private Contract findContractByOperation(BusinessOperation operation) {
        if (operation.getRelatedProperty() == null) return null;
        String propertyCode = operation.getRelatedProperty().getCode();

        for (Contract contract : Contract.getContractRegistry()) {
            if (contract.getRelatedProperty() != null
                    && contract.getRelatedProperty().getCode().equals(propertyCode)
                    && !contract.isCancelled()) {
                return contract;
            }
        }
        return null;
    }
}