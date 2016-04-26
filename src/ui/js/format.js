/* Custom JavaScript functions */
/* Global variables. */
var title = null;
var name = "Bruce Wayne";
var login = "bwayn052";
var url = "js/"; //"/home/rhom001/Documents/CS180/mobile-ilearn/data/users/exampleDC/" + login + "/";

/* Function for adding functions on to the menu. */
function menu() {
    // Sets up click functions for the menu options.
    $("#app-menu-home").click(function(){
        loadHome();
    });
    $("#app-menu-course").click(function(){
        loadClasses();
    });
    $("#app-menu-assn").click(function(){
        loadAssigns();
    });
    $("#app-menu-grade").click(function(){
        loadGrades();
    });
    $("#app-menu-setting").click(function(){
        loadSets();
    });
    
    // Welcome dialog box (testing)
    //alert("Welcome to MiLearn!");
    
    // Gets the current time.
    footer();
}

/* Function for retrieving the time every 1000 milliseconds. 
 * Output displays day of the week, abbreviated month, day, year, time 
 * (military time), and time zone. (may change later)*/
function footer() {
    var now = new Date();
    document.getElementById("app-time").innerHTML = now.toString();
    var t = setTimeout(footer, 1000);
}

/* Functions for getting data from JSON files and outputting into the proper array. */
/* Function for reading data from a JSON file and returning a function. */
function getData(url, func) {
    var file = new XMLHttpRequest();
    
    file.onreadystatechange = function() {
        //alert(file.readyState + " " + file.status);
        if(file.readyState == 4 && file.status == 200) {
            //alert(url);
            var response = JSON.parse(file.responseText);
            func(response);
        }
    };
    
    file.open("GET", url, true);
    file.send();
}

/* Function for getting a user's classes. */
function jsonClasses(arr) {
    var out = "";
    var i;
    var idNum = 0;
    for(i = 0; i < arr.length; i++) {
        idNum++;
        // Generates a collapsible with the course information.
        out += '<div data-role="collapsible" id="class' + idNum + '"><h3 class="scheduled">' + arr[i].courseNum + '-' + arr[i].courseSec + '</h3><p>Class Name: ' + arr[i].courseName + '<br />Instructor: ' + arr[i].instructor + '</p></div>';
    }
    // Refreshes the schedule collapsible set.
    $(".schedule").html(out).collapsibleset("refresh");
}

/* Function for getting a user's assignments. */
function jsonAssigns(arr) {
    var out = "";
    var i;
    var idNum = 0;
    for(i = 0; i < arr.length; i++) {
        idNum++;
        // Generates a collapsible with the assignment.
        out += '<div data-role="collapsible" id="assnNum' + idNum + '"><h3 class="assigned">' + arr[i].courseNum + '-' + arr[i].courseSec + ': ' + arr[i].title + '</h3><p>' + arr[i].desc + '</p><p>Due: ' + arr[i].due + '</p><p>Points: ' + arr[i].points + '</p></div>';
    }
    // Refreshes the assignments collapsible set.
    $(".assign").html(out).collapsibleset("refresh");
}

// Work on this
/* Function for getting a user's grades. */
function jsonGrades(arr) {
    var out = "";
    var i;
    var idNum;
    
    /* Function for generating a class list. */
    function jsonClassList(arrC) {
        out = "";
        idNum = 0;
        var className = "";
        var classArr = [];
        for(i = 0; i < arrC.length; i++) {
            idNum++;
            // Generates a list item with the class name and adds it to the array of class names.
            className = arrC[i].courseNum + ' (' + arrC[i].courseSec + ')';
            out += '<li><a href="#graded' + idNum + '">' + className + '</a></li>';
            classArr.push(className);
            alert(classArr[i]);
        }
        // Refreshes the list of classes.
        $(".grades").html(out).listview("refresh");
        
        // Generates gradebooks
        out = "";
        idNum = 0;
        for(i = 0; i < classArr.length; i++) {
            idNum++;
            out += '<div data-role="panel" data-position="right" data-display="overlay" id="graded' + idNum + '"><ul data-role="listview" class="gradebook" id="gradebook' + idNum + '"></ul></div>';
        }
        $("#gradePanels").html(out);
    }
    
    // Gets the list of classes
    getData(url + "classes.json", jsonClassList);
    alert(document.getElementById("graded*").length);

    // Gets the grades.
    var prevClass = arr[i].courseNum;
    idNum = 1;
    for(i = 0; i < arr.length; i++) {
        // While the course number and section do not equal the one at the array, then go to the next id number.
        while(classArr[idNum-1] !== (arr[i].courseNum + ' (' + arr[i].courseSec + ')')) { 
            $("#gradebook" + idNum).html(out).listview("refresh");
            idNum++;
            out = "";
        }
        
        // Generates the panel holding the grade information
        out += '<li>'+ arr[i].title + '<br /><span class="gradedTotal">' + arr[i].grade + '/' + arr[i].total + '</span></li>';
    }
    $("#gradebook" + idNum).html(out).listview("refresh");
    //$(".grades").html(out).listview("refresh");
}

/* Function for setting the page title. */
function header(pageName) {
    $("#app-title").text(pageName);
}

/* Function for changing active link. */
function changeLink(link) {
    $(".app-menu-active").toggleClass("app-menu-active");
    $("#app-menu-"+link).toggleClass("app-menu-active");
}

/* Function for changing active content. */
function changeContent(content) {
    $(".app-active").toggleClass("app-active");
    $("#"+content).toggleClass("app-active");
}

/* Function for loading the Home screen. */
function loadHome() {
    title = "home";
    // Activates the Home screen.
    header("Home");
    changeLink(title);
    changeContent(title);
    
    // Loads user's name
    $(".app-user-name").text(name);
    
    // Reloads alerts and puts them on to the content.
}

/* Function for loading the Classes screen. */
function loadClasses() {
    title = "course";
    // Activates the Classes screen.
    header("Classes");
    changeLink(title);
    changeContent(title);
    
    // Loads the list of classes.
    //alert(url + "classes.json");
    getData(url + "classes.json", jsonClasses);
}

/* Function for loading the Assignments screen. */
function loadAssigns() {
    title = "assn";
    // Activates the Assignments screen.
    header("Assignments");
    changeLink(title);
    changeContent(title);
    
    // Loads all assignments.
    //alert(url + "assign.json");
    getData(url + "assign.json", jsonAssigns);
}

/* Function for loading the Grades screen. */
function loadGrades() {
    title = "grade";
    // Activates the Grades screen.
    header("Grades");
    changeLink(title);
    changeContent(title);
    
    // Reloads the grades for different assignments.
    //alert(url + "grade.json");
    getData(url + "grade.json", jsonGrades);
}

/* Function for loading the Settings screen. */
function loadSets() {
    title = "setting";
    // Activates the Settings screen.
    header("Settings");
    changeLink(title);
    changeContent(title);
    
    // Loads the users settings.
}
