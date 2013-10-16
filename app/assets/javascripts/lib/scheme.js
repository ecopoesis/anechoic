define(['lib/cookie'], function(cookie) {
    var scheme = {
        reloadFuncs: Array(),

        initialize: function() {
            $('#scheme').click(scheme.change);
        },

        // figure out what scheme is set
        getScheme: function() {
            var parts = $('#css').attr('href').split('/');
            return parts[parts.length - 1].split('.')[0];
        },

        change: function() {
            var sig = $('#scheme').data('sig');

            var oldscheme = scheme.getScheme();

            var newscheme = 'dark';
            if (oldscheme === 'dark') {
                newscheme = 'light';
            }

            $('#css').attr('href', $('#css').attr('href').replace(oldscheme, newscheme));

            cookie.bake('scheme', newscheme);

            if (sig !== null && sig != undefined) {
                $.get(anechoic_base_url + 'scheme' + '?sig=' + sig + "&scheme=" + newscheme);
            }

            for (var i = 0; i < scheme.reloadFuncs.length; i++) {
                var func = scheme.reloadFuncs[i];
                func.fn(func.args);
            }

            return false;
        },

        // these colors are duped in the themes
        colors: function() {
            switch (scheme.getScheme()) {
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