package leagues;

public interface PokemonInLeague {

    String getDisplayName();
    String getSimpleName();
    String getUrl(int numberOfShields);

    default boolean equalsBothNames(String name){
        return getSimpleName().equalsIgnoreCase(name) || getDisplayName().equalsIgnoreCase(name);
    }

    static PokemonInLeague getPokemonByName(League league, String name){
        return league.getAvailablePokemon().stream()
                .filter(pokemon -> pokemon.equalsBothNames(name))
                .findFirst().orElseThrow(() -> new RuntimeException(name + " not found in "+league.getAvailablePokemon().get(0).getClass().getSimpleName()));
    }

    static boolean hasPokemon(League league, String name){
        boolean has = league.getAvailablePokemon().stream()
                .filter(pokemon -> pokemon.equalsBothNames(name))
                .count() > 0;

        return has;
    }
}