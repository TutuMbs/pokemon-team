package br.com.fiap.model.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "times_pokemon")
public class Time {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 60)
    private String nome;
    @Column(nullable = false, length = 80)
    private String treinador;
    @Column(length = 40)
    private String regiao;
    @OneToMany(mappedBy = "time", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PokemonDoTime> pokemons = new ArrayList<>();

    public Time() { }
    public void substituirPokemons(List<PokemonDoTime> novos) {
        pokemons.clear();
        novos.forEach(this::adicionarPokemon);
    }
    public void adicionarPokemon(PokemonDoTime pokemon) { pokemon.setTime(this); pokemons.add(pokemon); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTreinador() { return treinador; }
    public void setTreinador(String treinador) { this.treinador = treinador; }
    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }
    public List<PokemonDoTime> getPokemons() { return pokemons; }
    public void setPokemons(List<PokemonDoTime> pokemons) { this.pokemons = pokemons; }
}
