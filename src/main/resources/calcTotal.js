var calc = function(names, removeWorstResult, prints){
  var pks = [];
  var i = 0;

  for(var name of names){
    pks[i++] = JSON.parse(localStorage.getItem(name));
  }

  var total = new Map();

  for( var pk of pks ){

      for( var counter in pk ){
          if(total.get(counter) === undefined) total.set(counter, []);
		  total.get(counter).push( parseInt(pk[counter] / 100) );
      }
  }

  var sortedTotal = new Map([...total.entries()].sort((pk1, pk2) => {
	let sumCounter1 = pk1[1].reduce((a, b) => a + b);
	let sumCounter2 = pk2[1].reduce((a, b) => a + b);


	if(removeWorstResult){
		sumCounter1 = sumCounter1 - Math.max(...pk1[1]);
		sumCounter2 = sumCounter2 - Math.max(...pk2[1]);
	}

	return sumCounter1 - sumCounter2;
  }));

  console.log(sortedTotal);

  var i = 0;
  for(var pk of sortedTotal){
	let sum = pk[1].reduce((a, b) => a + b);

	if(removeWorstResult){
		sum -= Math.max(...pk[1]);
	}

	console.log(pk[0] + ": " + sum);
	if(i++ > prints) break;
  }
}

//-----------------------------------------------------------------------------------------------------

calc(["Swampert", "Giratina (Altered)", "Snorlax", "Melmetal", "Venusaur", "Togekiss", "Poliwrath"], false, 30);
