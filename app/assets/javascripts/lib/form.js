define([], function() {
    var form = {
        initialize: function() {
            $('input').keydown(function(e) {
                if (e.keyCode == 13) {
                    $(this).closest('form').submit();
                }
            });
        }
    }

    return form;
});