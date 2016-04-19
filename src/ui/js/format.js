/* Custom JavaScript functions */
/* Global variables. */
var title = null;
var name = "Bruce Wayne";
var login = "bwayn052";

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
    alert("Welcome to MiLearn!");
    
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
}

/* Function for loading the Assignments screen. */
function loadAssigns() {
    title = "assn";
    // Activates the Assignments screen.
    header("Assignments");
    changeLink(title);
    changeContent(title);
    
    // Loads all assignments.
}

/* Function for loading the Grades screen. */
function loadGrades() {
    title = "grade";
    // Activates the Grades screen.
    header("Grades");
    changeLink(title);
    changeContent(title);
    
    // Reloads the grades for different assignments.
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
