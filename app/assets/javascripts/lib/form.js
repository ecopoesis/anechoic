define([], function() {
    var form = {
        add_enter_to_submit: function(form) {
            form.find($('input')).keydown(function(e) {
                if (e.keyCode == 13) {
                    $(this).closest('form').submit();
                }
            });
        }
    }

    return form;
});