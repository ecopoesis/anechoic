define([], function() {
    var cookie = {
        bake: function(name, value, days) {
            if (days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                var expires = "; expires=" + date.toGMTString();
            } else var expires = "";
            document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
        }
    }

    return cookie;
});