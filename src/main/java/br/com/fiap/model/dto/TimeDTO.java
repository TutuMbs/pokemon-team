package br.com.fiap.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record TimeDTO(@NotBlank(message = "Informe o nome do time") String nome,
                      @NotBlank(message = "Informe o treinador") String treinador,
                      String regiao,
                      @NotEmpty(message = "Cadastre pelo menos um Pokémon")
                      @Size(max = 6, message = "Um time pode ter no máximo 6 Pokémon")
                      List<@Valid PokemonDTO> pokemons) { }
