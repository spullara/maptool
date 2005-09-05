<?php
	if ( ! $_REQUEST['debug'] ) {
	    header("Content-type: application/x-java-jnlp-file");
	} else { 
	    header("Content-type: text/plain");             # Debugging header
    }
	$bits = pathinfo( $_SERVER['REQUEST_URI'] );
	$base = sprintf("http://%s/",  $_SERVER['SERVER_NAME']);
	$self = sprintf("http://%s%s", $_SERVER['SERVER_NAME'],$bits['dirname']);
	echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
?>
	<jnlp
    spec="1.5+" codebase="<? echo $self ?>">
	<information>
		<title>RPTools MapTool</title>
		<vendor><? echo $base ?></vendor>
		<offline-allowed/>
	</information>
	
	<security>
		<all-permissions />
	</security>
	
	<resources>
		<j2se version="1.5+" java-vm-args="-Xms64m -Xmx128m"/>
<?php
    $files = array();
    if (($dh = @opendir(getcwd())) != FALSE) {
        while (($file = readdir($dh)) !== false) {
            if (@preg_match("/\.jar$/i",$file) && !@preg_match("/^\./",$file)) {
                $files[] = $file;
            }
        }
        closedir($dh);
    }
   
    # find the main jarfile and move it to the top of the array.
    # we match the jarfile name to the directory 
    for($i=0;$i<count($files);$i++) {
        $basename = substr($files[$i],0,strpos($files[$i],"-"));
        if ( strpos(getcwd(),$basename) !== false ) {
#            print "$i : $files[$i] : $basename\n";
            $filename = $files[$i];
            unset($files[$i]);
            array_unshift($files,$filename);
        }
    }
        
    foreach($files as $jar) {
        echo "\t\t<jar href=\"$jar\" />\n";
    }
	
?>
	</resources>
	
	<application-desc main-class="net.rptools.maptool.client.MapTool" />
</jnlp>
