var leagueNames = [];
leagueNames["1500"] = "Great";
leagueNames["2500"] = "Ultra";
leagueNames["10000"] = "Master";

var leagueCp = $(".league-select").val();

var finalString = "";

$(".rank").each(function(){
    if($(this).attr("data").indexOf("_shadow") == -1){
        console.log(this);
        finalString += $(this).attr("data") + "</br>"
    }
});

var myWindow = window.open("", "Pokemons" + leagueNames[leagueCp] + "League" );
myWindow.document.write(finalString)