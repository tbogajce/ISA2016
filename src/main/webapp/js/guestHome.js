var restaurantCombo = "tableReservation/getRestaurantCombo";
var restaurantTables = "tableReservation/restaurantTables";
var restaurantReservation = "tableReservation/restaurantReservation";
var getAllRestaurantsFun = "tableReservation/getAllRestaurants";


$( document ).ready(function() {
	$('#tableReservationPanel').show();
	$('#callFriendPanel').hide();
	$('#makeOrderPanel').hide();
	$('#historyVisitsPanel').hide();
	$('#allRestaurantsPanel').hide();
	restaurantCombo22();
});

$(document).on('click', '#reservationButton', function(e) {
	$('#tableReservationPanel').show();
	$('#callFriendPanel').hide();
	$('#makeOrderPanel').hide();
	$('#historyVisitsPanel').hide();
	$('#allRestaurantsPanel').hide();
	restaurantCombo22();

});


$(document).on('click', '#callFriendButton', function(e) {
	$('#callFriendPanel').show();
	$('#tableReservationPanel').hide();
	$('#makeOrderPanel').hide();
	$('#historyVisitsPanel').hide();
	$('#allRestaurantsPanel').hide();
	
	tableReservationInvite();
	getAllFriends1();
});

$(document).on('click', '#makeOrderButton', function(e) {
	$('#tableReservationPanel').hide();
	$('#callFriendPanel').hide();
	$('#makeOrderPanel').show();
	$('#historyVisitsPanel').hide();
	$('#allRestaurantsPanel').hide();
	
	restaurantCombo33();
});

$(document).on('click', '#allRestaurantsButton', function(e) {
	$('#tableReservationPanel').hide();
	$('#callFriendPanel').hide();
	$('#makeOrderPanel').hide();
	$('#historyVisitsPanel').hide();
	$('#allRestaurantsPanel').show();
	
	printAllRestaurants();
});



$(document).on('change', '.selectRestaurant', function(e) {
	getTables();

});

function restaurantCombo22() {
			console.log('ubacivanje restorana');
			$(".selectRestaurant").empty();
			$.ajax({
				type : 'GET',
				url : restaurantCombo,
				contentType : 'application/json',
				dataType : "json",
				success : function(data) {
					$(".selectRestaurant").empty();
					var list = data == null ? [] : (data instanceof Array ? data : [ data ]);
					var selectRestaurant = $(".selectRestaurant");
					$.each(list, function(index, restaurant) {
						var li = $('<option value="'+restaurant.restaurantId+'">' + restaurant.restaurantName + ' </option>');
						$(selectRestaurant).append(li);
					});
					getTables();
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("AJAX ERROR: " + errorThrown);
				}
			});
}

function getTables(){
	console.log("vrati broj stolova restorana");
	var restaurantId=$('.selectRestaurant').find(":selected").val();
	console.log(restaurantId);
	$.ajax({
		type : 'POST',
		url : restaurantTables,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSONTable(restaurantId),
		success : function(data) {
			$(".selectDiningTable").empty();
			var list = data == null ? [] : (data instanceof Array ? data : [ data ]);
			var selectRestaurant = $(".selectDiningTable");
			$.each(list, function(index, diningTable) {
				var li = $('<option value="'+diningTable.generalTableID+'">' + diningTable.numberOfSeats + ' </option>');
				$(selectRestaurant).append(li);
			});
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + errorThrown);
		}
	});
}

function formToJSONTable(restaurantId) {
	return JSON.stringify({
		"restaurantId" : restaurantId,
	});
}

$(document).on('submit', '.formReservation', function(e) {
	e.preventDefault();
	var restaurantId=$('.selectRestaurant').find(":selected").val();
	var date = $(this).find("input[name=date]").val();
	var time = $(this).find("input[name=time]").val();
	var hours = $(this).find("input[name=hours]").val();
	var diningTableId =$('.selectDiningTable').find(":selected").val();	
	console.log(restaurantId + " " + date + " " + time + " " + hours + " " + diningTableId)
	$.ajax({
		type : 'POST',
		url : restaurantReservation,
		contentType : 'application/json',
		dataType : "text",
		data : formToJSONReservation(restaurantId, date, time, hours, diningTableId),
		success : function(data) {
				
			location.reload(true);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + errorThrown);
		}
	});
	
});

function formToJSONReservation(restaurantId, date, time, hours, diningTableId) {
	return JSON.stringify({
		"restaurantId" : restaurantId,
		"date" : date,
		"time" : time,
		"hours" : hours,
		"diningTableId" : diningTableId,
	});
}

//----------
///GET ALL RESTAURANTS

function printAllRestaurants() {
	$.ajax({
		type : 'GET',
		url : getAllRestaurantsFun,
		dataType : "json", // data type of response
		success : function(data) {
			allRestaurantsPrint(data);
		}
	});
}

function allRestaurantsPrint(data) {
	// JAX-RS serializes an empty list as null, and a 'collection of one' as an
	// object (not an 'array of one')
	$('#allRestaurantsData').empty();
	var list = data == null ? [] : (data instanceof Array ? data : [ data ]);
	$.each(list,function(index, restaurant) {
						var tr = $('<tr></tr>');
						tr.append('<td>' + restaurant.restaurantName + '</td>'+ 
										'<td>' + restaurant.restaurantType + '</td>' +
										'<td>' + restaurant.restaurantAdress + '</td>');
						$('#allRestaurantsData').append(tr);
					});
}


///////////TABELA FILTER
//----------------------tabela-----------------------
$(document)
		.ready(
				function() {
					$(document)
							.on(
									'click',
									'.filterable .btn-filter',
									function() {
										var $panel = $(this).parents(
												'.filterable'), $filters = $panel
												.find('.filters input'), $tbody = $panel
												.find('.table tbody');
										if ($filters.prop('disabled') == true) {
											$filters.prop('disabled', false);
											$filters.first().focus();
										} else {
											$filters.val('').prop('disabled',
													true);
											$tbody.find('.no-result').remove();
											$tbody.find('tr').show();
										}
									});

					$('.filterable .filters input')
							.keyup(
									function(e) {
										/* Ignore tab key */
										var code = e.keyCode || e.which;
										if (code == '9')
											return;
										/* Useful DOM data and selectors */
										var $input = $(this), inputContent = $input
												.val().toLowerCase(), $panel = $input
												.parents('.filterable'), column = $panel
												.find('.filters th').index(
														$input.parents('th')), $table = $panel
												.find('.table'), $rows = $table
												.find('tbody tr');
										/* Dirtiest filter function ever ;) */
										var $filteredRows = $rows
												.filter(function() {
													var value = $(this).find(
															'td').eq(column)
															.text()
															.toLowerCase();
													return value
															.indexOf(inputContent) === -1;
												});
										/* Clean previous no-result if exist */
										$table.find('tbody .no-result')
												.remove();
										/*
										 * Show all rows, hide filtered ones
										 * (never do that outside of a demo !
										 * xD)
										 */
										$rows.show();
										$filteredRows.hide();
										/*
										 * Prepend no-result row if all rows are
										 * filtered
										 */
										if ($filteredRows.length === $rows.length) {
											$table
													.find('tbody')
													.prepend(
															$('<tr class="no-result text-center"><td colspan="'
																	+ $table
																			.find('.filters th').length
																	+ '">No result found</td></tr>'));
										}
									});
				});
// --------------------------kraj tabela------------------------------------
