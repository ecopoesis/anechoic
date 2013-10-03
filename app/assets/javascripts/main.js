require.config({
    paths : {
        timezone: 'timezone',
        flot: 'flot-0.8.1/jquery.flot',
        flot_resize: 'flot-0.8.1/jquery.flot.resize',
        flot_time: 'flot-0.8.1/jquery.flot.time',
        jquery: 'jquery-1.9.1.min',
        jqueryui: 'jquery-ui-1.10.3.custom.min',
        lodash: 'lodash-1.3.1.min',
        nprogress: 'nprogress-0.1.2'
     },

    shim: {
        "flot": {
            deps: ['jquery']
        },
        "flot_resize": {
            deps: ['flot']
        },
        "flot_time": {
            deps: ['timezone', 'flot']
        },
        "jqueryui": {
            exports: "$",
            deps: ['jquery']
        },
        "lodash": {
            exports: "_"
        },
        "nprogress": {
            deps: ['jqueryui']
        },
        "timezone": {
            exports: "timezoneJS",
            deps: ['jquery']
        }
    }
});


// list all JS libs here so they get combined into one file
require([
    'lib/cookie',
    'lib/dashboard',
    'lib/dashboard-config',
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
    'timezone'
], function(
    cookie,
    dashboard,
    dashboard_config,
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
        timezoneJS.timezone.zoneFileBasePath = "assets/javascripts/tz";
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
                $('#addstock').click(dashboard_config.addStock);
                $('#addweather').click(dashboard_config.addWeather);
                break;
        }
    });

});
