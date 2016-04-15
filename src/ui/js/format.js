/* Custom JavaScript functions */

/* Function for setting up header */
function header(pageName) {
    // Insert page name
    var title = $("<h1></h1>").text(pageName);
    $(".app-header").append(title);
}

/* Function for menu */


/* Function for setting up footer */
function footer() {
    // Get footer information
    var info = $("<h1></h1>").text("Footer information");
    $(".app-footer").append(info);
}
