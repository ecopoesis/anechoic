define([], function() {
    var utils = {
        round_2: function(v) {
            return Math.round(v * 100) / 100;
        }
    }

    return utils;
});