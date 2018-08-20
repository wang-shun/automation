function diffPieChartshow(chartid , chartdata , linkData ) {

	// Define DOM element, chart data and then the flot chart init
	var topProgrammingLanguages = $(chartid);
	var programmingLanguageData = chartdata;
        //[{"color": "#005CDE", "data": 30, "label": "JavaScript"}, {"color": "#00A36A", "data": 20, "label": "Java"}, {"color": "#7D0096", "data": 20, "label": "PHP"}, {"color": "#992B00", "data": 15, "label": "C#"}, {"color": "#DE000F", "data": 15, "label": "Python"}];
	
	$.plot(topProgrammingLanguages, programmingLanguageData, {
		series: {
			pie: { 
				radius: 120,
				show: true
			}
		},
		legend: {
        	show: false,
			labelFormatter: function(label, series) {
            	var number = series.data[0][1];
				return(
                "<div style='padding-right:6px; line-height:18px;'>" +
                //"<span style=font-size:12px;'>"
                //+ number +
                //"</span>" +
                    //todo show data info.
                "<a style='font-size:12px;' href='#'>"
                + label + "</a>" +
                "</div>");
        	}
    	},
		grid: {
            hoverable: true,
            clickable: true
        }
	});

	// Tooltip attributes
	function showTooltip(x, y, contents) {
		$(".pie-chart-tooltip").remove();
    		$('<div class="pie-chart-tooltip">' + contents + '</div>').css({
        		position: 'absolute',
        		top: y - 45,
        		left: x + 15,
        		border: '2px solid #358FAA',
        		padding: '5px 10px',     
        		size: '10',   
        		'background-color': '#fff',
        		opacity: 1
    		}).appendTo("body").fadeIn(200);
	}

	// Show tooltip on pie slice hover, unless cursor not over a slice and then hide the tooltip
	topProgrammingLanguages.bind("plothover", function(event, pos, obj) {
		if (!obj) {
			$(".pie-chart-tooltip").remove();
		}
        else {
            showTooltip(pos.pageX, pos.pageY, obj.series.label + " " + obj.series.data[0][1] + "条数据");
        }
	});
	
	// Apply a URL to the links on each legend item, use data like programmingLanguageData[i].label
	$("#chart div.legend table tbody tr .legendLabel div a").each(function(i) {
		$(this).attr("href", "#");
	});

	// Bind a URL to each pie slice click. Can use obj variables like obj.series.label to generate different URLs for each pie slice.
	topProgrammingLanguages.bind("plotclick", function(event, pos, obj) {
        var data = obj.series;
        var pieLabel = data.label;
        var pieColor = data.color;
        var flag;
        //todo
        //var percent = data.percent.toFixed(0);
        var jump = true;
        var url = "/getPieChartDataDetail?srcVersion="+linkData.srcVersion
            +"&dictVersion="+linkData.dictVersion
            +"&tOffsetDown="  +linkData.downTotal
            +"&tOffsetUp=" +linkData.upTotal
            +"&fpOffsetDown=" +linkData.fpdownTotal
            +"&fpOffsetUp=" +linkData.fpupTotal
            +"&pageNumber=1"
            +"&pageSize=36" ;
        if(pieColor == "#005CDE"){
            flag = "tnormal";
        }else if(pieColor == "#00A36A"){
            flag = "tdown";
        }else if(pieColor == "#7D0096"){
            flag = "tup";
        }else if(pieColor == "#41d441"){
            flag = "fpnormal";
        }else if(pieColor == "#c61331"){
            flag = "fpdown";
        }else if(pieColor == "#fa9f1b"){
            flag = "fpup";
        }else{
            flag = "undefined";
            jump=false;
        }
        if(jump ==true){
            url=(url+"&flag="+flag);
            window.open(url);
            //window.location.href = url;
        }else{
            window.location.href = "";
        }

	});

};