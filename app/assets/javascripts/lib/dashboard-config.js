define(['jquery', 'jqueryui', 'lodash'], function() {
    var dashboard_config = {
        blocks: 0,
        saving: false,

        startup: function() {
            dashboard_config.blockActions();
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

            dashboard_config.unblockActions();
        },

        reloadWidgetLayout: function() {
            dashboard_config.blockActions();

            dashboard_config.resetWidgets();

            $.post(
                anechoic_base_url + 'dashboard/layout',
                {},
                dashboard_config.renderWidgetLayout
            );

            dashboard_config.unblockActions();
        },

        renderWidgetLayout: function(data) {
            dashboard_config.blockActions();

            var layout = $('#dashboard-layout');
            layout.empty();

            // create our columns
            var c1 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c2 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c3 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var c4 = $('<div class="supercolumn cf"><div class="column cf">&nbsp;</div></div>').appendTo(layout).find('.column');
            var unassigned = $('<div class="supercolumn cf"><div class="column unassigned cf">Unassigned Widgets:</div></div>').appendTo(layout).find('.column');

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

            dashboard_config.unblockActions();
        },

        renderWidget: function(parent, widget) {
            dashboard_config.blockActions();

            var w = $('<div class="widget draggable ' + widget.kind + '" data-id="' + widget.id + '" id="widget_' + widget.id + '"></div>').appendTo(parent);

            var template = _.template($('#' + widget.kind + '-template').html());
            var html = template(widget);
            w.html(html);

            $('#remove_' + widget.id).click(function() {
                dashboard_config.blockActions();
                $.post(
                    anechoic_base_url + 'dashboard/delete',
                    {
                        id: widget.id,
                        sig: widget.properties.delsig
                    }
                )
                    .done(function() {
                        $('#widget_' + widget.id).remove();
                        dashboard_config.unblockActions();
                    })
                    .fail(function(){
                        alert("fail");
                        dashboard_config.unblockActions();
                    })
                return false;
            });

            dashboard_config.unblockActions();
        },

        saveLayout: function() {
            if (!dashboard_config.saving) {
                dashboard_config.saving = true;

                dashboard_config.blockActions();

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
                    .always(function() {
                        dashboard_config.unblockActions();
                        dashboard_config.saving = false;
                    })
                    .fail(function(jqXHR, textStatus, errorThrown){
                        alert(errorThrown);
                    })
            }
        },

        addFeed: function() {
            dashboard_config.blockActions();
            $.post(
                anechoic_base_url + 'dashboard/addfeed',
                {
                    url: $("#newfeed [name='url']").val(),
                    max: $("#newfeed [name='max']").val()
                }
            )
                .done(dashboard_config.reloadWidgetLayout)
                .fail(function(){alert("fail");})
                .always( dashboard_config.unblockActions)
        },

        addStock: function() {
            dashboard_config.blockActions();
            $.post(
                anechoic_base_url + 'dashboard/addstock',
                {
                    symbol: $("#newstock [name='symbol']").val(),
                    range: "1d"
                }
            )
                .done(dashboard_config.reloadWidgetLayout)
                .fail(function(){alert("fail");})
                .always(dashboard_config.unblockActions)
        },

        addWeather: function() {
            dashboard_config.blockActions();
            $.post(
                anechoic_base_url + 'dashboard/addweather',
                {
                    city: $("#newweather [name='city']").val(),
                    wunder_id: $("#newweather [name='city']").data('wunder_id')
                }
            )
                .done(dashboard_config.reloadWidgetLayout)
                .fail(function(){alert("fail");})
                .always(dashboard_config.unblockActions)
        },

        resetWidgets: function() {
            $("#newfeed [name='url']").val('');
            $("#newfeed [name='max']").val(10);
            $("#newstock [name='symbol']").val('');
            $("#newweather [name='city']").val('');
        },

        // blocks updates while we're saving stuff
        blockActions: function() {
            dashboard_config.blocks += 1;

            if (dashboard_config.blocks === 1) {
                $('#spinner').removeClass('ninja');
                $('.column').sortable({
                    connectWith: undefined,
                    update: function(e, ui) {
                        dashboard_config.saveLayout();
                    }
                });
            }
        },

        // allow actions to happen
        unblockActions: function() {
            dashboard_config.blocks -= 1;

            if (dashboard_config.blocks === 0) {
                $('#spinner').addClass('ninja');
                $('.column').sortable({
                    connectWith: ".column",
                    update: function(e, ui) {
                        dashboard_config.saveLayout();
                    }
                })
            }
        }
    }

    return dashboard_config;
});
