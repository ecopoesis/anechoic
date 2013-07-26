var Vote = {
    vote: function(type, id, direction, signature) {
        var item = type + '_' + id;
        $('#' + item + '_up').addClass('ninja');
        $('#' + item + '_down').addClass('ninja');
        $.get('http://localhost:9000/' + type + '/vote/' + direction + '/' + id + '?sig=' + signature);
        return false;
    }
};

var Scheme = {
    change: function() {
        $('body').toggleClass('dark light');

        Cookie.create('scheme', $('body').attr('class'));
        return false;
    }
}

var Cookie = {
    create: function(name, value, days) {
        if (days) {
            var date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            var expires = "; expires=" + date.toGMTString();
        } else var expires = "";
        document.cookie = escape(name) + "=" + escape(value) + expires + "; path=/";
    }
}

var Form = {
    submitOnEnter: function() {
        $('input').keydown(function(e) {
            if (e.keyCode == 13) {
                $(this).closest('form').submit();
            }
        });
    }
}

$(document).ready(Form.submitOnEnter);