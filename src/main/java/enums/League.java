package enums;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public enum League {
    GREAT(PokemonsGreatLeague::values),
    ULTRA(PokemonsUltraLeague::values),
    MASTER(PokemonsMasterLeague::values)
    ;

    private List<PokemonInLeague> availablePokemon;

    League(Supplier<PokemonInLeague[]> pokemonSupplier){
        availablePokemon = Arrays.asList(pokemonSupplier.get());
    }

    public List<PokemonInLeague> getAvailablePokemon() {
        return availablePokemon;
    }
}
