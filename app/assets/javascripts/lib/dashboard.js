define(['lib/utils', 'lib/scheme', 'jquery', 'jqueryui', 'lodash', 'flot', 'flot_resize', 'flot_time'], function(utils, scheme) {
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
                        dashboard.draw({baseUrl: anechoic_base_url}, div, widget, true);
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
                    dashboard.draw(data, w, widget, true);
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
                    dashboard.draw(data, w, widget, true);
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
            scheme.reloadFuncs.push({
                fn: dashboard.plotStock,
                args: {
                    data: data,
                    div: div,
                    widget: widget,
                    initial: false
                }
            });

            dashboard.plotStock({
                data: data,
                div: div,
                widget: widget,
                initial: true
            });
        },

        plotStock: function(args) {
            dashboard.draw(args.data, args.div, args.widget, args.initial);
            var colors = scheme.colors();

            var current = {
                data: new Array(),
                lines: {
                    show: true,
                    fill: true
                },
                shadowSize: 0,
                color: colors.detail
            };

            var close = {
                data: new Array(),
                lines: {
                    show: true,
                    fill: false
                },
                shadowSize: 0,
                color: colors.detail_text
            };

            var y_min = args.data.ticks[0].close;
            var y_max = args.data.ticks[0].close;
            for(var i = 0; i < args.data.ticks.length; i++) {
                current.data.push([args.data.ticks[i].timestamp * 1000, args.data.ticks[i].close]);
                if (args.data.ticks[i].close < y_min) {
                    y_min = args.data.ticks[i].close;
                }
                if (args.data.ticks[i].close > y_max) {
                    y_max = args.data.ticks[i].close;
                }
            }

            close.data.push([args.data.min * 1000, args.data.previousClose]);
            close.data.push([args.data.max * 1000, args.data.previousClose]);

            if (args.data.previousClose < y_min) {
                y_min = args.data.previousClose;
            }

            if (args.data.previousClose > y_max) {
                y_max = args.data.previousClose;
            }

            $.plot($('#plot_' + args.widget.id), [current, close], {
                grid: {
                    show: true,
                    borderWidth: 0
                },
                xaxis: {
                    min: args.data.min * 1000,
                    max: args.data.max * 1000,
                    mode: "time",
                    timezone: "browser",
                    color: colors.button,
                    tickColor: 'rgba(0,0,0,0)'
                },
                yaxis: {
                    min: y_min - ((y_max - y_min) * 0.1),
                    max: y_max + ((y_max - y_min) * 0.1),
                    color: colors.button,
                    tickColor: 'rgba(0,0,0,0)'
                },
                series: {
                    lines: {
                        show: true,
                        fill: true,
                        fillColor: colors.detail
                    }
                }
            });
        },

        draw: function(data, div, widget, initial) {
            var template = _.template($('#' + widget.kind + '-template').html());
            var html = template({data: data, properties: widget.properties, utils: utils, widget: widget});
            div.html(html);

            if (initial) {
                dashboard.updateProgress();
            }
        }
    }
    
    return dashboard;
});