define(['lib/cookie'], function(cookie) {
    var scheme = {
        initialize: function() {
            $('#scheme').click(scheme.change);
        },

        change: function() {
            var sig = $('#scheme').data('sig');

            $('body').toggleClass('dark light');

            cookie.bake('scheme', $('body').attr('class'));

            if (sig !== null && sig != undefined) {
                $.get(anechoic_base_url + 'scheme' + '?sig=' + sig + "&scheme=" + $('body').attr('class'));
            }

            return false;
        }
    }

    return scheme;
});