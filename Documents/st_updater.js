var url = new URL(window.location);
if (sessionStorage.refreshAgain === undefined || sessionStorage.refreshAgain === 'true') {
    sessionStorage.refreshCount = 1;
    sessionStorage.refreshAgain = 'false';
    document.write('<meta http-equiv="refresh" content="3;' + url + '"/>');
} else {
    sessionStorage.refreshAgain = 'true';
    sessionStorage.refreshCount = Number(sessionStorage.refreshCount) + 1;
}

function makeRequest(url, method, message, async = true, typeId = null, typeDesc = null) {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        url += typeId || '';
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    if (typeId && typeDesc) {
                        // console.log(xhr.response);
                        resolve({ response: xhr.response, typeId: typeId, typeDesc: typeDesc });
                    } else {
                        resolve(xhr.response);
                    }
                } else {
                    reject(Error(xhr.statusText));
                }
            }
        };
        xhr.onprogress = function() {
            // console.log('LOADING', xhr.readyState); // readyState will be 3
        };
        xhr.onerror = function() {
            if (typeId && typeDesc) {
                reject({ statusText: xhr.statusText, typeId: typeId, typeDesc: typeDesc });
            } else {
                reject(Error('XMLHttpRequest failed; error code:' + xhr.statusText));
            }
        };
        xhr.open(method, url, async);
        xhr.send(message);
    });
}

function addResult(str, good) {
    resultList.style.display = 'block';
    resultsTitle.style.display = 'block';
    let s = "<li><span style='color: " + (good !== false ? '#25c225' : '#FF0000') + ";'>";
    s += "<i class='fa fa-" + (good !== false ? 'check' : 'exclamation') + "'></i>";
    s += '</span> ' + str + '</li>';
    $('#resultList ul').append(s);
}

function installError(err, reload = true) {
    console.log('installError:', err);
    if (reload) {
        if (sessionStorage.refreshCount < 7) {
            $('#results').text('Reloading Page in 3 seconds');
            location.reload(true);
        } else {
            loader.style.display = 'none';
            results.style.display = 'block';
            document.getElementById('results').innerHTML = '<br><button onclick="location.reload();">Reload page</button>';
        }
    }
}

function installComplete(text, red = false) {
    loader.style.display = 'none';
    finishedImg.style.display = 'block';
    if (red) {
        finishedImg.style.color = 'red';
    }
    results.style.display = 'block';
    document.getElementById('results').innerHTML = text;
    sessionStorage.removeItem('appsDone');
    sessionStorage.removeItem('devsDone');
}

function runStUpdates() {
    var appsDone = [];
    var devsDone = [];
    $('#loaderText2').text('Authenticating');
    $('#loaderText1').text('Please Wait');
    makeRequest(authUrl, 'GET', null, false)
        .then(function(response) {
            addResult('SmartThings Authentication', true);
            if (appsDone.length < Object.keys(appIds).length) {
                for (var i in appIds) {
                    var appDesc = i.toString();
                    var appId = appIds[i];
                    var appType;
                    // console.log('appDesc: '+appDesc)
                    if (appDesc !== undefined) {
                        if (appDesc.toString() === 'main') {
                            appType = 'NST Manager';
                        } else if (appDesc.toString() === 'auto') {
                            appType = 'Automation App';
                        }
                    }
                    $('#loaderText2').text('Checking');
                    $('#loaderText1').text(appType);
                    makeRequest(appUpd1Url, 'GET', null, true, appId, appType)
                        .catch(function(errResp1) {
                            installError(errResp1, false);
                            addResult(errResp1.typeDesc + ' Update Issue', false);
                        })
                        .then(function(stResp1) {
                            // console.log(stResp1);
                            let respData = JSON.parse(stResp1.response);
                            if (respData.hasDifference === true) {
                                $('#loaderText2').text('Updating');
                                $('#loaderText1').text(stResp1.typeDesc);
                                makeRequest(appUpd2Url, 'GET', null, true, stResp1.typeId, stResp1.typeDesc)
                                    .catch(function(errResp2) {
                                        installError(errResp2, false);
                                        addResult(errResp2.typeDesc + ' Update Issue', false);
                                    })
                                    .then(function(stResp2) {
                                        if (!JSON.parse(stResp2.response).errors.length) {
                                            $('#loaderText2').text('Compiling');
                                            $('#loaderText1').text(stResp2.typeDesc);
                                            // console.log("stResp2(" + stResp2.typeId + "):", JSON.parse(stResp2.response));
                                            makeRequest(appUpd3Url, 'GET', null, true, stResp2.typeId, stResp2.typeDesc)
                                                .catch(function(errResp3) {
                                                    addResult(errResp3.typeDesc + ' Update Issue', false);
                                                    installError(errResp3, false);
                                                })
                                                .then(function(stResp3) {
                                                    // console.log("stResp3(" + stResp3.typeId + "):", JSON.parse(stResp3.response));
                                                    addResult(stResp3.typeDesc + ' was Updated', true);
                                                    appsDone.push(stResp3.typeDesc);
                                                    sessionStorage.setItem('appsDone', appsDone);
                                                    if (appsDone.length === Object.keys(appIds).length) {
                                                        //installComplete('Updates are Complete!<br/>Everything is Good!');
                                                        if (devsDone.length < Object.keys(devIds).length) {
                                                            for (var i in devIds) {
                                                                var devDesc = i.toString();
                                                                var devId = devIds[i];
                                                                var devType;
                                                                // console.log('devDesc: '+devDesc)
                                                                if (devDesc !== undefined) {
                                                                    if (devDesc.toString() === "tstat") { devType = "Thermostat Device"; }
                                                                    else if (devDesc.toString() === "protect") { devType = "Protect Device"; }
                                                                    else if (devDesc.toString() === "camera") { devType = "Camera Device"; }
                                                                    else if (devDesc.toString() === "presence") { devType = "Presence Device"; }
                                                                    else if (devDesc.toString() === "weather") { devType = "Weather Device"; }
                                                                }
                                                                $('#loaderText2').text('Checking');
                                                                $('#loaderText1').text(devType);
                                                                makeRequest(devUpd1Url, 'GET', null, true, devId, devType)
                                                                    .catch(function(errResp1) {
                                                                        installError(errResp4, false);
                                                                        addResult(errResp4.typeDesc + ' Update Issue', false);
                                                                    })
                                                                    .then(function(stResp4) {
                                                                        // console.log(stResp4);
                                                                        let respData = JSON.parse(stResp4.response);
                                                                        if (respData.hasDifference === true) {
                                                                            $('#loaderText2').text('Updating');
                                                                            $('#loaderText1').text(stResp4.typeDesc);
                                                                            makeRequest(devUpd2Url, 'GET', null, true, stResp4.typeId, stResp4.typeDesc)
                                                                                .catch(function(errResp5) {
                                                                                    installError(errResp5, false);
                                                                                    addResult(errResp5.typeDesc + ' Update Issue', false);
                                                                                })
                                                                                .then(function(stResp5) {
                                                                                    if (!JSON.parse(stResp5.response).errors.length) {
                                                                                        $('#loaderText2').text('Compiling');
                                                                                        $('#loaderText1').text(stResp5.typeDesc);
                                                                                        // console.log("stResp5(" + stResp5.typeId + "):", JSON.parse(stResp5.response));
                                                                                        makeRequest(devUpd3Url, 'GET', null, true, stResp5.typeId, stResp5.typeDesc)
                                                                                            .catch(function(errResp6) {
                                                                                                addResult(errResp6.typeDesc + ' Update Issue', false);
                                                                                                installError(errResp6, false);
                                                                                            })
                                                                                            .then(function(stResp6) {
                                                                                                // console.log("stResp6(" + stResp6.typeId + "):", JSON.parse(stResp6.response));
                                                                                                addResult(stResp6.typeDesc + ' was Updated', true);
                                                                                                devsDone.push(stResp6.typeDesc);
                                                                                                sessionStorage.setItem('devsDone', devsDone);
                                                                                                if (devsDone.length === Object.keys(devIds).length) {
                                                                                                    installComplete('Updates are Complete!<br/>Everything is Good!');
                                                                                                }
                                                                                            });
                                                                                    }
                                                                                });
                                                                        } else {
                                                                            addResult(stResp4.typeDesc + ' is Up-to-Date', true);
                                                                            devsDone.push(stResp4.typeDesc);
                                                                            sessionStorage.setItem('devsDone', devsDone);
                                                                            if (devsDone.length === Object.keys(devIds).length) {
                                                                                installComplete('Updates are Complete!<br/>Everything is Good!');
                                                                            }
                                                                        }
                                                                    });
                                                            }
                                                        }
                                                    }
                                                });
                                        }
                                    });
                            } else {
                                addResult(stResp1.typeDesc + ' is Up-to-Date', true);
                                appsDone.push(stResp1.typeDesc);
                                sessionStorage.setItem('appsDone', appsDone);
                                if (appsDone.length === Object.keys(appIds).length) {
                                    //installComplete('Updates are Complete!<br/>Everything is Good!');
                                    if (devsDone.length < Object.keys(devIds).length) {
                                        for (var i in devIds) {
                                            var devDesc = i.toString();
                                            var devId = devIds[i];
                                            var devType;
                                            // console.log('devDesc: '+devDesc)
                                            if (devDesc !== undefined) {
                                                if (devDesc.toString() === "tstat") { devType = "Thermostat Device"; }
                                                else if (devDesc.toString() === "protect") { devType = "Protect Device"; }
                                                else if (devDesc.toString() === "camera") { devType = "Camera Device"; }
                                                else if (devDesc.toString() === "presence") { devType = "Presence Device"; }
                                                else if (devDesc.toString() === "weather") { devType = "Weather Device"; }
                                            }
                                            $('#loaderText2').text('Checking');
                                            $('#loaderText1').text(devType);
                                            makeRequest(devUpd1Url, 'GET', null, true, devId, devType)
                                                .catch(function(errResp1) {
                                                    installError(errResp4, false);
                                                    addResult(errResp4.typeDesc + ' Update Issue', false);
                                                })
                                                .then(function(stResp4) {
                                                    // console.log(stResp4);
                                                    let respData = JSON.parse(stResp4.response);
                                                    if (respData.hasDifference === true) {
                                                        $('#loaderText2').text('Updating');
                                                        $('#loaderText1').text(stResp4.typeDesc);
                                                        makeRequest(devUpd2Url, 'GET', null, true, stResp4.typeId, stResp4.typeDesc)
                                                            .catch(function(errResp5) {
                                                                installError(errResp5, false);
                                                                addResult(errResp5.typeDesc + ' Update Issue', false);
                                                            })
                                                            .then(function(stResp5) {
                                                                if (!JSON.parse(stResp5.response).errors.length) {
                                                                    $('#loaderText2').text('Compiling');
                                                                    $('#loaderText1').text(stResp5.typeDesc);
                                                                    // console.log("stResp5(" + stResp5.typeId + "):", JSON.parse(stResp5.response));
                                                                    makeRequest(devUpd3Url, 'GET', null, true, stResp5.typeId, stResp5.typeDesc)
                                                                        .catch(function(errResp6) {
                                                                            addResult(errResp6.typeDesc + ' Update Issue', false);
                                                                            installError(errResp6, false);
                                                                        })
                                                                        .then(function(stResp6) {
                                                                            // console.log("stResp6(" + stResp6.typeId + "):", JSON.parse(stResp6.response));
                                                                            addResult(stResp6.typeDesc + ' was Updated', true);
                                                                            devsDone.push(stResp6.typeDesc);
                                                                            sessionStorage.setItem('devsDone', devsDone);
                                                                            if (devsDone.length === Object.keys(devIds).length) {
                                                                                installComplete('Updates are Complete!<br/>Everything is Good!');
                                                                            }
                                                                        });
                                                                }
                                                            });
                                                    } else {
                                                        addResult(stResp4.typeDesc + ' is Up-to-Date', true);
                                                        devsDone.push(stResp4.typeDesc);
                                                        sessionStorage.setItem('devsDone', devsDone);
                                                        if (devsDone.length === Object.keys(devIds).length) {
                                                            installComplete('Updates are Complete!<br/>Everything is Good!');
                                                        }
                                                    }
                                                });
                                        }
                                    }
                                }
                            }
                        });
                }
            }

        })
        .catch(function(err) {
            // addResult('ST Authentication', false);
            installError(err);
        });
}

window.onload = function() {
    if (sessionStorage.refreshAgain === 'false') {
        document.getElementById('results').innerHTML = 'Waiting for connection...';
    } else {
        runStUpdates();
    }
    $('body').flowtype({
        minFont: 17,
        maxFont: 30,
        minimum: 300,
        maximum: 900,
        fontRatio: 30
    });
};
