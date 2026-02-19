package com.discovery.atm.repository.mapper;

import com.discovery.atm.dto.ClientDto;
import org.springframework.jdbc.core.RowMapper;

public final class ClientRowMapper {

    private ClientRowMapper() {
    }

    public static RowMapper<ClientDto> rowMapper() {
        return (rs, rowNum) -> new ClientDto(
                rs.getLong("CLIENT_ID"),
                rs.getString("TITLE"),
                rs.getString("NAME"),
                rs.getString("SURNAME")
        );
    }
}
