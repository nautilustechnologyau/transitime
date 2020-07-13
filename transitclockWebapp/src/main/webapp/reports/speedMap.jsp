<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@include file="/template/includes.jsp" %>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Speed Map</title>

    <!-- Load in Select2 files so can create fancy route selector -->
    <link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/css/select2.min.css" rel="stylesheet" />
    <script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0/js/select2.min.js"></script>

    <link href="params/reportParams.css" rel="stylesheet"/>
    <style>
        hr {
            height: 2px;
            background-color: darkgray;
            margin-right: 5px;
        }

        label {
            text-align: left;
            width: auto;
        }

        #paramsSidebar {
            width: 20%;
            margin-left: 10px;
            float:left;
            border-right: 1px solid black;
            border-bottom: 1px solid black;
        }

        .speedLegend {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 1px solid black;
            margin-right: 10px;
        }
    </style>
    <%--        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>--%>
    <link rel="stylesheet" type="text/css" href="../jquery.datepick.package-5.1.0/css/jquery.datepick.css">
    <script type="text/javascript" src="../jquery.datepick.package-5.1.0/js/jquery.plugin.js"></script>
    <script type="text/javascript" src="../jquery.datepick.package-5.1.0/js/jquery.datepick.js"></script>
</head>
<body>
<%@include file="/template/header.jsp" %>
<div id="paramsSidebar">
    <div id="title" style="text-align: left; font-size:x-large">
        Speed Map
    </div>

    <div id="paramsFields">
        <%-- For passing agency param to the report --%>
        <input type="hidden" name="a" value="<%= request.getParameter("a")%>">

        <jsp:include page="params/routeAllOrSingle.jsp" />

        <div class="param">
            <label for="direction">Direction</label>
            <select id="direction" name="direction" disabled="true">

            </select>
        </div>

        <script src="../javascript/jquery-timepicker/jquery.timepicker.min.js"></script>
        <link rel="stylesheet" type="text/css" href="../javascript/jquery-timepicker/jquery.timepicker.css"></link>

        <script>
            $(function() {
                var calendarIconTooltip = "Popup calendar to select date";

                $( "#beginDate" ).datepick({
                    dateFormat: "yy-mm-dd",
                    showOtherMonths: true,
                    selectOtherMonths: true,
                    // Show button for calendar
                    buttonImage: "img/calendar.gif",
                    buttonImageOnly: true,
                    showOn: "both",
                    // Don't allow going past current date
                    maxDate: 0,
                    // onClose is for restricting end date to be after start date,
                    // though it is potentially confusing to user
                    rangeSelect: true,
                    showTrigger: '<button type="button" class="trigger">' +
                        '<img src="../jquery.datepick.package-5.1.0/img/calendar.gif" alt="Popup"></button>',
                    onClose: function( selectedDate ) {
                        // Strangely need to set the title attribute for the icon again
                        // so that don't revert back to a "..." tooltip
                        // FIXME $(".ui-datepicker-trigger").attr("title", calendarIconTooltip);
                    }
                });

                // Use a better tooltip than the default "..." for the calendar icon
                $(".ui-datepicker-trigger").attr("title", calendarIconTooltip);

                $("#beginTime, #endTime").timepicker({timeFormat: "H:i"})
                    .on('change', function(evt) {
                        if (evt.originalEvent) { // manual change
                            // validate that this looks like HH:MM
                            if (!evt.target.value.match(/^(([0,1][0-9])|(2[0-3])):[0-5][0-9]$/))
                                evt.target.value = evt.target.oldval ? evt.target.oldval : "";
                        }
                        evt.target.oldval = evt.target.value;
                    });

            });
        </script>

        <div class="param">
            <label for="beginDate">Date:</label>
            <input type="text" id="beginDate" name="beginDate"
                   title="The range of dates that you want to examine data for.
                           <br><br> Begin date must be before the end date."
                   size="18"
                   value="Date range" />
        </div>

        <div class="param">
            <label for="beginTime">Begin Time:</label>
            <input id="beginTime" name="beginTime"
                   title="Optional begin time of day to limit query to. Useful if
                            want to see result just for rush hour, for example. Leave blank
                            if want data for entire day.
                            <br/><br/>Format: hh:mm, as in '07:00' for 7AM."
                   size="5"
                   value="" /> <span class="note">(hh:mm)</span>
        </div>

        <div class="param">
            <label for="endTime">End Time:</label>
            <input id="endTime" name="endTime"
                   title="Optional end time of day to limit query to. Useful if
                            want to see result just for rush hour, for example. Leave blank
                            if want data for entire day.
                            <br/><br/>Format: hh:mm, as in '09:00' for 9AM.
                            Use '23:59' for midnight."
                   size="5"
                   value="" /> <span class="note">(hh:mm)</span>
        </div>

        <div class="param">
            <select id="serviceDayType" name="serviceDayType">
                <option value="">Service Day Type</option>
                <option value="">All</option>
                <option value="weekday">Weekday</option>
                <option value="saturday">Saturday</option>
                <option value="sunday">Sunday</option>
            </select>
        </div>

        <hr>

        <div id="speedParams">
            <div class = "param">
                <div class="speedLegend" style="background-color: green;"></div>
                <input id='lowSpeed' name='lowSpeed' type="range" min="0" max="98" step="0.1" value="0" oninput="lowSpeedSlider(this.value)">
                <output id="lowSpeedOutput">mph max</output>
            </div>
            <div class="param">
                <div class="speedLegend" style="background-color: yellow;"></div>
                <input id='midSpeed' name='midSpeed' type="range" min="1" max="99" step="0.1" value="1" oninput="midSpeedSlider(this.value)">
                <output id="midSpeedOutput">mph max</output>
            </div>
            <div class="param">
                <div class="speedLegend" style="background-color: red;"></div>
                <input id='highSpeed' name='highSpeed' type="range" min="2" max="100" step="0.1" value="2" oninput="highSpeedSlider(this.value)">
                <output id="highSpeedOutput">mph max</output>
            </div>
        </div>
    </div>

    <input type="button" id="submit" value="Submit" style="margin-top: 10px; margin-bottom: 10px;">

</div>

<script>
    $("#route").attr("style", "width: 200px");

    $("#route").change(function() {
        var headsigns = ['a', 'b', 'c', '1', '2', '3'];
        $("#direction").removeAttr('disabled');
        $("#direction").empty();
        headsigns.forEach(function(headsign) {
            $("#direction").append("<option value='" + headsign + "'>" + headsign + "</option>")
        });
    })

    function lowSpeedSlider(value) {
        $("#lowSpeedOutput").val(value + " mph max");

        $("#midSpeed").attr("min", (parseFloat(value) + 1));
        $("#highSpeed").attr("min", (parseFloat(value) + 2));

        if ($("#midSpeed").val() <= (parseFloat(value) + 1)) {
            midSpeedSlider((parseFloat(value) + 1));
        }
    }

    function midSpeedSlider(value) {
        $("#midSpeedOutput").val(value + " mph max");

        $("#highSpeed").attr("min", (parseFloat(value) + 1));

        if ($("#highSpeed").val() <= (parseFloat(value) + 1)) {
            highSpeedSlider((parseFloat(value) + 1));
        }
    }

    function highSpeedSlider(value) {
        $("#highSpeedOutput").val(value + " mph max");
    }
</script>