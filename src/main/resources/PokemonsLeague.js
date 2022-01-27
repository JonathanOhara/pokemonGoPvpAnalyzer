var finalString = "";

var leagueNames = [];
leagueNames["1500"] = "Great";
leagueNames["2500"] = "Ultra";
leagueNames["10000"] = "Master";

var leagueCp = $(".league-select").val();

$(".rankings-container > .rank").each( function(){
    var pokemonRow = $(this);

    var simpleName = pokemonRow.attr("data").toUpperCase();
    var name = pokemonRow.find(".name").html();
    var detailsUrl = pokemonRow.find(".multi-battle-link a").attr("href");

    if(detailsUrl){
        detailsUrl = detailsUrl.replace("/11/","/{shields}/")

        console.log(simpleName);

        finalString +=
            '\n</br>' +
            simpleName +

            '(' +
            '"' + name + '"' +
            ',' + '"' +detailsUrl + '"' +
            ')' +

            ',';
    }


});
finalString = finalString.substring(0, finalString.length - 1);
finalString += ";"

var myWindow = window.open("", "Pokemons" + leagueNames[leagueCp] + "League" );
myWindow.document.write(finalString);