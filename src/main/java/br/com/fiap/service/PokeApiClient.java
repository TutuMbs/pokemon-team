package br.com.fiap.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.ArrayList;
import java.util.List;

@Service
public class PokeApiClient {
    private final RestClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public PokeApiClient(@Value("${pokeapi.base-url}") String baseUrl) {
        client = RestClient.builder().baseUrl(baseUrl).build();
    }
    public DadosPokemon buscar(String nomeOuId) {
        JsonNode json = obterJson("/pokemon/{pokemon}", nomeOuId.trim().toLowerCase());
        if (json == null) throw new IllegalArgumentException("Pokémon não encontrado");
        StringBuilder tipos = new StringBuilder();
        json.path("types").forEach(t -> {
            if (!tipos.isEmpty()) tipos.append(", ");
            tipos.append(t.path("type").path("name").asText());
        });
        String imagem = json.path("sprites").path("other").path("official-artwork").path("front_default").asText();
        if (imagem.isBlank()) imagem = json.path("sprites").path("front_default").asText();
        return new DadosPokemon(json.path("id").asInt(), json.path("name").asText(), tipos.toString(), imagem);
    }
    public List<PokemonCatalogo> listarTodos() {
        JsonNode json = obterJson("/pokemon?limit=2000");
        List<PokemonCatalogo> resultado = new ArrayList<>();
        if (json == null) return resultado;
        json.path("results").forEach(item -> {
            String nome = item.path("name").asText();
            String url = item.path("url").asText();
            String[] partes = url.replaceAll("/$", "").split("/");
            int id = Integer.parseInt(partes[partes.length - 1]);
            resultado.add(new PokemonCatalogo(id, nome, "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" + id + ".png"));
        });
        return resultado;
    }
    private JsonNode obterJson(String uri, Object... parametros) {
        try {
            String resposta = client.get().uri(uri, parametros).retrieve().body(String.class);
            return objectMapper.readTree(resposta);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível consultar a PokéAPI", e);
        }
    }
    public record DadosPokemon(Integer id, String nome, String tipos, String imagem) { }
    public record PokemonCatalogo(Integer id, String nome, String imagem) { }
}
