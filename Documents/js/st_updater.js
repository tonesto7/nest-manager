'use esversion: 6';

function makeRequest(url, method, message, appId = null, appDesc = null, contentType = null, responseType = null) {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        url += appId || '';
        xhr.onreadystatechange = function() {
            if (xhr.readyState === XMLHttpRequest.DONE) {
                if (xhr.status === 200) {
                    if (appId !== null && appDesc !== null) {
                        // console.log(xhr.response);
                        resolve({
                            response: xhr.response,
                            appId: appId,
                            appDesc: appDesc
                        });
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
            if (appId !== null && appDesc !== null) {
                reject({
                    statusText: xhr.statusText,
                    appId: appId,
                    appDesc: appDesc
                });
            } else {
                reject(Error('XMLHttpRequest failed; error code:' + xhr.statusText));
            }
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

function addResult(str, good) {
    // $('#results').css({ display: 'none' });
    $('#resultList').css({
        display: 'block'
    });
    $('#resultsTitle').css({
        display: 'block'
    });
    let s = "<li><span style='color: " + (good !== false ? '#25c225' : '#FF0000') + ";'>";
    s += "<i class='fa fa-" + (good !== false ? 'check' : 'exclamation') + "'></i>";
    s += '</span> ' + str + '</li>';
    $('#resultList ul').append(s);
}

function installError(err, reload = true) {
    if (reload) {
        if (sessionStorage.refreshCount < 7) {
            loaderFunc();
        } else {
            installComplete(err, true);
        }
    }
}

function installComplete(text, red = false) {
    loaderDiv.style.display = 'none';
    $('#loaderDiv').css({
        display: 'none'
    });
    $('#finishedImg').removeClass('fa-exclamation-circle').addClass('fa-check').css({
        display: 'block'
    });
    if (red) {
        $('#finishedImg').removeClass('fa-check').addClass('fa-exclamation-circle').css({
            color: 'red'
        });
    }
    $('#results')
        .css({
            display: 'block'
        })
        .html(text + '<br/><br/>Press Back/Done Now');
    sessionStorage.removeItem('appsDone');
    sessionStorage.removeItem('refreshCount');
}

function stUpdates() {
    var appsDone = [];
    var devsDone = [];
    $('#loaderText1').text('Authenticating');
    $('#loaderText2').text('Please Wait');
    makeRequest(authUrl, 'GET', null, false)
        .then(function(response) {
            $('#results').text('');
            addResult('SmartThings Authentication', true);
            if (appsDone.length < Object.keys(appIds).length) {
                for (var i in appIds) {
                    var appDesc = i.toString();
                    var appId = appIds[i];
                    var appType;
                    // console.log('appDesc: ' + appDesc);
                    if (appDesc !== undefined) {
                        if (appDesc.toString() === 'main') {
                            appType = 'NST Manager';
                        } else if (appDesc.toString() === 'auto') {
                            appType = 'Automation App';
                        }
                    }
                    $('#loaderText1').text('Checking');
                    $('#loaderText2').text(appType);
                    makeRequest(appUpd1Url, 'GET', null, appId, appType)
                        .catch(function(errResp1) {
                            installError(errResp1, false);
                            addResult(errResp1.appDesc + ' Update Issue', false);
                        })
                        .then(function(stResp1) {
                            // console.log(stResp1);
                            var respData = JSON.parse(stResp1.response);
                            if (respData.hasDifference === true) {
                                $('#loaderText1').text('Updating');
                                $('#loaderText2').text(stResp1.appDesc);
                                makeRequest(appUpd2Url, 'GET', null, stResp1.appId, stResp1.appDesc)
                                    .catch(function(errResp2) {
                                        installError(errResp2, false);
                                        addResult(errResp2.appDesc + ' Update Issue', false);
                                    })
                                    .then(function(stResp2) {
                                        if (!JSON.parse(stResp2.response).errors.length) {
                                            $('#loaderText1').text('Compiling');
                                            $('#loaderText2').text(stResp2.appDesc);
                                            // console.log("stResp2(" + stResp2.appId + "):", JSON.parse(stResp2.response));
                                            makeRequest(appUpd3Url, 'GET', null, stResp2.appId, stResp2.appDesc)
                                                .catch(function(errResp3) {
                                                    addResult(errResp3.appDesc + ' Update Issue', false);
                                                    installError(errResp3, false);
                                                })
                                                .then(function(stResp3) {
                                                    // console.log("stResp3(" + stResp3.appId + "):", JSON.parse(stResp3.response));
                                                    addResult(stResp3.appDesc + ' was Updated', true);
                                                    appsDone.push(stResp3.appDesc);
                                                    sessionStorage.setItem('appsDone', appsDone);
                                                    if (appsDone.length === Object.keys(appIds).length) {
                                                        // installComplete('Updates are Complete!<br/>Everything is Good!');
                                                        if (devsDone.length < Object.keys(devIds).length) {
                                                            for (var i in devIds) {
                                                                var devDesc = i.toString();
                                                                var devId = devIds[i];
                                                                var devType;
                                                                // console.log('devDesc: '+devDesc)
                                                                if (devDesc !== undefined) {
                                                                    if (devDesc.toString() === 'tstat') {
                                                                        devType = 'Thermostat Device';
                                                                    } else if (devDesc.toString() === 'protect') {
                                                                        devType = 'Protect Device';
                                                                    } else if (devDesc.toString() === 'camera') {
                                                                        devType = 'Camera Device';
                                                                    } else if (devDesc.toString() === 'presence') {
                                                                        devType = 'Presence Device';
                                                                    } else if (devDesc.toString() === 'weather') {
                                                                        devType = 'Weather Device';
                                                                    }
                                                                }
                                                                $('#loaderText1').text('Checking');
                                                                $('#loaderText2').text(devType);
                                                                makeRequest(devUpd1Url, 'GET', null, devId, devType)
                                                                    .catch(function(errResp4) {
                                                                        installError(errResp4, false);
                                                                        addResult(errResp4.appDesc + ' Update Issue', false);
                                                                    })
                                                                    .then(function(stResp4) {
                                                                        // console.log(stResp4);
                                                                        var respData = JSON.parse(stResp4.response);
                                                                        if (respData.hasDifference === true) {
                                                                            $('#loaderText1').text('Updating');
                                                                            $('#loaderText2').text(stResp4.appDesc);
                                                                            makeRequest(devUpd2Url, 'GET', null, stResp4.appId, stResp4.appDesc)
                                                                                .catch(function(errResp5) {
                                                                                    installError(errResp5, false);
                                                                                    addResult(errResp5.appDesc + ' Update Issue', false);
                                                                                })
                                                                                .then(function(stResp5) {
                                                                                    if (!JSON.parse(stResp5.response).errors.length) {
                                                                                        $('#loaderText1').text('Compiling');
                                                                                        $('#loaderText2').text(stResp5.appDesc);
                                                                                        // console.log("stResp5(" + stResp5.appId + "):", JSON.parse(stResp5.response));
                                                                                        makeRequest(devUpd3Url, 'GET', null, stResp5.appId, stResp5.appDesc)
                                                                                            .catch(function(errResp6) {
                                                                                                addResult(errResp6.appDesc + ' Update Issue', false);
                                                                                                installError(errResp6, false);
                                                                                            })
                                                                                            .then(function(stResp6) {
                                                                                                // console.log("stResp6(" + stResp6.appId + "):", JSON.parse(stResp6.response));
                                                                                                addResult(stResp6.appDesc + ' was Updated', true);
                                                                                                devsDone.push(stResp6.appDesc);
                                                                                                sessionStorage.setItem('devsDone', devsDone);
                                                                                                if (devsDone.length === Object.keys(devIds).length) {
                                                                                                    installComplete('Updates are Complete!<br/>Everything is Good!');
                                                                                                }
                                                                                            });
                                                                                    }
                                                                                });
                                                                        } else {
                                                                            addResult(stResp4.appDesc + ' is Up-to-Date', true);
                                                                            devsDone.push(stResp4.appDesc);
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
                                addResult(stResp1.appDesc + ' is Up-to-Date', true);
                                appsDone.push(stResp1.appDesc);
                                sessionStorage.setItem('appsDone', appsDone);
                                if (appsDone.length === Object.keys(appIds).length) {
                                    // installComplete('Updates are Complete!<br/>Everything is Good!');
                                    if (devsDone.length < Object.keys(devIds).length) {
                                        for (var i in devIds) {
                                            var devDesc = i.toString();
                                            var devId = devIds[i];
                                            var devType;
                                            // console.log('devDesc: '+devDesc)
                                            if (devDesc !== undefined) {
                                                if (devDesc.toString() === 'tstat') {
                                                    devType = 'Thermostat Device';
                                                } else if (devDesc.toString() === 'protect') {
                                                    devType = 'Protect Device';
                                                } else if (devDesc.toString() === 'camera') {
                                                    devType = 'Camera Device';
                                                } else if (devDesc.toString() === 'presence') {
                                                    devType = 'Presence Device';
                                                } else if (devDesc.toString() === 'weather') {
                                                    devType = 'Weather Device';
                                                }
                                            }
                                            $('#loaderText1').text('Checking');
                                            $('#loaderText2').text(devType);
                                            makeRequest(devUpd1Url, 'GET', null, devId, devType)
                                                .catch(function(errResp4) {
                                                    installError(errResp4, false);
                                                    addResult(errResp4.appDesc + ' Update Issue', false);
                                                })
                                                .then(function(stResp4) {
                                                    // console.log(stResp4);
                                                    var respData = JSON.parse(stResp4.response);
                                                    if (respData.hasDifference === true) {
                                                        $('#loaderText1').text('Updating');
                                                        $('#loaderText2').text(stResp4.appDesc);
                                                        makeRequest(devUpd2Url, 'GET', null, stResp4.appId, stResp4.appDesc)
                                                            .catch(function(errResp5) {
                                                                installError(errResp5, false);
                                                                addResult(errResp5.appDesc + ' Update Issue', false);
                                                            })
                                                            .then(function(stResp5) {
                                                                if (!JSON.parse(stResp5.response).errors.length) {
                                                                    $('#loaderText1').text('Compiling');
                                                                    $('#loaderText2').text(stResp5.appDesc);
                                                                    // console.log("stResp5(" + stResp5.appId + "):", JSON.parse(stResp5.response));
                                                                    makeRequest(devUpd3Url, 'GET', null, stResp5.appId, stResp5.appDesc)
                                                                        .catch(function(errResp6) {
                                                                            addResult(errResp6.appDesc + ' Update Issue', false);
                                                                            installError(errResp6, false);
                                                                        })
                                                                        .then(function(stResp6) {
                                                                            // console.log("stResp6(" + stResp6.appId + "):", JSON.parse(stResp6.response));
                                                                            addResult(stResp6.appDesc + ' was Updated', true);
                                                                            devsDone.push(stResp6.appDesc);
                                                                            sessionStorage.setItem('devsDone', devsDone);
                                                                            if (devsDone.length === Object.keys(devIds).length) {
                                                                                installComplete('Updates are Complete!<br/>Everything is Good!');
                                                                            }
                                                                        });
                                                                }
                                                            });
                                                    } else {
                                                        addResult(stResp4.appDesc + ' is Up-to-Date', true);
                                                        devsDone.push(stResp4.appDesc);
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
            installError(err);
        });
}

async function loaderFunc() {
    $('#results').text('Waiting for connection...');
    if (sessionStorage.refreshCount === undefined) {
        sessionStorage.refreshCount = '0';
    }
    sessionStorage.refreshCount = Number(sessionStorage.refreshCount) + 1;
    switch (functionType) {
        case 'updates':
            await stUpdates();
            break;
    }
}

window.onload = loaderFunc;