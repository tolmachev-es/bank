package com.tolmachev.bank.mapper;

import com.tolmachev.bank.model.WalletBalanceDomain;
import com.tolmachev.bank.repository.entity.WalletEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.BalanceDto;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "walletId", source = "walletId")
    BalanceDto toDto(WalletBalanceDomain domain);

    @Mapping(target = "balance", source = "availableBalance")
    @Mapping(target = "walletId", source = "id")
    WalletBalanceDomain toDomain(WalletEntity entity);
}
