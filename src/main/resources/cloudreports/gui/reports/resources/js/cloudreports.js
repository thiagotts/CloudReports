/* 
 * Copyright (c) 2010-2012 Thiago T. Sá
 * 
 * This file is part of CloudReports.
 *
 * CloudReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CloudReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For more information about your rights as a user of CloudReports,
 * refer to the LICENSE file or see <http://www.gnu.org/licenses/>.
 */

function toggleBackground(graphName) {
	var currentColor = $("#" + graphName).css('background-color');
	if( currentColor == 'white' || currentColor == 'rgb(255, 255, 255)' || currentColor == '#ffffff') {
		$("#" + graphName).css('background-color','transparent');
	}
	else {
		$("#" + graphName).css('background-color','#ffffff');
	}
}

function toggleDiv(divName, speed) {
	$("#" + divName).toggle(speed);

	var currentArrow = $("." + divName).attr('src');
	if( currentArrow == '../images/up.png') {
		$("." + divName).attr('src', '../images/down.png');	
	}
	else {
		$("." + divName).attr('src', '../images/up.png');
	}
}


function changeOption(newOption,previousOption) {
	$("#" + previousOption).toggle();
	$("#" + newOption).toggle();
}

function drawMultiGraph(graphId, datasets) {
    // Hard-code color indices to prevent them from shifting as
    // series are turned on/off
    var i = 0;
    $.each(datasets, function(key, val) {
        val.color = i;
        ++i;
    });
    
    // Insert checkboxes 
    var choiceContainer = $("#checkboxes_" + graphId);
    $.each(datasets, function(key, val) {
        choiceContainer.append('<input type="checkbox" name="' + key +
                               '" checked="checked" id="id' + key + '">' +
                               '<label for="id' + key + '">'
                                + val.label + '</label>');
    });
    choiceContainer.find("input").click(plotAccordingToChoices);
 
    plotAccordingToChoices(graphId, datasets, choiceContainer);	
}

function plotAccordingToChoices(graphId, datasets, choiceContainer) {
	var data = [];
	var placeholder = $("#" + graphId);
	var options = {
	 series: {
	     lines: { show: true }
	 },
         zoom: {
             interactive: true
         },
         pan: {
             interactive: true
         }
        };
 
        choiceContainer.find("input:checked").each(function () {
            var key = $(this).attr("name");
            if (key && datasets[key])
                data.push(datasets[key]);
        });
 
        if (data.length > 0)
            $.plot(placeholder, data, options);
}
