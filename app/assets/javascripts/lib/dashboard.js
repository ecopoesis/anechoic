define(['lib/utils', 'jquery', 'jqueryui', 'lodash', 'flot', 'flot_resize', 'flot_time'], function(utils) {
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
                    case "stock":
                        dashboard.loadStock(columns[widget.column], widget);
                        break;
                    case "weather":
                        dashboard.loadWeather(columns[widget.column], widget);
                        break;
                    case "welcome":
                        var div = $('<div class="widget welcome"></div>').appendTo(columns[widget.column]);
                        dashboard.draw({baseUrl: anechoic_base_url}, div, widget);
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
                    dashboard.draw(data, w, widget);
                }
            );
        },

        loadStock: function(c, widget) {
            var w = $('<div class="widget stock"></div>').appendTo(c);

            $.post(
                anechoic_base_url + 'dashboard/stock',
                {symbol: widget.properties.symbol, range: widget.properties.range, sig: widget.properties.sig},
                function(data) {
                    dashboard.drawStock(data, w, widget);
                }
            );
        },

        loadWeather: function(c, widget) {
            var w = $('<div class="widget weather"></div>').appendTo(c);

            $.post(
                anechoic_base_url + 'dashboard/weather',
                {url: widget.properties.wunderId, sig: widget.properties.sig},
                function(data) {
                    widget.properties.icon_template =  _.template($("#weather-icon-template").html());
                    dashboard.draw(data, w, widget);
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

        drawStock: function(data, div, widget) {
            dashboard.draw(data, div, widget);

            var flot_data = new Array();
            for(i = 0; i < data.ticks.length; i++) {
                flot_data.push([data.ticks[i].timestamp * 1000, data.ticks[i].close]);
            }
            var plot = $.plot($('#plot_' + widget.id), [flot_data], {
                xaxis: {
                    mode: "time",
                    timezone: "browser"
                }
           });
        },

        draw: function(data, div, widget) {
            var template = _.template($('#' + widget.kind + '-template').html());
            var html = template({data: data, properties: widget.properties, utils: utils, widget: widget});
            div.html(html);
            dashboard.updateProgress();
        }
    }
    
    return dashboard;
});