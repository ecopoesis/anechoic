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

$(document).ready(Anechoic.Form.submitOnEnter);