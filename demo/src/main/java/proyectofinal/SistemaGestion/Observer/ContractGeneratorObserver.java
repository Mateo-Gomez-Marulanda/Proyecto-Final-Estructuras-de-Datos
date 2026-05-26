package proyectofinal.SistemaGestion.Observer;

import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;
import proyectofinal.SistemaGestion.OperacionDeNegocio.ProcessStatus;
import proyectofinal.Personal.Advisor;
import proyectofinal.controllers.AppContext;
import proyectofinal.SistemaGestion.Grafos.GraphService; // Importación necesaria

import java.time.LocalDate;

public class ContractGeneratorObserver implements OperationObserver {

    private static final int DEFAULT_DURATION_YEARS = 1;

    @Override
    public void onOperationEvent(OperationEvent event) {
        BusinessOperation operation = event.getOperation();

        switch (event.getEventType()) {
            case OPERATION_CREATED:
                System.out.println("[ContractGeneratorObserver] Operación registrada: " + operation.getIdentifier());
                break;

            case OPERATION_CANCELLED:
                handleCancelled(operation);
                break;

            case OPERATION_UPDATED:
                if (operation.getProcessStatus() == ProcessStatus.COMPLETED) {
                    handleCompletedClosure(operation);
                }
                break;
        }
    }

    private void handleCompletedClosure(BusinessOperation operation) {
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
        Contract contract = findContractByOperation(operation);
        if (contract != null && contract.isActive()) {
            contract.cancel();
            System.out.println("[ContractGeneratorObserver] Contrato cancelado: " + contract.getId());
        }
    }

    private void createNewContract(BusinessOperation operation) {
        String contractId = "CON-" + operation.getIdentifier();

        // Evitar duplicados
        for (Contract existing : AppContext.getInstance().getContracts()) {
            if (existing.getId().equals(contractId)) return;
        }

        String contractName = operation.getOperationType() == OperationType.RENTAL ? "Rental Agreement" : "Property Sale Agreement";
        String address = operation.getRelatedProperty() != null ? operation.getRelatedProperty().getAddress() : "Address not specified";

        LocalDate creationDate = operation.getDate() != null ? operation.getDate() : LocalDate.now();
        LocalDate expirationDate = creationDate.plusYears(DEFAULT_DURATION_YEARS);

        Contract contract = new Contract(contractId, contractName, address, operation.getClient(),
                operation.getRelatedProperty(), creationDate, expirationDate, operation.getAdvisor());

        contract.approve();
        AppContext.getInstance().getContracts().add(contract);

        if (operation.getClient() != null && operation.getRelatedProperty() != null) {
            GraphService.getInstance().getGrafo().addEdge(
                "CONTRACT-" + operation.getIdentifier(),
                GraphService.getInstance().getOrCreateVertex(operation.getClient().getId(), operation.getClient()),
                GraphService.getInstance().getOrCreateVertex(operation.getRelatedProperty().getCode(), operation.getRelatedProperty()),
                "CONTRACT_SIGNED", 1
            );
        }

        // Cambiar disponibilidad del inmueble
        if (operation.getRelatedProperty() != null) {
            operation.getRelatedProperty().setAvailable(false);
        }

        // Incrementar cierres del asesor
        if (operation.getAdvisor() != null) {
            Advisor advisor = operation.getAdvisor();
            advisor.setCompletedClosings(advisor.getCompletedClosings() + 1);
        }

        System.out.println("[ContractGeneratorObserver] Contrato generado y registrado en el Grafo: " + contractId);
    }

    private void renewExistingContract(BusinessOperation operation) {
        Contract contract = findContractByOperation(operation);
        if (contract != null) {
            LocalDate baseDate = contract.isExpired() ? LocalDate.now() : contract.getExpirationDate();
            contract.setExpirationDate(baseDate.plusYears(DEFAULT_DURATION_YEARS));
        }
    }

    private void cancelExistingContract(BusinessOperation operation) {
        Contract contract = findContractByOperation(operation);
        if (contract != null && !contract.isCancelled()) {
            contract.cancel();
            if (operation.getRelatedProperty() != null) {
                operation.getRelatedProperty().setAvailable(true);
            }
        }
    }

    private Contract findContractByOperation(BusinessOperation operation) {
        if (operation.getRelatedProperty() == null) return null;
        String propertyCode = operation.getRelatedProperty().getCode();
        for (Contract contract : Contract.getContractRegistry()) {
            if (contract.getRelatedProperty() != null && 
                contract.getRelatedProperty().getCode().equals(propertyCode) && 
                !contract.isCancelled()) {
                return contract;
            }
        }
        return null;
    }
}