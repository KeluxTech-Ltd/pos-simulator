package com.jayrush.springmvcrest.domain.domainDTO;

import lombok.Data;

import javax.persistence.*;

/**
 * @author JoshuaO
 */

@Entity
@Data
@NamedStoredProcedureQueries({@NamedStoredProcedureQuery(name = "fundWalletProcedure",procedureName = "fundWallet",
        resultClasses = { storedProcedure.class }, parameters = {@StoredProcedureParameter(name = "id",type = Long.class,
        mode = ParameterMode.IN) })})
public class storedProcedure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int returnedValue;
}
