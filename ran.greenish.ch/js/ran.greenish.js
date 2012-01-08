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
			
			$(".greenishSlides .navigation").hoverIntent(function(){
				$(this).greenishSlides("activate");
				
			}, function(){
				$(".greenishSlides .content").greenishSlides("activate");
			});
			
			
		
	}
}
////////////////////////////////////////////////////////////////////////////////
	$(document).ready(function() { 
		ran.behaviour($("body"));
	});
})(jQuery);
