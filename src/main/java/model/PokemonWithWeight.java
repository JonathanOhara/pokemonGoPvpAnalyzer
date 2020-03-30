package model;

import enums.PokemonInLeague;

public class PokemonWithWeight {
    private PokemonInLeague pokemonInLeague;
    private int weight;

    public PokemonWithWeight(PokemonInLeague pokemonInLeague, int weight) {
        this.pokemonInLeague = pokemonInLeague;
        this.weight = weight;
    }


    public PokemonWithWeight(PokemonInLeague pokemonInLeague) {
        this.pokemonInLeague = pokemonInLeague;
        this.weight = 1;
    }

    public PokemonInLeague getPokemonInLeague() {
        return pokemonInLeague;
    }

    public int getWeight() {
        return weight;
    }
}
