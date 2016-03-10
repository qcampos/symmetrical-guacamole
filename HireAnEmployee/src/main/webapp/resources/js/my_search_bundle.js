/**
 * Closes the given element by appending it the closeAnimation
 * class.
 */
function closeAnimation(el) {
    var elem = document.getElementById(el);
    elem.className += " closeAnimation";
}