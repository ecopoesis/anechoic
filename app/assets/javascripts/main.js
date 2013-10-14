require.config({
    paths : {
        flot: 'flot-0.8.1/jquery.flot',
        flot_resize: 'flot-0.8.1/jquery.flot.resize',
        flot_time: 'flot-0.8.1/jquery.flot.time',
        jquery: 'jquery-1.9.1.min',
        jqueryui: 'jquery-ui-1.10.3.custom.min',
        lodash: 'lodash-1.3.1.min',
        nprogress: 'nprogress-0.1.2',
        pidcrypt: 'pidcrypt/pidcrypt',
        pidcrypt_asn1: 'pidcrypt/asn1',
        pidcrypt_jsbn: 'pidcrypt/jsbn',
        pidcrypt_prng4: 'pidcrypt/prng4',
        pidcrypt_rng: 'pidcrypt/rng',
        pidcrypt_rsa: 'pidcrypt/rsa',
        pidcrypt_util: 'pidcrypt/pidcrypt_util',
        timezone: 'timezone'
     },

    shim: {
        flot: {
            deps: ['jquery']
        },
        flot_resize: {
            deps: ['flot']
        },
        flot_time: {
            deps: ['timezone', 'flot']
        },
        jqueryui: {
            exports: '$',
            deps: ['jquery']
        },
        lodash: {
            exports: '_'
        },
        nprogress: {
            deps: ['jqueryui']
        },
        pidcrypt_asn1: {
            deps: ['pidcrypt', 'pidcrypt_util']
        },
        pidcrypt_rsa: {
            deps: ['pidcrypt', 'pidcrypt_jsbn', 'pidcrypt_prng4', 'pidcrypt_rng', 'pidcrypt_util']
        },
        timezone: {
            exports: 'timezoneJS',
            deps: ['jquery']
        }
    }
});


// list all JS libs here so they get combined into one file
require([
    'lib/cookie',
    'lib/dashboard',
    'lib/dashboard-config',
    'lib/encryption',
    'lib/form',
    'lib/scheme',
    'lib/utils',
    'lib/vote',

    // outside libs
    'flot',
    'flot_resize',
    'flot_time',
    'jquery',
    'jqueryui',
    'lodash',
    'nprogress',
    'pidcrypt',
    'pidcrypt_asn1',
    'pidcrypt_jsbn',
    'pidcrypt_prng4',
    'pidcrypt_rng',
    'pidcrypt_rsa',
    'pidcrypt_util',
    'timezone'
], function(
    cookie,
    dashboard,
    dashboard_config,
    encryption,
    form,
    scheme,
    utils,
    vote
) {
    $(document).ready(function() {
        // common functions fo the whole world
        form.initialize();
        scheme.initialize();

        // timezoneJs
        timezoneJS.timezone.zoneFileBasePath = "/assets/javascripts/tz";
        timezoneJS.timezone.defaultZoneFile = [];
        timezoneJS.timezone.init({async: false});

        // startup individual pages
        switch (page) {
            case 'dashboard':
                dashboard.load();
                break;
            case 'dashboard-config':
                $(document).ready(function() { dashboard_config.startup(); });
                $('#addfeed').click(dashboard_config.addFeed);
                $('#addmail').click(dashboard_config.addMail);
                $('#addstock').click(dashboard_config.addStock);
                $('#addweather').click(dashboard_config.addWeather);
                break;
            case 'login':
                $('#login').submit(function() { encryption.encrypt($('#password').val()) });
                break;
            case 'singleSignUp':
                $('#signup').submit(function() { encryption.encrypt($('#password_password1').val()) });
                break;
        }
    });

});
