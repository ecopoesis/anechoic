var Anechoic =  Anechoic || {};

Anechoic.Vote = {
    vote: function(type, id, direction, signature) {
        var item = type + '_' + id;
        $('#' + item + '_up').addClass('ninja');
        $('#' + item + '_down').addClass('ninja');
        $.get(Anechoic.baseUrl + type + '/vote/' + direction + '/' + id + '?sig=' + signature);
        return false;
    }
};

Anechoic.Scheme = {
    change: function(signature) {
        $('body').toggleClass('dark light');

        Anechoic.Cookie.bake('scheme', $('body').attr('class'));

        if (signature !== null) {
            $.get(Anechoic.baseUrl + 'user/scheme' + '?sig=' + signature + "&scheme=" + $('body').attr('class'));
        }

        return false;
    }
}

Anechoic.Cookie = {
    bake: function(name, value, days) {
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            var expires = "; expires=" + date.toGMTString();
        } else var expires = "";
        document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
    }
}

Anechoic.Form = {
    submitOnEnter: function() {
        $('input').keydown(function(e) {
            if (e.keyCode == 13) {
                $(this).closest('form').submit();
            }
        });
    }
}

Anechoic.Dashboard = {
    build: function(config) {
        for (var i = 0; i < config.columns.length; i++) {
            Anechoic.Dashboard.buildColumn(config.columns[i], config.columns.length);
        }
    },

    buildColumn: function(column, num_columns) {
        var c = $('<div class="column column' + num_columns + ' cf"></div>').appendTo('#dashboard');
        for (var i = 0; i < column.widgets.length; i++) {
            var widget = column.widgets[i];
            if (widget.type === "feed") {
                Anechoic.Dashboard.buildFeed(c, widget);
            }
        }
    },

    buildFeed: function(c, widget) {
        var w = $('<div class="widget feed"></div>').appendTo(c);

        $.post(
            Anechoic.baseUrl + 'feed',
            {url: widget.url, sig: "foo"},
            function(data) {
                Anechoic.Dashboard.renderFeed(data, w, widget.max);
            }
        );
    },

    renderFeed: function(data, w, max) {
        $('<h3>' + data.title  +'</h3>').appendTo(w);
        var l = $('<ul></ul>').appendTo(w);
        for (var i = 0; i < data.items.length && i < max; i++) {
            var item = data.items[i];
            $('<li><a href="' + item.link + '">' + item.title + '</a></li>').appendTo(l);
        }
    }
}

Anechoic.Dashboard.Config = {
    reloadWidgetLayout: function() {
        $.post(
            Anechoic.baseUrl + 'dashboard/layout',
            {},
            Anechoic.Dashboard.Config.renderWidgetLayout
        );
    },

    renderWidgetLayout: function(data) {
        var layout = $('#dashboard-layout');
        layout.empty();

        // create our columns
        var c1 = $('<div class="column cf">&nbsp;</div>').appendTo(layout);
        var c2 = $('<div class="column cf">&nbsp;</div>').appendTo(layout);
        var c3 = $('<div class="column cf">&nbsp;</div>').appendTo(layout);
        var c4 = $('<div class="column cf">&nbsp;</div>').appendTo(layout);
        var unassigned = $('<div class="column unassigned cf">&nbsp;</div>').appendTo(layout)

        c1.sortable({connectWith: ".column"});
        c2.sortable({connectWith: ".column"});
        c3.sortable({connectWith: ".column"});
        c4.sortable({connectWith: ".column"});
        unassigned.sortable({connectWith: ".column"});

        // draw the widgets
        for (var i = 0; i < data.length; i++) {
            switch(data[i].col) {
                case 1:
                    Anechoic.Dashboard.Config.renderWidget(c1, data[i]);
                    break;
                case 2:
                    Anechoic.Dashboard.Config.renderWidget(c2, data[i]);
                    break;
                case 3:
                    Anechoic.Dashboard.Config.renderWidget(c3, data[i]);
                    break;
                case 4:
                    Anechoic.Dashboard.Config.renderWidget(c4, data[i]);
                    break;
                default:
                    Anechoic.Dashboard.Config.renderWidget(unassigned, data[i]);
            }
        }
    },

    renderWidget: function(parent, widget) {
        var w = $('<div class="draggable"></div>').appendTo(parent);
        $('<i class="icon-rss"></i>').appendTo(w);
        $('<div class="url">' + widget.properties.url + '</div>').appendTo(w);
    },

    addFeed: function() {
        $.post(
            Anechoic.baseUrl + 'dashboard/addfeed',
            {
                url: $("#newfeed [name='url']").val(),
                max: $("#newfeed [name='max']").val()
            }
        )
        .done(Anechoic.Dashboard.Config.reloadWidgetLayout)
        .fail(function(){alert("fail");})
    }
}

$(document).ready(Anechoic.Form.submitOnEnter);