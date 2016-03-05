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
    // Hidding the description.
    experienceDiv.className = descriptionClass;
}


function hideCVEditor(id) {
    var experienceDiv = _(id);
    var experienceEditor = _(id + '-editor');
    var experienceContainer = _(id + '-container');
    // Hidding the editor.
    experienceEditor.className = editorClass;
    // Making the container the right new size.
    experienceContainer.className = containerClass;
    // Hidding the description.
    experienceDiv.className = descriptionClass + " expand";
    // Cleaning the form.
    var experienceForm = _(id+'-form');
    experienceForm.reset();
}

// Setting all childs to now call hide onclick.
/*var links = experienceDiv.getElementsByTagName('a');
 for (var i = 0; i < links.length; i++) {
 links[i].setAttribute("onClick", "hideExperienceEditor('"+id+"');");
 }*/