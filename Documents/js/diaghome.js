var hdrHeight = $("#top-hdr").height();
var hdrHeightPx = hdrHeight + 5 + "px";

$("body").animate({
    paddingTop: hdrHeightPx,
    duration: 'fast'
});

$("#rfrshBtn").click(function() {
    window.location.reload(true);
});

$("#rfrshBtn").hover(function(e) {
    $("#rfrshBtnIcn").toggleClass("fa-spin");
});

$(window).scroll(function() {
    if ($("body").scrollTop() > 20 || document.documentElement.scrollTop > 20) {
        $("#scrollTopBtn").css({
            display: "block"
        });
    } else {
        $("#scrollTopBtn").css({
            display: "none"
        });
    }
});

function topFunction() {
    //console.log("requested height:", hdrHeightPx, "| actual height:", $('body').scrollTop());
    document.body.scrollTop = hdrHeightPx;
    document.documentElement.scrollTop = hdrHeightPx;
}

$(function() {
    $("#stateUseCirc").percircle();
});



var btn = document.getElementById('copyUrlBtn');
var clipboard = new Clipboard('.btn');

clipboard.on('success', function(e) {
    console.info('Text:', e.text);
    //console.info('Trigger:', e.trigger);
    e.clearSelection();
});

clipboard.on('error', function(e) {
    //console.error('Action:', e.action);
    //console.error('Trigger:', e.trigger);
});

$("body").animate({
    paddingTop: hdrHeightPx
});

$("#rfrshBtn").click(function() {
    window.location.reload(true);
});

$("#rfrshBtn").hover(function(e) {
    $("#rfrshBtnIcn").toggleClass("fa-spin");
});

$(window).scroll(function() {
    if ($("body").scrollTop() > 20 || document.documentElement.scrollTop > 20) {
        $("#scrollTopBtn").css({
            display: "block"
        });
    } else {
        $("#scrollTopBtn").css({
            display: "none"
        });
    }
});

function topFunction() {
    //console.log("requested height:", hdrHeightPx, "| actual height:", $('body').scrollTop());
    document.body.scrollTop = hdrHeightPx;
    document.documentElement.scrollTop = hdrHeightPx;
}

$(function() {
    $("#stateUseCirc").percircle();
});



var btn = document.getElementById('copyUrlBtn');
var clipboard = new Clipboard('.btn');

clipboard.on('success', function(e) {
    console.info('Text:', e.text);
    //console.info('Trigger:', e.trigger);
    e.clearSelection();
});

clipboard.on('error', function(e) {
    //console.error('Action:', e.action);
    //console.error('Trigger:', e.trigger);
});

$("body").animate({
    paddingTop: hdrHeightPx,
    duration: 'fast'
});

$("#rfrshBtn").click(function() {
    window.location.reload(true);
});

$("#rfrshBtn").hover(function(e) {
    $("#rfrshBtnIcn").toggleClass("fa-spin");
});

$(window).scroll(function() {
    if ($("body").scrollTop() > 20 || document.documentElement.scrollTop > 20) {
        $("#scrollTopBtn").css({
            display: "block"
        });
    } else {
        $("#scrollTopBtn").css({
            display: "none"
        });
    }
});

function topFunction() {
    //console.log("requested height:", hdrHeightPx, "| actual height:", $('body').scrollTop());
    document.body.scrollTop = hdrHeightPx;
    document.documentElement.scrollTop = hdrHeightPx;
}

$(function() {
    $("#stateUseCirc").percircle();
});



var btn = document.getElementById('copyUrlBtn');
var clipboard = new Clipboard('.btn');

clipboard.on('success', function(e) {
    console.info('Text:', e.text);
    //console.info('Trigger:', e.trigger);
    e.clearSelection();
});

clipboard.on('error', function(e) {
    //console.error('Action:', e.action);
    //console.error('Trigger:', e.trigger);
});
