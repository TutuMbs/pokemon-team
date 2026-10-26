package br.com.fiap.controller;

import br.com.fiap.model.dto.PokemonDTO;
import br.com.fiap.model.dto.TimeDTO;
import br.com.fiap.model.entity.PokemonDoTime;
import br.com.fiap.model.entity.Time;
import br.com.fiap.model.repository.TimeRepository;
import br.com.fiap.service.PokeApiClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TimeController {
    private final TimeRepository repository;
    private final PokeApiClient pokeApi;
    public TimeController(TimeRepository repository, PokeApiClient pokeApi) { this.repository = repository; this.pokeApi = pokeApi; }

    @GetMapping("/times")
    public ModelAndView listar() {
        ModelAndView mv = new ModelAndView("times");
        mv.addObject("times", repository.findAll());
        return mv;
    }

    @GetMapping("/times/novo")
    public ModelAndView novo() { return formulario(new TimeDTO("", "", "", List.of(new PokemonDTO("", 50))), null); }

    @PostMapping("/times")
    public ModelAndView criar(@Valid @ModelAttribute("timeForm") TimeDTO dto, BindingResult result) {
        if (result.hasErrors()) return formulario(dto, result);
        try { repository.save(montarTime(dto, new Time())); return new ModelAndView("redirect:/times"); }
        catch (RuntimeException e) { result.reject("pokemon.invalido", "Não foi possível consultar um dos Pokémon na PokeAPI."); return formulario(dto, result); }
    }

    @GetMapping("/times/editar/{id}")
    public ModelAndView editar(@PathVariable Long id) {
        Time time = repository.findById(id).orElseThrow();
        return formulario(toDto(time), null, time);
    }

    @PostMapping("/times/atualizar/{id}")
    public ModelAndView atualizar(@PathVariable Long id, @Valid @ModelAttribute("timeForm") TimeDTO dto, BindingResult result) {
        if (result.hasErrors()) return formulario(dto, result);
        try { Time time = repository.findById(id).orElseThrow(); montarTime(dto, time); repository.save(time); return new ModelAndView("redirect:/times"); }
        catch (RuntimeException e) { result.reject("pokemon.invalido", "Não foi possível consultar um dos Pokémon na PokeAPI."); return formulario(dto, result); }
    }

    @PostMapping("/times/excluir/{id}")
    public String excluir(@PathVariable Long id) { repository.deleteById(id); return "redirect:/times"; }

    @GetMapping("/api/times")
    public List<Time> apiListar() { return repository.findAll(); }

    @GetMapping("/api/pokemons")
    public List<PokeApiClient.PokemonCatalogo> listarPokemons() { return pokeApi.listarTodos(); }

    @PostMapping("/api/times")
    public ResponseEntity<?> apiCriar(@Valid @RequestBody TimeDTO dto, BindingResult result) {
        if (result.hasErrors()) return ResponseEntity.badRequest().body(result.getAllErrors());
        try { return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(montarTime(dto, new Time()))); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body("Pokémon não encontrado na PokeAPI"); }
    }

    @PutMapping("/api/times/{id}")
    public ResponseEntity<?> apiAtualizar(@PathVariable Long id, @Valid @RequestBody TimeDTO dto, BindingResult result) {
        if (result.hasErrors()) return ResponseEntity.badRequest().body(result.getAllErrors());
        return repository.findById(id).map(time -> { montarTime(dto, time); return ResponseEntity.ok(repository.save(time)); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/times/{id}")
    public ResponseEntity<Void> apiExcluir(@PathVariable Long id) { if (!repository.existsById(id)) return ResponseEntity.notFound().build(); repository.deleteById(id); return ResponseEntity.noContent().build(); }

    private Time montarTime(TimeDTO dto, Time time) {
        time.setNome(dto.nome().trim()); time.setTreinador(dto.treinador().trim()); time.setRegiao(dto.regiao());
        List<PokemonDoTime> pokemons = new ArrayList<>();
        dto.pokemons().forEach(item -> { if (item.nome() != null && !item.nome().isBlank()) { var dado = pokeApi.buscar(item.nome()); PokemonDoTime p = new PokemonDoTime(); p.setPokemonId(dado.id()); p.setNome(dado.nome()); p.setTipos(dado.tipos()); p.setUrlImagem(dado.imagem()); p.setNivel(item.nivel()); pokemons.add(p); } });
        if (pokemons.isEmpty()) throw new IllegalArgumentException("Time sem Pokémon");
        time.substituirPokemons(pokemons); return time;
    }
    private TimeDTO toDto(Time t) { return new TimeDTO(t.getNome(), t.getTreinador(), t.getRegiao(), t.getPokemons().stream().map(p -> new PokemonDTO(p.getNome(), p.getNivel())).toList()); }
    private ModelAndView formulario(TimeDTO dto, BindingResult result) { return formulario(dto, result, null); }
    private ModelAndView formulario(TimeDTO dto, BindingResult result, Time original) { ModelAndView mv = new ModelAndView("formulario-time"); mv.addObject("timeForm", dto); if (original != null) { mv.addObject("time", original); mv.addObject("acao", "/times/atualizar/" + original.getId()); } else mv.addObject("acao", "/times"); return mv; }
}
