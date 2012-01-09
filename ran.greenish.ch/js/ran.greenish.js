(function($) {
var ran={
////////////////////////////////////////////////////////////////////////////////
	behaviour: function (context) {
		
			$(".greenishSlides").greenishSlides({
				resizable:true,
				vertical:false,
				stayOpen:true,
				active:1
			});
			
			$(".greenishSlides .navigation, .greenishSlides .description").hoverIntent(function(){
				$(this).greenishSlides("activate");
				
			}, function(){
				$(".greenishSlides .applet").greenishSlides("activate");
			});
			
			
		
	}
}
////////////////////////////////////////////////////////////////////////////////
	$(document).ready(function() { 
		ran.behaviour($("body"));
	});
})(jQuery);
