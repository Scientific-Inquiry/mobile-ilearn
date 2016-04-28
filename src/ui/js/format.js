/* Custom JavaScript functions */
/* Global variables. */
var title = null;
var name = "Bruce Wayne";
var login = "bwayn052";
var url = "data/users/" + login + "/";
var graded = false;

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
        if(file.readyState == 4 && file.status == 200) {
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
    // Gets the quarter name
    var qyear = arr[0].quarter;
    // If qyear does not match the format such that the first letter of the quarter followed by the year, then output "Class Schedule".
    if(qyear.length != 5) { $("#courseQuarter").text("Class Schedule"); }
    else {
        var q = qyear.slice(0,1);   // Quarter letter
        var y = qyear.slice(1);     // Year (four digits)
        if((q === "F") || (q === "f")) { out = "Fall "; }
        else if((q === "W") || (q === 'w')) { out = "Winter "; }
        else if((q === "S") || (q === 's')) { out = "Spring "; }
        else if((q === "U") || (q === 'u')) { out = "Summer "; }
        else { out = "Unknown "; }
        if (isNaN(y)) { y = "Quarter"; }
        out += y;
        $("#courseQuarter").text(out);
    }
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

/* Function for generating a class list. */
/* Creates a set of collapsibles that contain a list of grades */
function jsonClassList(arr) {
    var out = "";
    var className = "";
    
    // Generates a collapsible with the classname and a blank list.
    for(var i = 0; i < arr.length; i++) {
        className = arr[i].courseNum + '-' + arr[i].courseSec;
        out += '<div data-role="collapsible" class="graded" id="graded-' + className + '"><h3>' + className + '</h3><ul data-role="listview" class="gradebook" id="gradebook-' + className + '"></ul></div>';
    }
    // Refreshes the grades collapsible set.
    $(".grades").html(out).collapsibleset("refresh");
}
/* Originally for panel list.
function jsonClassList(arrC) {
    out = "";
    var classId = "";
    var className = "";
    var classArr = [];
    for(i = 0; i < arrC.length; i++) {
        // Generates a list item with the class name and adds it to the array of class names.
        className = arrC[i].courseNum + ' (' + arrC[i].courseSec + ')';
        classId = arrC[i].courseNum + '-' + arrC[i].courseSec;
        out += '<li><a href="#graded-' + classId + '">' + className + '</a></li>';
        classArr.push(classId);
    }
    // Refreshes the list of classes.
    $(".grades").html(out).listview("refresh");
    
    // Generates gradebooks
    out = "";
    for(i = 0; i < classArr.length; i++) {
        out += '<div data-role="panel" data-position="right" data-display="overlay" id="graded-' + classArr[i] + '"><ul data-role="listview" data-inset="true" class="gradebook" id="gradebook-' + classArr[i] + '"></ul></div>';
        $("#graded-"+classArr[i]).panel("refresh");
    }
    $("#container").append(out);
}*/

/* Function for getting a user's grades. */
function jsonGrades(arr) {
    var out = "";
    var i;
    var prevClass = arr[0].courseNum + '-' + arr[0].courseSec;
    var currClass;
    
    for (i = 0; i < arr.length; i++) {
        currClass = arr[i].courseNum + '-' + arr[i].courseSec;
        if (prevClass != currClass) {
            // If the current class does not match the previous one, then the previous class's grades are put into the collapsible and the current class is now the previous class.
            $("#gradebook-" + prevClass).html(out);
            out = "";
            prevClass = currClass;
        }
        out += '<li>' + arr[i].title + '<br /><span class="gradedTotal">' + arr[i].grade + '/' + arr[i].total + '</span></li>';
    }
    $("#gradebook-" + currClass).html(out);
    $(".grades").collapsibleset("refresh");
}
/* Originally for a panel list.
function jsonGrades(arr) {
    var out = "";
    var i;
    
    // Gets the grades.
    var curClass = "";
    for(i = 0; (i < arr.length) && (!graded); i++) {
        // Generates the panel item holding the grade information
        curClass = arr[i].courseNum + '-' + arr[i].courseSec;
        alert(curClass);
        alert(arr[i].courseNum);
        out = '<li>'+ arr[i].title + '<br /><span class="gradedTotal">' + arr[i].grade + '/' + arr[i].total + '</span></li>';
        alert(out);
        $("#gradebook-" + curClass).append(out);
    }
    graded = true;
}*/

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
    getData(url + "classes.json", jsonClassList);
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
