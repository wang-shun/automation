/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var alreadyLogin = isLogin();
$(document).ready(function(){
    if(window.parent.frames.length==0)
    {
        $("#homeA").attr('href','/home');
    }else {
        $("#homeA").attr('href','/');
    }

    $('#nav-tabs-ul li').bind("click", function(){
        removeActive();
        $(this).addClass('active');
    });
    //autoLogin();

    activeNavTab();
});

function removeActive() {
    $('#nav-tabs-ul li').each(function(){
        $(this).removeClass('active');
    });
}

//点击导航栏上的Login按钮，调用Cookie登录
function cookieLogin() {
    //clearCookie();

    var username = $.cookie("gtp-login-username");
    var password = $.cookie("gtp-login-password");

    //alert(username + "|" + password);
    if (username !== 'null' && password !== 'null' && username != null && password !== null) {
        $.ajax({
            url: '/login/cookie',
            type: 'GET',
            async: false,
            success: function(data){
                var goUrl='';
                if(data) {
                    var currentPath = window.location.href;
                    currentPath = currentPath.split("/").pop();
                    if (currentPath == "signup" || currentPath == "logout" || currentPath == "login") {
                        goUrl="/";
                    } else {
                        goUrl=currentPath;
                    }
                } else {
                    goUrl = '/login';
                }

                if(window.parent.frames.length==0)
                {
                    window.location.href=goUrl;
                }else
                {
                  window.parent.frames["main"].location.href=goUrl;
                }
            }
        });
    } else {
        var goUrl = '/login';
        if(window.parent.frames.length==0)
        {
            window.location.href=goUrl;
        }else
        {
            window.parent.frames["main"].location.href=goUrl;
        }
    }
}

function autoLogin () {
    var lastPath = document.referrer;
    lastPath = lastPath.split("/").pop();

    if (alreadyLogin === false && lastPath !== "/login" && lastPath !== "/logout") {
        cookieLogin();
    }

}

function clearCookie () {
    $.cookie("gtp-login-username",'',{expires:-1});
    $.cookie("gtp-login-password",'',{expires:-1});
}
function intervalActiveLogin() {
    var count = 0;
    var loginActiveInterval = setInterval(function(){
        $('#nav_login_li').children('a').toggleClass('nav-login-a');
        $('#nav_login_li').toggleClass('active');
        count++;
        if (count > 3) {
            clearInterval(loginActiveInterval);
        }
    },200);
}

function activeNavTab() {
    var localPath = window.location.href;
    var tabPath = localPath.split("/")[3];
    $('#frame_navbar_collapse li.nav-tab').removeClass('active');
    switch(tabPath) {
        case "home":
            $('#navbar-tab-home').addClass('active');
            break;
        case "task":
            $('#navbar-tab-task').addClass('active');
            break;
        case "branch":
            $('#navbar-tab-branch').addClass('active');
            break;
        case "regularRule":
            $('#navbar-tab-regular').addClass('active');
            break;
        case "machine":
            $('#navbar-tab-machine').addClass('active');
            break;
        case "hosts":
            $('#navbar-tab-host').addClass('active');
            break;
        case "gtp-report":
            if(localPath.split("/").length>4)
                tabPath=localPath.split("/")[4];

            $(tabPath=="gtp-report"?'#navbar-tab-report':'#navbar-tab-report-jmt').addClass('active');
            break;
        case "testResult":
            $('#navbar-tab-task').addClass('active');
            break;
        case "":
            $('#navbar-tab-home').addClass('active');
            break;

        case "gtp-report":
            $('#navbar-tab-report').addClass('active');
            break;

        default :
    }
}