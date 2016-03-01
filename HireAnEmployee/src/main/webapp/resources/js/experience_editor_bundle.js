/* Script to handle experience-editor to show up. */
var editorClass = "col-xs-12 experience-editor";
var descriptionClass = "col-xs-12 experience-description";
var containerClass = "experience-editable-container";

function _(x) {
    return document.getElementById(x);
}

function showExperienceEditor(id) {
    var experienceDiv = _(id);
    var experienceEditor = _(id + '-editor');
    var experienceContainer = _(id + '-container');
    // Showing the editor.
    experienceEditor.className = editorClass + " expand";
    // Making the container the right new size.
    experienceContainer.className = containerClass + " expand";
    // Hidding the description.
    experienceDiv.className = descriptionClass;
    // Setting all childs to now call hide onclick.
    /* var links = experienceDiv.getElementsByTagName('a');
     for (var i = 0; i < links.length; i++) {
     links[i].setAttribute("onClick", "hideExperienceEditor('"+id+"');");
     }*/
}

function hideExperienceEditor(id) {
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