<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>
	<meta charset="utf-8">
	
	<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame
	   Remove this if you use the .htaccess -->
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	
	<title></title>
	<meta name="description" content="">
	<meta name="author" content="Philipp C. Adrian">
	
	<!-- Mobile viewport optimized: j.mp/bplateviewport -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<!-- Place favicon.ico and apple-touch-icon.png in the root directory: mathiasbynens.be/notes/touch-icons -->
	
	<!-- CSS: implied media="all" -->
	<link rel="stylesheet" href="css/all.css">

	<!-- All JavaScript at the bottom, except for Modernizr and Respond.
	   Modernizr enables HTML5 elements & feature detects; Respond is a polyfill for min/max-width CSS3 Media Queries -->
	<script src="js/libs/modernizr-1.7.min.js"></script>
	<script src='js/libs/respond.min.js'></script>
	<script src='js/plugins/processing-1.3.6.min.js'></script>
</head>

<body>
	<ul class="greenishSlides">
		<li class="mod navigation">
			<div class="inner">
				<div class="bd">
					<a href="/" class="header">
						<img src="img/ran.jpg" class="ran">
						<h1>RAN /<br>Chaos</h1>
					</a>
					<hr>
					<ul class="links">
						<li><h3>Experiments</h3></li>
						<li><a href="/?p=lorenzVisual">Lorenz 84 Visualization 1</a></li>
						<li><a href="/?p=lorenzDisplay">Lorenz 84 Visualization 2</a></li>
						<li><a href="/?p=lorenzSoundFinal">Sound Experiments</a></li>
						<li><h3>Final Experiment</h3></li>
						<li><a href="/?p=pcaRan">pcaRAN</a></li>
						<li><h3>Processing Library</h3></li>
						<li><a href="/?p=pcaCollisionDetection">pcaCollisionDetection</a></li>
						<li><h3>Source on Github</h3></li>
						<li><a href="https://github.com/greenish/HGK-Ran" target="_blank">HGK-Ran</a></li>
						<li><a href="https://github.com/greenish/pcaCollisionDetection" target="_blank">pcaCollisionDetection</a></li>
						<li><h3>TImeline on GitHub</h3></li>
						<li><a href="https://github.com/greenish/HGK-Ran/commits/master" target="_blank">HGK-Ran</a></li>
					</ul>
					<p class="info"> 
						<a href="http://www.philippadrian.com">Philipp C. Adrian</a><br>
						HGK Basel, Switzerland <br>
						Fall 2011				
					</p>
				</div>
			</div>
		</li>
		<li class="content">
			<?php
				if(file_exists($_GET["p"].".html")) include($_GET["p"].".html");
				else include("pcaRanWEBjs.html");
			?>
		</li>
	</ul>
	<!-- JavaScript at the bottom for fast mod project project1 loading -->
	<!-- Grab Google CDN's jQuery, with a protocol relative URL; fall back to local if necessary -->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6/jquery.js"></script>
	<script>window.jQuery || document.write("<script src='js/libs/jquery-1.6.min.js'>\x3C/script>")</script>
	<script src='js/plugins.js'></script>
	<script src="js/plugins/jquery.greenishSlides-v0.2-beta.js"></script>
	
	<?php
		if(file_exists($_GET["p"].".html") && $_GET["p"] != "pcaRanWEBjs") echo"
		<script src='js/ran.greenish.js'></script>";
	?>

	
	<!-- scripts concatenated and minified via ant build script-->
	<!-- end scripts-->
	
	
	<!-- mathiasbynens.be/notes/async-analytics-snippet Change UA-XXXXX-X to be your site's ID -->
	<script>
	if(false) {
		var _gaq=[["_setAccount","UA-XXXXX-X"],["_trackmod project project1view"],["_trackmod project project1LoadTime"]];
		(function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];g.async=1;
		g.src=("https:"==location.protocol?"//ssl":"//www")+".google-analytics.com/ga.js";
		s.parentNode.insertBefore(g,s)}(document,"script"));
	}
	</script>

</body>
</html>