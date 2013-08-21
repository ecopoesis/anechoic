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
            Anechoic.Dashboard.buildColumn(config.columns[i]);
        }
    },

    buildColumn: function(column) {
        var c = $('<div class="column"></div>').appendTo('#dashboard');
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

$(document).ready(Anechoic.Form.submitOnEnter);