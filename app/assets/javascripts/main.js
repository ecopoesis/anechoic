var Vote = {
    vote: function(type, id, direction) {
        var item = type + '_' + id;
        $('#' + item + '_up').addClass('ninja');
        $.get('http://localhost:9000/' + type + '/vote/' + direction + '/' + id);
        return false;
    }
};