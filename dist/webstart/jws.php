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
    foreach (scandir(getcwd()) as $file) {
        if (@preg_match("/\.jar$/",file)) {
            $files[] = $file;
        }
    }

    foreach($files[] as $jar) {
        echo "\t\t<jar href=\"$dir/$file\" />\n";
    }
	
?>
	</resources>
	
	<application-desc main-class="net.rptools.maptool.client.MapTool" />
</jnlp>
