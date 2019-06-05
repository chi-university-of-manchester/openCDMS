function selectStudy(selectid){
	var studyDdl = document.getElementById(selectid);
	var count = 0;
	var index = -1;
	for ( i=0; i<studyDdl.length; i++ ){
		if ( studyDdl.options[i].value.length > 0 ){
			count++;
			if ( index < 0 ) index=i;
		}
	}
	if ( 1 == count ){
		studyDdl.selectedIndex = index;
		studyDdl.onchange();
	}
}