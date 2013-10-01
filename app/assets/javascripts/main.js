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
    $(document).ready(function(){
        form.initialize();
        scheme.initialize();
    });

});