package enums;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum League {
    GREAT(1500, PokemonGreatLeague::values),
    HALLOWEEN(2500, PokemonHalloweenLeague::values),
    ULTRA(2500, PokemonUltraLeague::values),
    ULTRAPREMIER(2500, PokemonUltraPremierLeague::values),
    MASTER(10000, PokemonMasterLeague::values)
//    MASTERPREMIERE(10000, PokemonUltraLeague::values)
    ;

    private List<PokemonInLeague> availablePokemon;
    private int maxCp;

    League(int maxCP, Supplier<PokemonInLeague[]> pokemonSupplier){
        this.maxCp = maxCP;
        availablePokemon = Arrays.asList(pokemonSupplier.get());
    }

    public List<PokemonInLeague> getAvailablePokemon() {
        return availablePokemon;
    }

    public int getMaxCp() {
        return maxCp;
    }
}
