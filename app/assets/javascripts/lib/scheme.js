define(['lib/cookie'], function(cookie) {
    var scheme = {
        reloadFuncs: Array(),

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

            for (var i = 0; i < scheme.reloadFuncs.length; i++) {
                var func = scheme.reloadFuncs[i];
                func.fn(func.args);
            }

            return false;
        },

        // these colors are duped in global.less
        colors: function() {
            switch ($('body').attr('class')) {
                case 'light':
                    return {
                        background: '#FFF8E7',
                        foreground: '#16161D',
                        content: '#fff1cd',
                        button: '#ffe5a7',
                        detail: '#ffda81',
                        detail_text: '#ffbc1b',
                        alert: '#e19095',
                        link: '#1B48BB',
                        link_alt: '#cf413b'
                    }
                case 'dark':
                default:
                    return {
                        background: '#16161D',
                        foreground: '#FFF8E7',
                        content: '#2c2c3a',
                        button: '#424257',
                        detail: '#585874',
                        detail_text: '#6e6e91',
                        alert: '#640300',
                        link: '#6897BB',
                        link_alt: '#8c0300'
                    }
            }
        }
    }

    return scheme;
});