define(['lib/utils', 'jquery', 'jqueryui', 'lodash'], function(utils) {
    var dashboard = {
        searchProvider: 'google',
        count: 0,
        inc: 0,

        load: function() {
            NProgress.configure({
                showSpinner: false,
                trickle: false
            });

            NProgress.start();
            $.post(
                anechoic_base_url + 'dashboard/layout',
                {},
                function(data) {
                    dashboard.render(data);
                }
            );

            $('#search').keypress(function(e){
                // enter key
                if (e.which == 13) {
                    dashboard.search();
                }
            });
        },

        search: function() {
            var url = encodeURI($('#search').val());
            switch (dashboard.searchProvider) {
                case 'bing':
                    url = 'http://www.bing.com/search?q=' + url;
                default:
                    url = 'http://www.google.com/search?q=' + url;
            }
            window.open(url, '_blank');
            return false;
        },

        render: function(widgets) {
            var columns = {};
            var num_columns = 0;

            // build the columns and stick them in a map
            for (var i = 0; i < widgets.length; i++) {
                if (typeof widgets[i].column !== 'undefined') {
                    dashboard.count += 1;
                    if (!(widgets[i].column in columns)) {
                        columns[widgets[i].column] = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo('#dashboard').find('.column');
                        num_columns = num_columns + 1;
                    }
                }
            }

            dashboard.inc = 1 / dashboard.count;

            // add the size to columns
            $('#dashboard').find('.supercolumn').addClass('layout_' + num_columns + '_column');

            // build the widgets
            for (var i = 0; i < widgets.length; i++) {
                var widget = widgets[i];
                switch (widget.kind) {
                    case "feed":
                        dashboard.loadFeed(columns[widget.column], widget);
                        break;
                    case "weather":
                        dashboard.loadWeather(columns[widget.column], widget);
                        break;
                    case "welcome":
                        dashboard.renderWelcome(columns[widget.column], widget);
                        break;
                }
            }
        },

        loadFeed: function(c, widget) {
            var w = $('<div class="widget feed"></div>').appendTo(c);

            $.post(
                anechoic_base_url + 'dashboard/feed',
                {url: widget.properties.url, sig: widget.properties.sig},
                function(data) {
                    dashboard.renderFeed(data, w, widget.properties.max);
                }
            );
        },

        loadWeather: function(c, widget) {
            var w = $('<div class="widget weather"></div>').appendTo(c);

            $.post(
                anechoic_base_url + 'dashboard/weather',
                {url: widget.properties.wunderId, sig: widget.properties.sig},
                function(data) {
                    dashboard.renderWeather(data, w);
                }
            );
        },

        updateProgress: function() {
            NProgress.inc(dashboard.inc);
            dashboard.count = dashboard.count - 1;
            if (dashboard.count == 0) {
                NProgress.done();
            }
        },

        renderFeed: function(data, w, max) {
            var template = _.template($("#feed-template").html());
            var html = template({data: data, max: max});
            w.html(html);
            dashboard.updateProgress();
        },

        renderWeather: function(data, w) {
            var template = _.template($("#weather-template").html());
            var icon = _.template($("#weather-icon-template").html());
            var html = template({data: data, icon: icon, utils: utils});
            w.html(html);
            dashboard.updateProgress();
        },

        renderWelcome: function(c, widget) {
            var w = $('<div class="widget welcome"></div>').appendTo(c);
            var template = _.template($("#welcome-template").html());
            var html = template({baseUrl: anechoic_base_url});
            w.html(html);
            dashboard.updateProgress();
        }
    }
    
    return dashboard;
});