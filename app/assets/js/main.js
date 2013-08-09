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
            if (widget.type === "rss") {
                Anechoic.Dashboard.buildRss(c, widget);
            }
        }
    },

    buildRss: function(c, widget) {
        var w = $('<div class="widget rss"></div>').appendTo(c);
        var l = $('<ul></ul>').appendTo(w);

        $.get(widget.url, function(data) {
            var $xml = $(data);
            $xml.find("item").each(function() {
                var $this = $(this),
                    item = {
                        title: $this.find("title").text(),
                        link: $this.find("link").text(),
                        description: $this.find("description").text(),
                        pubDate: $this.find("pubDate").text(),
                        author: $this.find("author").text()
                    }
                $('<li><a href="' + item.link + '">' + item.title + '</a></li>').appendTo(l);
            });
        });
    }
}

$(document).ready(Anechoic.Form.submitOnEnter);