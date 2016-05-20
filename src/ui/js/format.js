/* Custom JavaScript functions */
/* Global variables. */
/* User data information */
var login;
var name;
var typeS = false;
var typeI = false;
var url;

var priLow = 7;     // Low priority is by default 7 days (7-21 days)
var priMed = 3;     // Medium priority is by default 3 days (3-6 days)
var priHigh = 24;   // High priority is by default 24 hours (1-72 hours)
var quarter;

/* Initializes the user's data. */
function init(user) {
    // Gets the user data
    function jsonUser(arr) {
        login = arr[0].login;
        name = arr[0].name;
        priHigh = arr[0].notifyH;
        priMed = arr[0].notifyM;
        priLow = arr[0].notifyL;
        var utype = Number(arr[0].type);
        if((utype == 0) || (utype == 2)) { typeS = true; }
        if((utype == 1) || (utype == 2)) { typeI = true; }
        else if((utype > 2) || (utype < 0)) { alert(name + " is not a user!"); }
        
        // Sets the user's theme
        var themeUse = arr[0].theme;
        changeTheme(themeUse);
    }
    getQuarter();
    url = "data/users/" + user + "/";
    getData(url + "user.json", jsonUser);
    $(document).ready(function() {
        menu();
        $("#app-wait-img").click(function() {
            loadHome();
        });
    });
}

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
    $("#app-site").click(function(){
        androidWebsite();
    });
    $("#app-logout").click(function(){
        androidLogout();
    });
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

/* Function for getting the quarter. 
 * Returns a five character string.*/
function getQuarter() {
    var now = new Date();
    var q;
    // Gets the month
    if(now.getMonth() < 3) {
        q = "w";    // Winter quarter (0-2)
    }
    else if(now.getMonth() < 6) {
        q = "s";    // Spring quarter (3-5)
    }
    else if(now.getMonth() < 9) {
        q = "u";    // Summer Session (6-8)
    }
    else if(now.getMonth() < 12) {
        q = "f";
    }
    else {
        q = "q";    // Quarter not recognized
    }
    // Gets the year.
    q += now.getFullYear();
    quarter = q;
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

/* Function for getting a student's classes. */
function jsonClasses(arr) {
    // Sets up quarter and title
    quarter = arr[0].quarter;
    $("#app-enrolled").removeClass("app-label");
    var out = "";
    var i;
    var idNum = 0;
    for(i = 0; i < arr.length; i++) {
        idNum++;
        // Generates a collapsible with the course information.
        out += '<div data-role="collapsible" id="classS' + idNum + '"><h3 class="scheduled">' + arr[i].courseNum + '-' + arr[i].courseSec + '</h3><p>Class Name: ' + arr[i].courseName + '<br />Instructor: ' + arr[i].instructor + '</p></div>';
    }
    // Refreshes the schedule collapsible set.
    $("#courseS").html(out).collapsibleset("refresh");
}

/* Function for getting a user's assignments. */
function jsonAssigns(arr) {
    var out = "";
    var i;
    var idNum = 0;
    for(i = 0; i < arr.length; i++) {
        idNum++;
        // Generates a collapsible with the assignment.
        var className = arr[i].courseNum + '-' + arr[i].courseSec;
        out += '<div data-role="collapsible" id="assnNum' + idNum + '"><h3 class="assigned">' + className + ': ' + arr[i].title + '</h3><form class="ui-form" action="http://ec2-52-37-165-140.us-west-2.compute.amazonaws.com:8080/UploadServlet" method="post" enctype="multipart/form-data"><fieldset data-role="fieldcontain" id="assnField' + idNum + '"><input type="hidden" name="aid" value="' + arr[i].aid + '"><input type="hidden" name="slogin" value="' + login + '" readonly><input type="file" name="file"><input type="submit" value="Turn in assignment"></fieldset></form><p>' + arr[i].desc + '</p><p>Due: ' + arr[i].due + '</p><p>Points: ' + arr[i].points + '</p></div>';
    }
    // Refreshes the assignments collapsible set.
    $("#assignS").html(out).collapsibleset("refresh");
    $("#assn").trigger("create");
}

/* Function for generating a student's class list. */
/* Creates a set of collapsibles that contain a list of grades */
function jsonClassList(arr) {
    var out = "";
    var className = "";
    
    // Generates a collapsible with the classname and a blank list.
    for(var i = 0; i < arr.length; i++) {
        className = arr[i].courseNum + '-' + arr[i].courseSec;
        out += '<div data-role="collapsible" class="graded" id="graded-' + className + '"><h3>' + className + '</h3><ul data-role="listview" data-inset="true" class="gradebook" id="gradebook-' + className + '"><li>No grades are available for this class!</li></ul></div>';
    }
    // Refreshes the grades collapsible set.
    $("#gradedS").html(out).collapsibleset("refresh");
}

/* Function for getting a student's grades. */
/* Function for getting a student's grades. */
function jsonGrades(arr) {
    var i;
    var prevClass = arr[0].courseNum + '-' + arr[0].courseSec;
    var currClass;
    var totalGrade = 0;
    var totalPoints = 0;
    var arrClasses = [];
    var arrGrades = [];
    var out = '<li><strong>Total</strong> <span id="gradedTotal-' + prevClass + '"></span></li>';
    
    for(i = 0; i < arr.length; i++) {
        currClass = arr[i].courseNum + '-' + arr[i].courseSec;
        if (prevClass != currClass) {
            // If the current class does not match the previous one, then the previous class's grades are put into the collapsible and the current class is now the previous class.
            $("#gradebook-" + prevClass).html(out);
            var graded = totalGrade + " / " + totalPoints;
            arrClasses.push(prevClass);
            arrGrades.push(graded);
            prevClass = currClass;
            out = '<li><strong>Total</strong> <span id="gradedTotal-' + prevClass + '"></span></li>';
            totalGrade = 0;
            totalPoints = 0;
        }
        out += '<li>' + arr[i].title + '<br />' + arr[i].grade + '/' + arr[i].total + '</span></li>';
        totalGrade += Number(arr[i].grade);
        totalPoints += Number(arr[i].total);
    }
    var graded = totalGrade + " / " + totalPoints;
    arrClasses.push(prevClass);
    arrGrades.push(graded);
    $("#gradebook-" + prevClass).html(out);
    $("#gradedS").collapsibleset("refresh");
    
    /* Gets the total grades. */
    for(i = 0; i < arrClasses.length; i++) {
        alert(arrClasses[i] + " - " + arrGrades[i]);
        $("#gradedTotal-" + arrClasses[i]).text(arrGrades[i]);
    }
}

/* Function for getting alerts. */
function jsonAlerts(arr) {
    var out = '<li data-role="list-divider" id="app-priority">Alerts</li>';
    var i;
    
    var now = new Date();
    // Gets the assignment and checks it against the current time.
    for(i = 0; i < arr.length; i++) {
        // Gets the difference in milliseconds between the assignment due date and the current time.
        var assn = new Date(arr[i].due);
        var diff = assn.getTime() - now.getTime();
        if(diff > 0) {
            // Get the difference in hours.
            var diffH = Math.floor(diff / (1000 * 60 * 60));
            if(diffH < priHigh) { // Gets high priority
                out += '<li class="priorityHigh">' + arr[i].courseNum + '&mdash;' + arr[i].title + '<br />' + diffH + ' hours left</li>'; 
            }
            else {
                // Gets the difference in days.
                var diffD = Math.floor(diffH / 24);
                diffH -= (diffD * 24);
                if(diffD < priMed) { // Gets medium priority
                    out += '<li class="priorityMed">' + arr[i].courseNum + '&mdash;' + arr[i].title + '<br />' + diffD + ' days left</li>';
                }
                else if(diffD < priLow) { // Gets low priority
                    out +='<li class="priorityLow">' + arr[i].courseNum + '&mdash;' + arr[i].title + '<br />' + diffD + ' days left</li>';
                }
            }
        }
    }
    $("#app-alerts").html(out).listview("refresh");
}

/* Function for getting the classes an instructor teaches. */
function jsonCourse(arr) {
    // Sets up quarter and title
    quarter = arr[0].quarter;
    $("#app-teaching").removeClass("app-label");
    var out = "";
    var i = 0;
    var idNum = 0;
    var currClass = arr[0].courseNum + '-' + arr[0].courseSec;
    var currStudent = "";
    for(i = 0; i < arr.length; i++) {
        // Generates a collapsible with the course information.
        if(currClass !== currStudent) {
            idNum++;
            currClass = arr[i].courseNum + '-' + arr[i].courseSec; 
            out += '<div data-role="collapsible" id="classI' + idNum + '"><h3 class="scheduled">' + currClass + '</h3><p>Class Name: ' + arr[i].courseName + '</p><table data-role="table" class="ui-responsive"><thead><tr><th>Student Name</th><th>Student Login</th></tr></thead><tbody>';
        }
        // Gets the students in the class.
        out += '<tr><td>' + arr[i].sname + '</td><td>' + arr[i].slogin + '</td></tr>';
        var next = i + 1;
        if(next < arr.length) {
            currStudent = arr[next].courseNum + '-' + arr[next].courseSec;
            // Closes the collapsible
            if(currStudent !== currClass) { out += '</tbody></table></div>'; }
        }
    }
    // Refreshes the schedule collapsible set.
    $("#courseI").html(out).collapsibleset("refresh");
}

/* Function for setting up new assignment options. */
function jsonClassOption(arr) {
    var i;
    var className = arr[0].courseNum + '-' + arr[0].courseSec;
    var out = '<option value=' + className + ' selected>' + className + '</option>';
    for(i = 0; i < arr.length; i++) {
        var curr = arr[i].courseNum + '-' + arr[i].courseSec;
        if(curr != className) {
            className = curr;
            out += '<option value=' + className + '>' + className + '</option>';
        }
    }
    $("#app-class-option").html(out).selectmenu("refresh");
}

/* Function for getting the assignments that an instructor assigned. */
function jsonAssigner(arr) {
    var out = "";
    var i;
    var idNum = 0;
    var className = arr[0].courseNum + '-' + arr[0].courseSec;
    for(i = 0; i < arr.length; i++) {
        idNum++;
        className = arr[i].courseNum + '-' + arr[i].courseSec;
        // Generates a collapsible with the assignment.
        out += '<div data-role="collapsible" id="assnNum' + idNum + '"><h3 class="assigned">' + className + ': ' + arr[i].title + '</h3><form action="http://ec2-52-37-165-140.us-west-2.compute.amazonaws.com:8080/AppFormAssign" method="post" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded" autocomplete="off" novalidate><fieldset data-role="fieldcontain" id="assnField' + idNum + '"><div class="ui-field-contain"><input type="hidden" name="slogin" value="' + login + '" readonly><input type="hidden" name="aid" value="' + arr[i].aid + '" readonly><p>Assignment Title: <input type="text" name="title" value="' + arr[i].title + '" maxlength="50" required><br /><label for="description">Description: </label><textarea name="description" rows="10" cols="15" maxlength="150">' + arr[i].desc + '</textarea></p><p>Due: ' + arr[i].due + '<br />New Due Date<br />(mm/dd/yyyy, hh:dd AM/PM):<br /><input type="datetime-local" name="dueDate" required></p><p>Points: <input type="text" name="grade" value="' + arr[i].points + '" maxlength="4" size="3" required></p><input type="submit" value="Submit"></div></fieldset></form></div>';
    }
    // Refreshes the assignments collapsible set.
    $("#assignI").html(out).collapsibleset("refresh");
    $("#assn").trigger("create");
}

/* Function for displaying the grades for a class. */
function jsonGrader(arr) {
    var out = "";
    var i;
    var currAssn = "";
    var idNum = 0;
    for(i = 0; i < arr.length; i++) {
        var currGrade = arr[i].title;
        // Generates a collapsible with the course information.
        if(currAssn !== currGrade) {
            idNum++;
            currAssn = currGrade;
            var className = arr[i].courseNum + '-' + arr[i].courseSec
            out += '<div data-role="collapsible" id="gradeI' + idNum + '"><h3 class="grader">' + className + ': ' + currAssn + '</h3><p>Assignment Title: ' + arr[i].title + '<br /><form action="http://ec2-52-37-165-140.us-west-2.compute.amazonaws.com:8080/AppFormGradebook" method="post" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded" autocomplete="off" novalidate>Total Points: ' + '<input type="text" name="points" value="' + arr[i].total + '" size="3"></p><input type="hidden" name="aid" value="' + arr[i].aid + '" readonly><input type="hidden" name="slogin" value="' + login + '" readonly><fieldset><div class="ui-field-contain"><table data-role="table" class="ui-responsive"><thead><tr><th>Student<br /> Login</th><th>Student<br /> Grade</th><th>Percent</tr></thead><tbody>';
        }
        // Gets the students in the class.
        var percent = Number((arr[i].grade / arr[i].total) * 100);
        out += '<tr';
        if(arr[i].late === "true") { out += ' class="grade-late" '; }
        out += '><td>' + arr[i].slogin + '</td><td><input type="text" name="' + arr[i].slogin + '" maxlength="4" size="3" value="' + arr[i].grade + '"></td><td>' + percent + '%</td></tr>';
        var next = i + 1;
        if(next < arr.length) {
            currGrade = arr[next].title;
            // Closes the collapsible
            if(currAssn !== currGrade) { out += '</tbody></table><input type="submit" value="Submit"></div></fieldset></form></div>'; }
        }
    }
    out += '</tbody></table><input type="submit" value="Submit"></div></fieldset></form></div>';
    $("#gradedI").html(out).collapsibleset("refresh");
}

/* Functions for changing information on pages. */
/* Function for getting the name of the quarter. */
function changeQuarter() {
    // If quarter does not match the format such that the first letter of the quarter followed by the year, then output "Class Schedule".
    if(quarter.length != 5) { $("#courseQuarter").text("Class Schedule"); }
    else {
        var q = quarter.slice(0,1);   // Quarter letter
        var y = quarter.slice(1);     // Year (four digits)
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

/* Function for changing the theme. */
function changeTheme(themeUse) {
    // Get the old theme
    var themeOld = $("#container").attr("data-theme");
    $("#container").attr("data-theme", themeUse);
    // Changes all of the CSS classes to match the current theme
    if(themeOld !== themeUse) {
        // Array of themed classes
        var arrTheme = ["ui-overlay-", 
                        "ui-page-theme-", 
                        "ui-body-", 
                        "ui-bar-", 
                        "ui-group-theme-",
                        "ui-btn-",
                        "ui-panel-page-container-"];
        for(var i = 0; i < arrTheme.length; i++) {
            var currTheme = arrTheme[i] + themeOld;
            $("." + currTheme).addClass(arrTheme[i] + themeUse);
            $("." + currTheme).removeClass(currTheme);
        }
        // Sets the selection in the Select list.
        $("#app-theme-select").val(themeUse).attr("selected", true).siblings("option").removeAttr("selected");
        $("#app-theme-select").selectmenu("refresh");
    }
}

/* Functions for loading screens. */
/* Function for loading the Home screen. */
function loadHome() {
    var title = "home";
    // Activates the Home screen.
    header("Home");
    changeLink(title);
    changeContent(title);
    
    // Loads user's name
    $(".app-user-name").text(name);
    
    // Reloads alerts and puts them on to the content.
    if(typeS) { getData(url + "assign.json", jsonAlerts); }
    if(typeI) { getData(url + "assigner.json", jsonAlerts); }
}

/* Function for loading the Classes screen. */
function loadClasses() {
    var title = "course";
    // Activates the Classes screen.
    header("Classes");
    changeLink(title);
    changeContent(title);
    
    // Loads the list of classes.
    if(typeS) { getData(url + "classes.json", jsonClasses); }
    if(typeI) { getData(url + "course.json", jsonCourse); }
    changeQuarter();
}

/* Function for loading the Assignments screen. */
function loadAssigns() {
    var title = "assn";
    // Activates the Assignments screen.
    header("Assignments");
    changeLink(title);
    changeContent(title);
    
    // Loads all assignments.
    if(typeS) { 
        $("#assnS").removeClass("app-label");
        getData(url + "assign.json", jsonAssigns);
    }
    if(typeI) { 
        $("#assnI").removeClass("app-label");
        $("#assnNew").removeClass("app-label");
        $(".app-form-login").attr("value", login);
        getData(url + "course.json", jsonClassOption);
        getData(url + "assigner.json", jsonAssigner);
    }
}

/* Function for loading the Grades screen. */
function loadGrades() {
    var title = "grade";
    // Activates the Grades screen.
    header("Grades");
    changeLink(title);
    changeContent(title);
    
    // Reloads the grades for different assignments.
    if(typeS) {
        getData(url + "classes.json", jsonClassList);
        getData(url + "grade.json", jsonGrades);
    }
    if(typeI) {
        getData(url + "graded.json", jsonGrader);
    }
}

/* Function for loading the Settings screen. */
function loadSets() {
    var title = "setting";
    // Activates the Settings screen.
    header("Settings");
    changeLink(title);
    changeContent(title);
    
    // Sets up the forms with the user login.
    $(".app-form-login").attr("value", login);
    
    // Loads the users settings.
    // Sets up user theme changing
    $("#app-theme-select").change(function() {
       changeTheme(this.value); 
    });
    // Sets the current user's setting into the fields.
    $("#priHigh").attr("value", priHigh);
    $("#priMed").attr("value", priMed);
    $("#priLow").attr("value", priLow);
}

/* Functions for setting up Android functions. */
/* Function for logging out. */
function androidLogout() {
    alert("You are logging out!");
    Android.logout();
}

/* Function for going to Crumb Lords website in browser. */
function androidWebsite() {
    Android.openWebsite();
}

/* Function for logging in the user. */
function androidLogin() {
    login = Android.getUser();
    /* For testing purposes. */
    //login = "ckent038";   // Instructor user
    //login = "bwayn052";   // Student user
    return login;
}
