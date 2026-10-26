package br.com.fiap.model.dto;

import jakarta.validation.constraints.*;

public record PokemonDTO(@NotBlank(message = "Informe o nome ou número do Pokémon") String nome,
                         @NotNull @Min(1) @Max(100) Integer nivel) { }
