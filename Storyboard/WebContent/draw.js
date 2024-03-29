var started = false;
var canvas, context;
var lastColor = 'black';
var lastBrush = 'brush1';
var lineWidth = 1;

var enableDraw = false; 

function init() {
	canvas = $('#imageView').get(0);
	context = canvas.getContext('2d');
	
	// Auto-adjust canvas size to fit window.
	canvas.width  = window.innerWidth - 25;
	canvas.height = window.innerHeight - 150;
	
	canvas.addEventListener('mousemove', onMouseMove, false);
	
	canvas.addEventListener('mousedown', function(e) { enableDraw = true; }, false);
	canvas.addEventListener('mouseup', function(e) { enableDraw = false; started = false; }, false); 
	
	canvas.addEventListener("touchstart", function(e) { e.preventDefault(); enableDraw = true; }, false);
	canvas.addEventListener("touchend", function(e) { enableDraw = false; started = false; }, false);
	canvas.addEventListener("touchmove", onTouchMove, true);
	
	// Add events for toolbar buttons.
	$('#red').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#pink').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#fuchsia').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#orange').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#yellow').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#lime').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#green').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#blue').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#purple').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#brown').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#black').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#white').get(0).addEventListener('click', function(e) { onColorClick(e.target.id); }, false);
	$('#brush1').get(0).addEventListener('click', function(e) { onBrushClick(e.target.id,1); }, false);
	$('#brush2').get(0).addEventListener('click', function(e) { onBrushClick(e.target.id,5); }, false);
	$('#brush3').get(0).addEventListener('click', function(e) { onBrushClick(e.target.id,10); }, false);
	$('#save').get(0).addEventListener('click', function(e) { onSave(); }, false);
	
	context.fillStyle = 'white';
	context.fillRect(0, 0, canvas.width, canvas.height);
}

function getOffset(event){
    var totalOffsetX = 0;
    var totalOffsetY = 0;
    var currentElement = canvas;

    do{
        totalOffsetX += currentElement.offsetLeft;
        totalOffsetY += currentElement.offsetTop;
    }
    while(currentElement = currentElement.offsetParent)

    return {x:totalOffsetX, y:totalOffsetY};
}
HTMLCanvasElement.prototype.getOffset = getOffset;

function onMouseMove(event) {
	canvas.onselectstart = function () { return false; };
	canvas.onmousedown = function () { return false; };
	
	var pos = canvas.getOffset(event);
	var x = event.pageX - pos.x;
	var y = event.pageY - pos.y;

	if (enableDraw) {
		if (!started) {
			started = true;
	
			context.beginPath();
			context.moveTo(x, y);		
		}
		else {
			context.lineTo(x, y);
			context.stroke();
		}
	}
}

function onTouchMove(event) {
	event.preventDefault();
	
	var pos = canvas.getOffset(event);
	var x = event.targetTouches[0].pageX - pos.x;
	var y = event.targetTouches[0].pageY - pos.y;
	
	if (enableDraw) {
		if (!started) {
			started = true;
	
			context.beginPath();
			context.moveTo(x, y);		
		}
		else {
			context.lineTo(x, y);
			context.stroke();
		}
	}
}

function onColorClick(color) {
	// Start a new path to begin drawing in a new color.
	context.closePath();
	context.beginPath();
	
	// Select the new color.
	context.strokeStyle = color;
	
	// Highlight selected color.
	var borderColor = 'white';
	if (color == 'white' || color == 'yellow') {
		borderColor = 'black';
	}
	
	$('#' + lastColor).css("border", "0px dashed white");
	$('#' + color).css("border", "1px dashed " + borderColor);
	
	// Store color so we can un-highlight it next time around.
	lastColor = color;
}

function onBrushClick(brush, size) {
	context.closePath();
	context.beginPath();
	
	context.lineWidth = size;
	
	$('#' + lastBrush).css("border", "0px dashed white");
	$('#' + brush).css("border", "1px dashed black");
	
	lastBrush = brush;
}

function onSave() {
	if(confirm("Are you sure?") == true) {
		var img = canvas.toDataURL("image/png");
		document.getElementById('drawing').value = img; 
		document.forms["submit"].submit();
	}
}