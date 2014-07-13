//  CSRF AJAX

window.location.hash = '';

jQuery(document).ajaxSend(function(event, xhr, settings) {
    function sameOrigin(url) {
        // url could be relative or scheme relative or absolute
        var host = document.location.host; // host + port
        var protocol = document.location.protocol;
        var sr_origin = '//' + host;
        var origin = protocol + sr_origin;
        // Allow absolute or scheme relative URLs to same origin
        return (url == origin || url.slice(0, origin.length + 1) == origin + '/') ||
            (url == sr_origin || url.slice(0, sr_origin.length + 1) == sr_origin + '/') ||
            // or any other URL that isn't scheme relative or absolute i.e relative.
            !(/^(\/\/|http:|https:).*/.test(url));
    }
    function safeMethod(method) {
        return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
    }
    if (!safeMethod(settings.type) && sameOrigin(settings.url)) {
        var token = window._sharedData
          ? window._sharedData.config.csrf_token
          : window._csrf_token;
        xhr.setRequestHeader("X-CSRFToken", token);
    }
});

$.ajaxSetup({cache: true});

//  Image Fallback

function imageFallback(el) {
    var fallbackURL = "//instagram-static.s3.amazonaws.com/bluebar/images/default-avatar.png";

    if(el.parentNode.className.indexOf("img-") > -1 && el.parentNode.tagName.toLowerCase() == 'span')
    {
        el.parentNode.setAttribute("style", el.parentNode.getAttribute("style").split(el.src).join(fallbackURL));
        el.src = fallbackURL;
    }
}

function openDropdown(e) {
    $(e.target)
        .parents('.has-dropdown')
        .toggleClass("dropdown-open")
        .children('a').toggleClass("link-active");
}

$(document).ready(function() {
    $(".has-dropdown > a").live('click', openDropdown);
    var $html = $('html');
    if ($.browser.msie) {
        $html.addClass('msie');
    } else if ($.browser.opera) {
        $html.addClass('opera');
    }
});
