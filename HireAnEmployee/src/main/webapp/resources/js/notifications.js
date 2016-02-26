// Created by Abwuds. HomeMade notifications for the J2EE course. 2016

var timeout;

function slideBack(el, colorClass) {
    clearTimeout(timeout);
    var elem = document.getElementById(el);
    elem.className = "out ";
    elem.className += colorClass;
}

function slideIn(el, colorClass) {
    clearTimeout(timeout);
    timeout = setTimeout(function () {
        slideBack(el, colorClass);
    }, 3700);
}