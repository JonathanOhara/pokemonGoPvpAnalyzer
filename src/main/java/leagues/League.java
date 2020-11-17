package leagues;

import leagues.special.PokemonHalloweenLeague;
import leagues.special.PokemonKantoLeague;
import leagues.special.PokemonLittleLeague;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum League {
    GREAT(1500, PokemonGreatLeague::values),
    LITTLE(500, PokemonLittleLeague::values),
    ULTRA(2500, PokemonUltraLeague::values),
    ULTRAPREMIER(2500, PokemonUltraPremierLeague::values),
    MASTER(10000, PokemonMasterLeague::values),
//    MASTERPREMIERE(10000, PokemonUltraLeague::values)

    //SPECIAL
    HALLOWEEN(2500, PokemonHalloweenLeague::values),
    KANTO(1500, PokemonKantoLeague::values)
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
