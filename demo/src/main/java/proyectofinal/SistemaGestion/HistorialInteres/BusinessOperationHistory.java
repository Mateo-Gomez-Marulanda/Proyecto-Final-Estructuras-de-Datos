package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;

public class BusinessOperationHistory extends History<BusinessOperation> {
    public BusinessOperationHistory() {
        super();
    }

    @Override
    public BusinessOperation findElementById(String code) {
        for (BusinessOperation operation : this.getHistoryList()) {
            if (operation.getIdentifier().equals(code)) {
                return operation;
            }
        }
        return null; 
    }    
}