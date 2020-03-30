var all = $(".rankings-container").children();
var values = {};

for( var i = 0; i < all.length; i++){
    var child = all.get(i);
    values[child.getAttribute("data")] = parseInt($(child).find(".star").html());
}
localStorage.setItem($("#main > div.section.battle > div:nth-child(5) > div > div > div > div > h2").html(),
    JSON.stringify(values))