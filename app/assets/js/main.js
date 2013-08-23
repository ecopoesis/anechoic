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
    load: function() {
        $.post(
            Anechoic.baseUrl + 'dashboard/layout',
            {},
            function(data) {
                Anechoic.Dashboard.render(data);
            }
        );
    },

    render: function(widgets) {
        var columns = {};
        var num_columns = 0;

        // build the columns and stick them in a map
        for (var i = 0; i < widgets.length; i++) {
            if (typeof widgets[i].column !== 'undefined' && !(widgets[i].column in columns)) {
                columns[widgets[i].column] = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo('#dashboard').find('.column');
                num_columns = num_columns + 1;
            }
        }

        // add the size to columns
        var foo = $('#dashboard').find('.supercolumn').addClass('layout_' + num_columns + '_column');

        // build the widgets
        for (var i = 0; i < widgets.length; i++) {
            var widget = widgets[i];
            if (widget.kind === "feed") {
                Anechoic.Dashboard.loadFeed(columns[widget.column], widget);
            }
        }
    },

    loadFeed: function(c, widget) {
        var w = $('<div class="widget feed"></div>').appendTo(c);

        $.post(
            Anechoic.baseUrl + 'feed',
            {url: widget.properties.url, sig: "foo"},
            function(data) {
                Anechoic.Dashboard.renderFeed(data, w, widget.properties.max);
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
        var c1 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
        var c2 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
        var c3 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
        var c4 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
        var unassigned = $('<div class="supercolumn cf"><div class="column unassigned cf">Unassigned Widgets:</div></div>').appendTo(layout).find('.column');

        c1.sortable({connectWith: ".column"});
        c2.sortable({connectWith: ".column"});
        c3.sortable({connectWith: ".column"});
        c4.sortable({connectWith: ".column"});
        unassigned.sortable({connectWith: ".column"});

        // draw the widgets
        for (var i = 0; i < data.length; i++) {
            switch(data[i].column) {
                case 0:
                    Anechoic.Dashboard.Config.renderWidget(c1, data[i]);
                    break;
                case 1:
                    Anechoic.Dashboard.Config.renderWidget(c2, data[i]);
                    break;
                case 2:
                    Anechoic.Dashboard.Config.renderWidget(c3, data[i]);
                    break;
                case 3:
                    Anechoic.Dashboard.Config.renderWidget(c4, data[i]);
                    break;
                default:
                    Anechoic.Dashboard.Config.renderWidget(unassigned, data[i]);
            }
        }
    },

    renderWidget: function(parent, widget) {
        var w = $('<div class="widget draggable" data-id="' + widget.id + '"></div>').appendTo(parent);
        $('<i class="icon-rss"></i>').appendTo(w);
        $('<div class="url">' + widget.properties.url + '</div>').appendTo(w);
    },

    saveLayout: function() {
        var columns = $('.column');

        var payload = [];

        // iterate over columns to get their widgets
        // don't process the last column: it's always the unassigned one
        for (var i = 0; i < columns.length - 1; i++) {
            var widgets = $(columns[i]).find('.widget');
            for (var j = 0; j < widgets.length; j++) {
                var location = {};
                location['widget'] = $(widgets[j]).data('id');
                location['column'] = i;
                location['position'] = j;
                payload.push(location);
            }
        }

        $.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            url: Anechoic.baseUrl + 'dashboard/save',
            data: JSON.stringify({"widgets": payload})
        })
        .done(Anechoic.Dashboard.Config.reloadWidgetLayout)
        .fail(function(jqXHR, textStatus, errorThrown){alert(errorThrown);})
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