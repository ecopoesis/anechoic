require.config({
    paths : {
        jquery: 'jquery-1.9.1.min',
        jqueryui: 'jquery-ui-1.10.3.custom.min',
        lodash: 'lodash-1.3.1.min',
        nprogress: 'nprogress-0.1.2'
    },

    shim: {
        "jqueryui": {
            exports: "$",
            deps: ['jquery']
        },
        "nprogress": {
            deps: ['jqueryui']
        },
        "lodash": {
            exports: "_"
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
    'jquery',
    'jqueryui',
    'lodash',
    'nprogress'
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

        // startup individual pages
        switch (page) {
            case 'dashboard':
                dashboard.load();
                break;
            case 'dashboard-config':
                $(document).ready(function() { dashboard_config.startup(); });
                $('#addfeed').click(dashboard_config.addFeed);
                $('#addweather').click(dashboard_config.addWeather);
                break;
        }
    });

});
