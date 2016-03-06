/* Script to handle experience-editor to show up. */
var editorClass = "col-xs-12 mycv-editor";
var descriptionClass = "col-xs-12 mycv-description";
var containerClass = "mycv-editable-container";

function _(x) {
    return document.getElementById(x);
}

function showExperienceEditor(id) {
    showCVElementEditor(id);
}

function showFormationEditor(id) {
    showCVElementEditor(id, 'formation-container-height');
}

function hideExperienceEditor(id) {
    hideCVEditor(id);
}

function hideFormationEditor(id) {
    hideCVEditor(id);
}


function showCVElementEditor(id, containerSupClass) {
    var experienceDiv = _(id);
    var experienceEditor = _(id + '-editor');
    var experienceContainer = _(id + '-container');
    // Showing the editor.
    experienceEditor.className = editorClass + " expand";
    // Making the container the right new size.
    experienceContainer.className = containerClass + " expand " + containerSupClass;
    // Hiding the description.
    experienceDiv.className = descriptionClass;
}

function handleAjaxCreationInMyCVList(data, id) {
    var div = _(id);
    // TODO make this method.
    div.visibility = "hidden";
    switch (data.status) {
        case "begin" :
            break;
        case "success" :
            div.visibility = "hidden";
            break;
    }
    // div.style
}
function hideCVEditor(id) {
    var experienceDiv = _(id);
    var experienceEditor = _(id + '-editor');
    var experienceContainer = _(id + '-container');
    // Hiding the editor.
    experienceEditor.className = editorClass;
    // Making the container the right new size.
    experienceContainer.className = containerClass;
    // Hiding the description.
    experienceDiv.className = descriptionClass + " expand";
    // Cleaning the form.
    var experienceForm = _(id + '-form');
    experienceForm.reset();
}

/* Calls fun on param only when data contains success.
 This is used for ajax result handling functions.
 */
function onSuccess(data, fun, param) {
    if (data.status == "success") {
        fun(param);
    }
}

function removeID(id) {
    var elem = _(id);
    if (elem == null) {
        console.log(id + ' not found.');
        return;
    }
    elem.remove();
}

// Setting all childs to now call hide onclick.
/*var links = experienceDiv.getElementsByTagName('a');
 for (var i = 0; i < links.length; i++) {
 links[i].setAttribute("onClick", "hideExperienceEditor('"+id+"');");
 }*/

