'use esversion: 6';

var copyBtn = document.getElementById('copyUrlBtn');
var clipboard = new Clipboard('.copyBtn');

function makeRequest(url, method, message, contentType, responseType) {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        // url += appId || '';
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    resolve(xhr.response);
                } else {
                    reject(Error(xhr.statusText));
                }
            }
        };
        xhr.onprogress = function() {
            // console.log('LOADING', xhr.readyState); // readyState will be 3
        };
        xhr.onerror = function() {
            reject(Error('XMLHttpRequest failed; error code:' + xhr.statusText));
        };
        xhr.open(method, url, true);
        if (contentType !== null && responseType !== null) {
            xhr.setRequestHeader('Accept', contentType);
            xhr.responseType = responseType;
            xhr.send();
        } else {
            xhr.send(message);
        }
    });
}

function topFunction() {
    //console.log("requested height:", hdrHeightPx, "| actual height:", $('body').scrollTop());
    document.body.scrollTop = hdrHeightPx;
    document.documentElement.scrollTop = hdrHeightPx;
}

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

$('#stateCleanupBtn').click(function(e) {
    var data = JSON.stringify({
        cmd: "stateCleanup",
        value: null
    });
    makeRequest(cmdUrl, 'POST', data, null, null)
        .catch(function(err) {
            console.log(err, 'Diag Command Results!');
        })
        .then(function(resp) {
            if (JSON.parse(resp).data) {
                console.log("diagCmd: Sent Successfully!");
            }
        });
});

$('#updateMethodBtn').click(function(e) {
    var data = JSON.stringify({
        cmd: "runUpdated",
        value: null
    });
    makeRequest(cmdUrl, 'POST', data, null, null)
        .catch(function(err) {
            console.log(err, 'Diag Command Results!');
        })
        .then(function(resp) {
            if (JSON.parse(resp).gotData) {
                console.log("diagCmd: Sent Successfully!");
            }
        });
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


$(function() {
    $("#stateUseCirc").percircle();
});

clipboard.on('success', function(e) {
    console.info('Text:', e.text);
    //console.info('Trigger:', e.trigger);
    e.clearSelection();
});

clipboard.on('error', function(e) {
    //console.error('Action:', e.action);
    //console.error('Trigger:', e.trigger);
});
