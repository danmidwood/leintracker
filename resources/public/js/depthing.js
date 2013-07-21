window.onload=function(){findRepos();};
// variable to hold request
var request;
function findUpdatable(event){
    $("#results").empty();
    // abort any pending request
    if (request) {
        request.abort();
    }
    // setup some local variables
    var $form = $("form#find");
    // let's select and cache all the fields
    var $username = $form.find("input#username")[0];
    var $project = $form.find("select#project")[0];
    var $selectedProject = $form.find("select#project").find("option:selected")[0]

    // let's disable the inputs for the duration of the ajax request
    $username.disabled = true;
    $project.disabled = true;
    


    // fire off the request to /form.php
    var request = $.ajax({
        url: "/user/" + username.value + "/project/" + $selectedProject.text + "/dependencies",
        type: "GET"
    });

    // callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR){
        if (response.trim().length > 0) {
            // log a message to the console

	    var responseList = JSON.parse(response);
	    if (responseList.length == 0) {
		$("#results").append("Everything is up to date");
	    }
	    else {
		$("#results").append("<ul id=\"upgrades\"></ul>");
		for (var i = 0; i < responseList.length; i++) {
		    console.log(responseList[i]);
		    var upgrade = responseList[i];
		    $("#upgrades").append("<li>" + upgrade.name + " can be upgraded to " + upgrade.version + "</li>");
		}
	    }
        } else {
            $("#results").append('<div id="errors" class="alert">Could not examine project. Is it Leiningen?</div>');
        }
    });

    // callback handler that will be called on failure
    request.fail(function (jqXHR, textStatus, errorThrown){
	$("#results").append('<div id="errors" class="alert">Could not examine project. Is it Leiningen?</div>');
        console.error(
            "The following error occured: "+
            textStatus, errorThrown
        );
    });

    // callback handler that will be called regardless
    // if the request failed or succeeded
    request.always(function () {
        // reenable the inputs
	$username.disabled = false;
	$project.disabled = false;
    });

    // prevent default posting of form
    event.preventDefault();
};
$("#find").submit(findUpdatable);

$("select#project").change(findUpdatable);

var repoAutocompleteRequest;
// bind to the submit event of our form
function findRepos(){
    

    var $username = $(this);
    var $project = $("select#project")[0];

    var repoAutocompleteRequest = $.ajax({
        url: "https://api.github.com/users/" + username.value + "/repos?per_page=100",
        type: "GET"
    });

    // callback handler that will be called on success
    repoAutocompleteRequest.done(function (response, textStatus, jqXHR){
        // log a message to the console
	function isClojure(repo, index, array) { 
	    console.log(repo);
	    return repo.language == "Clojure"
	}

	$project.rem
	while ($project.hasChildNodes()) {
	    $project.removeChild($project.lastChild);
	}

	var clojureRepos = response.filter(isClojure);
	console.log(clojureRepos);
	if (clojureRepos.length > 0) {
	    for (var i = 0; i < clojureRepos.length; i++) {
		var opt = document.createElement("option");
		opt.value = i;
		opt.innerHTML = clojureRepos[i].name; // whatever property it has
		// then append it to the select element
		$project.appendChild(opt);
	    }
	}
	else {
	    project.value = "No Clojure repos?";
	}
    });

    // callback handler that will be called on failure
    repoAutocompleteRequest.fail(function (jqXHR, textStatus, errorThrown){
	console.log("Count not lookup repos");

    });


};

$("input#username").change(findRepos);
    
    
