define(['jquery', 'jqueryui', 'lodash'], function() {
    var dashboard_config = {

        startup: function() {
            dashboard_config.reloadWidgetLayout();

            $('#add-widget').change(function() {
                dashboard_config.resetWidgets();
                $('.add-widget').addClass('ninja');
                $('#add-' + $('#add-widget').val()).removeClass('ninja');
            });

            $('#city').autocomplete({
                source: function(request, response) {
                    $.ajax({
                        url: 'http://autocomplete.wunderground.com/aq',
                        dataType: "jsonp",
                        jsonp: 'cb',
                        data: {
                            query: request.term
                        },
                        success: function(data) {
                            response($.map(data.RESULTS, function(item) {
                                return {
                                    label: item.name,
                                    wunder_id: item.l
                                }
                            }));
                        }
                    });
                },
                minLength: 3,
                focus: function(event, ui){
                    $('#city').val(ui.item.label);
                    $(".ui-helper-hidden-accessible").hide();
                    return false;
                },
                select: function(event, ui) {
                    $('#city').val(ui.item.label);
                    $('#city').data('wunder_id', ui.item.wunder_id);
                    return false;
                },
                open: function() {
                },
                close: function() {
                },
                messages: {
                    noResults: '',
                    results: function() {}
                }
            });
        },

        reloadWidgetLayout: function() {
            dashboard_config.resetWidgets();

            $.post(
                anechoic_base_url + 'dashboard/layout',
                {},
                dashboard_config.renderWidgetLayout
            );
        },

        renderWidgetLayout: function(data) {
            var layout = $('#dashboard-layout');
            layout.empty();

            // create our columns
            var c1 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c2 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c3 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c4 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var unassigned = $('<div class="supercolumn cf"><div class="column unassigned cf">Unassigned Widgets:</div></div>').appendTo(layout).find('.column');

            c1.sortable({
                connectWith: ".column",
                update: function(e, ui) {
                    dashboard_config.saveLayout();
                }
            });
            c2.sortable({
                connectWith: ".column",
                receive: function(e, ui) {
                    dashboard_config.saveLayout();
                }
            });
            c3.sortable({
                connectWith: ".column",
                receive: function(e, ui) {
                    dashboard_config.saveLayout();
                }
            });
            c4.sortable({
                connectWith: ".column",
                receive: function(e, ui) {
                    dashboard_config.saveLayout();
                }
            });
            unassigned.sortable({
                connectWith: ".column",
                receive: function(e, ui) {
                    dashboard_config.saveLayout();
                }
            });

            // draw the widgets
            for (var i = 0; i < data.length; i++) {
                switch(data[i].column) {
                    case 0:
                        dashboard_config.renderWidget(c1, data[i]);
                        break;
                    case 1:
                        dashboard_config.renderWidget(c2, data[i]);
                        break;
                    case 2:
                        dashboard_config.renderWidget(c3, data[i]);
                        break;
                    case 3:
                        dashboard_config.renderWidget(c4, data[i]);
                        break;
                    default:
                        dashboard_config.renderWidget(unassigned, data[i]);
                }
            }
            $('#spinner').addClass('ninja');
        },

        renderWidget: function(parent, widget) {
            var w = $('<div class="widget draggable ' + widget.kind + '" data-id="' + widget.id + '" id="widget_' + widget.id + '"></div>').appendTo(parent);

            var template = _.template($('#' + widget.kind + '-template').html());
            var html = template(widget);
            w.html(html);

            $('#remove_' + widget.id).click(function() {
                $('#spinner').removeClass('ninja');
                $.post(
                    anechoic_base_url + 'dashboard/delete',
                    {
                        id: widget.id,
                        sig: widget.properties.delsig
                    }
                )
                    .done(function() {
                        $('#widget_' + widget.id).remove();
                        $('#spinner').addClass('ninja');
                    })
                    .fail(function(){alert("fail");})
                return false;
            })
        },

        saveLayout: function() {
            $('#spinner').removeClass('ninja');

            var columns = $('.column');

            var payload = [];

            // iterate over columns to get their widgets
            // don't process the last column: it's always the unassigned one
            for (var i = 0; i < columns.length - 1; i++) {
                var widgets = $(columns[i]).find('.widget');
                for (var j = 0; j < widgets.length; j++) {
                    var location = {};
                    location['widget'] = $(widgets[j]).data('id');
                    location['column'] = i;
                    location['position'] = j;
                    payload.push(location);
                }
            }

            $.ajax({
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                url: anechoic_base_url + 'dashboard/save',
                data: JSON.stringify({"widgets": payload})
            })
                .done(function() {
                    $('#spinner').addClass('ninja');
                })
                .fail(function(jqXHR, textStatus, errorThrown){alert(errorThrown);})
        },

        addFeed: function() {
            $('#spinner').removeClass('ninja');
            $.post(
                anechoic_base_url + 'dashboard/addfeed',
                {
                    url: $("#newfeed [name='url']").val(),
                    max: $("#newfeed [name='max']").val()
                }
            )
                .done(dashboard_config.reloadWidgetLayout)
                .fail(function(){alert("fail");})
        },

        addWeather: function() {
            $('#spinner').removeClass('ninja');
            $.post(
                anechoic_base_url + 'dashboard/addweather',
                {
                    city: $("#newweather [name='city']").val(),
                    wunder_id: $("#newweather [name='city']").data('wunder_id')
                }
            )
                .done(dashboard_config.reloadWidgetLayout)
                .fail(function(){alert("fail");})
        },

        resetWidgets: function() {
            $("#newfeed [name='url']").val('');
            $("#newfeed [name='max']").val(10);
            $("#newweather [name='city']").val('');
        }
    }

    return dashboard_config;
});
