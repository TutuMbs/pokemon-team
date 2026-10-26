package br.com.fiap.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pokemons_dos_times")
public class PokemonDoTime {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Integer pokemonId;
    @Column(nullable = false, length = 40) private String nome;
    @Column(nullable = false, length = 100) private String tipos;
    @Column(nullable = false) private Integer nivel;
    @Column(length = 300) private String urlImagem;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_id", nullable = false)
    private Time time;
    public Long getId() { return id; }
    public Integer getPokemonId() { return pokemonId; }
    public void setPokemonId(Integer v) { pokemonId = v; }
    public String getNome() { return nome; }
    public void setNome(String v) { nome = v; }
    public String getTipos() { return tipos; }
    public void setTipos(String v) { tipos = v; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer v) { nivel = v; }
    public String getUrlImagem() { return urlImagem; }
    public void setUrlImagem(String v) { urlImagem = v; }
    @JsonIgnore public Time getTime() { return time; }
    public void setTime(Time v) { time = v; }
}
