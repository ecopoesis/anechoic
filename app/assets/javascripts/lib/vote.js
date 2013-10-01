define([], function() {
    var vote = {
        vote: function(type, id, direction, signature) {
            var item = type + '_' + id;
            $('#' + item + '_up').addClass('ninja');
            $('#' + item + '_down').addClass('ninja');
            $.get(anechoic_base_url + type + '/vote/' + direction + '/' + id + '?sig=' + signature);
            return false;
        }
    }

    return vote;
});