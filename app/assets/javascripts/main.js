var Vote = {
    vote: function(type, id, direction, signature) {
        var item = type + '_' + id;
        $('#' + item + '_up').addClass('ninja');
        $.get('http://localhost:9000/' + type + '/vote/' + direction + '/' + id + '?sig=' + signature);
        return false;
    }
};