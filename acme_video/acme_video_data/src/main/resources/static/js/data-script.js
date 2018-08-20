$(document).ready(function() {
	var sTerm;
	$('#m').on('keyup', '#searchInput', function (e) {
	    if (e.keyCode == 13) {
	    	 renderSearchedMovies($('#searchInput').val(),1);
	    }
	});
	
	$('#m').on('click', '#leftAnchor', function (e) {
		if($('#leftAnchor').attr('sTerm')){
			renderSearchedMovies($('#leftAnchor').attr('sTerm'),$('#leftAnchor').attr('page'));
		}else{
			renderAllMovies($('#leftAnchor').attr('page'));
		}
	});

	$('#m').on('click', '#rightAnchor', function (e) {
		if($('#rightAnchor').attr('sTerm')){
			renderSearchedMovies($('#rightAnchor').attr('sTerm'),$('#rightAnchor').attr('page'));
		}else{
			renderAllMovies($('#rightAnchor').attr('page'));
		}
	});
	/*
	$('#m').on('click', '#leftCastDiv', function (e) {
		renderCastsTable($('#leftCastDiv').attr('page'));
	});

	$('#m').on('click', '#rightCastDiv', function (e) {
		renderCastsTable($('#rightCastDiv').attr('page'));
	});
	
	$('#m').on('click', '#leftRatingDiv', function (e) {
		renderRatingsTable($('#leftRatingDiv').attr('page'));
	});

	$('#m').on('click', '#rightRatingDiv', function (e) {
		renderRatingsTable($('#rightRatingDiv').attr('page'));
	});
	*/
} );

function renderAllMovies(pageNum){
	$.getJSON( "/movie", {pageNumber: pageNum}, function( data ) {
		$('#searchInput').empty();
		$('#movies').empty();
		renderMovieTable(data);
		
	});
}

function renderSearchedMovies(searchTerm, pageNum){
	sTerm = searchTerm;
	$.getJSON( "/movie/search/"+searchTerm,{pageNumber: pageNum}, function( data ) {
		$('#movies').empty();
		renderMovieTable(data);
		$('#searchInput').val(searchTerm);
		$('#leftAnchor').attr('sTerm',searchTerm);
		$('#rightAnchor').attr('sTerm',searchTerm);
	});
}

function getCastSnippet(data){
	if(!data.casts){
		return "N/A";
	}
	var castNames = "";
	for(i=0;i<data.casts.length; i++){
		if(i>0){
			castNames = castNames + ", ";
		}
		castNames = castNames + data.casts[i].primaryName;
	}
	return castNames;
}

function getRatingData(data){
	if(!data.rating){
		return "N/A";
	}
	return data.rating.averageRating + "\nVotes ("+data.rating.numVotes+");"
}

function renderMovieTable(data){

	var table = $('<table></table>');
	$('#ratings').empty();
	$('#casts').empty();
	
		var trh = $('<tr></tr>');
		trh.append($('<td></td>').text("#"));
		trh.append($('<td></td>').text("Movie Id"));
		trh.append($('<td></td>').text("Primary Title"));
		trh.append($('<td></td>').text("Original Title"));
		trh.append($('<td></td>').text("Start Year"));
		trh.append($('<td></td>').text("End Year"));
		trh.append($('<td></td>').text("Runtime Minutes"));
		trh.append($('<td></td>').text("Adult"));
		trh.append($('<td></td>').text("Genres"));
		trh.append($('<td></td>').text("Casts"));
		trh.append($('<td></td>').text("Rating"));
		table.append(trh);
		 $.each(data.entity.content, function(i,movieObject) {
			  var tr = $('<tr></tr>');
			  tr.append($('<td></td>').text(i+1));
			  tr.append($('<td></td>').text(movieObject.movieId));
			  tr.append($('<td></td>').text(movieObject.primaryTitle));
			  tr.append($('<td></td>').text(movieObject.originalTitle));
			  tr.append($('<td></td>').text(movieObject.startYear));
			  tr.append($('<td></td>').text(movieObject.endYear));
			  tr.append($('<td></td>').text(movieObject.runtimeMinutes));
			  tr.append($('<td></td>').text(movieObject.adult));
			  tr.append($('<td></td>').text(movieObject.genres));
			  tr.append($('<td></td>').text(getCastSnippet(movieObject)));
			  tr.append($('<td></td>').text(getRatingData(movieObject)));
			  table.append(tr);
		  });
 
	$('#data-header').text("Movies");
	
	var searchDiv = $('<div></div>').attr('id','searchPanel');
	
	var searchSpan = $('<span></span>').attr('id', 'searchSpan');
	searchSpan.text("Search");
	
	var totalItemSpan =  $('<span></span>').attr('id', 'totalItems').text("Total Items : "+data.entity.totalElements);
	
	var searchInput = $('<input></input>').attr("id", "searchInput");
	searchDiv.append(searchSpan);
	searchDiv.append(searchInput);
	searchDiv.append(totalItemSpan);
	
	if(!data.entity.first){
		var leftAnchor = $('<a></a>').attr('id','leftAnchor').text("Previous");
		$(leftAnchor).attr("page",data.entity.number<2?1:data.entity.number);
		$(leftAnchor).attr("href","#");
		
	}
	if(!data.entity.last){
		var rightAnchor = $('<a></a>').attr('id','rightAnchor').text("Next");
		$(rightAnchor).attr("page",data.entity.number==0?2:data.entity.number+2);
		$(rightAnchor).attr("href","#");
		
	}
	searchDiv = searchDiv.append(rightAnchor);
	searchDiv.append(leftAnchor);
	$('#movies').append(searchDiv);
	$('#movies').append(table);
	
	$('#leftAnchor').css({'float':'right', 'padding-right':'15px'});
	$('#rightAnchor').css('float','right');
	$('#searchPanel').css('margin-left', '150px');
	$('#searchSpan').css('padding-right', '10px');
	$('#totalItems').css('padding-left', '10px');

}

function renderRatingsTable(pageNum){
	var table = $('<table></table>');
	$('#movies').empty();
	$('#casts').empty();
	$.getJSON( "/rating", {pageNumber: pageNum}, function( data ) {
		var trh = $('<tr></tr>');
		trh.append($('<td></td>').text("#"));
		trh.append($('<td></td>').text("Movie Id"));
		trh.append($('<td></td>').text("Agerage Rating"));
		trh.append($('<td></td>').text("Number of Votes"));
		table.append(trh);
		 $.each(data.entity.content, function(i,ratingObject) {
			  var tr = $('<tr></tr>');
			  tr.append($('<td></td>').text(i+1));
			  tr.append($('<td></td>').text(ratingObject.ratingId));
			  tr.append($('<td></td>').text(ratingObject.averageRating));
			  tr.append($('<td></td>').text(ratingObject.numVotes));
			  table.append(tr);
			  });
	});
	
	$('#data-header').text("Ratings");
	$('#ratings').append(table);
}

function renderCastsTable(pageNum){
	var table = $('<table></table>');
	$('#ratings').empty();
	$('#movies').empty();
	$.getJSON( "/cast", {pageNumber: pageNum}, function( data ) {
		var trh = $('<tr></tr>');
		trh.append($('<td></td>').text("#"));
		trh.append($('<td></td>').text("Cast Id"));
		trh.append($('<td></td>').text("Primary Name"));
		trh.append($('<td></td>').text("Birty Year"));
		trh.append($('<td></td>').text("Death Year"));
		trh.append($('<td></td>').text("Primary Profession"));
		trh.append($('<td></td>').text("Known for Titles"));
		table.append(trh);
		 $.each(data.entity.content, function(i,castObject) {
			  var tr = $('<tr></tr>');
			  tr.append($('<td></td>').text(i+1));
			  tr.append($('<td></td>').text(castObject.castId));
			  tr.append($('<td></td>').text(castObject.primaryName));
			  tr.append($('<td></td>').text(castObject.birthYear));
			  tr.append($('<td></td>').text(castObject.deathYear));
			  tr.append($('<td></td>').text(castObject.primaryProfession));
			  tr.append($('<td></td>').text(castObject.knownForTitles));
			  table.append(tr);
		  });
	});  
	$('#data-header').text("Casts");
	$('#casts').append(table);
}