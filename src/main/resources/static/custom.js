var map;

function initMap() {
	
	let image = {
		url: "/bus.png",
		scaledSize: new google.maps.Size(40,40),
	}; 
	
	let person = {
		url: "/person.png",
		scaledSize: new google.maps.Size(40,40)
	}
	
    map = new google.maps.Map(document.getElementById('map'), {
        center: { lat: parseFloat(busLocations[0].LATITUDE), lng: parseFloat(busLocations[0].LONGITUDE) },
        zoom: 15,
        scrollwheel: false
    });


	var personMarker = new google.maps.Marker({
		position: { lat: parseFloat(personLocation.lat), lng: parseFloat(personLocation.lng) },
        map: map,
		icon: person,
		animation: google.maps.Animation.DROP
	});
	
    for (i=0; i<busLocations.length; i++){ 
		
		let contentString = "<h2>Vehicle# " + busLocations[i].VEHICLE;
		
		var marker = new google.maps.Marker({
            position: { lat: parseFloat(busLocations[i].LATITUDE), lng: parseFloat(busLocations[i].LONGITUDE) },
            map: map,
			icon: image,
			animation: google.maps.Animation.DROP
        });
	
		marker.infowindow = new google.maps.InfoWindow({
			content: contentString
		});
	
		google.maps.event.addListener(marker, 'click', function() {
            var marker_map = this.getMap();
            this.infowindow.open(marker_map, this);
            }); 
	}
}