package com.tolmachev.bank.mapper;

import com.tolmachev.bank.model.OperationDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.openapitools.model.WalletOperationDto;

@Mapper(componentModel = "spring")
public interface OperationsMapper {
    @Mapping(target = "uuid", source = "walletId")
    @Mapping(target = "deposit", source = "operationType", qualifiedByName = "operationTypeHelper")
    @Mapping(target = "amount", source = "amount")
    OperationDomain toOperationDomain(WalletOperationDto walletOperationDto);

    @Named("operationTypeHelper")
    default boolean operationTypeHelper(WalletOperationDto.OperationTypeEnum operationTypeEnum) {
        return operationTypeEnum.equals(WalletOperationDto.OperationTypeEnum.DEPOSIT);
    }
}
