/**
 * 
 */
function calTime(time){
	var str = time + "ms";
	if(time > 100){
		time = Math.round((time * 100) / 1000) / 100;
		str = time + "s";
		if(time > 60){
			time = Math.round((time * 100) / 60) / 100;
			str = time + "min";
		}
	}
	return str;
}