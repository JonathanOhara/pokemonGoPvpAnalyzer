function expandLeague(){

    $(".rankings-container > .rank").not(".selected").each( function(i){
        if(i == 100) return false;
        try{
            var pk = $(this);

            console.log(i + " - " + pk.attr("data"));

            pk.click();
        }catch(e){}
    });

    if($(".rankings-container > .rank").not(".selected").length > 0){
        console.log("waiting...");
        setTimeout(() => {expandLeague()}, 100);
    }
}

setTimeout(() => {expandLeague()}, 100);