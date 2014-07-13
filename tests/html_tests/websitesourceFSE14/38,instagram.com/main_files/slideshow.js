function next_slide() {
    var active = $('#iphone-overlay img.active');
    if (active.length == 0) active = $('#iphone-overlay img:last');
    var next = active.next().length ? active.next() : $('#iphone-overlay img:first');
    active.addClass('last-active');
    next.css({opacity: 0.0}).addClass('active').animate({opacity: 1.0}, 1500, function() {
        active.removeClass('active last-active');
    });
    setTimeout("next_slide()", 5000);
}

$(function() {
    setTimeout("next_slide()", 5000);
});

