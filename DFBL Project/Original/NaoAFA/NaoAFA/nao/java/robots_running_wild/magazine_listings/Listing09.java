long touchEventId = memory.subscribeToEvent("MiddleTactilTouched", new EventCallback<Float>() {
	   @Override
	   public void onEvent(Float touched) throws InterruptedException, CallError {
           // This means the sensor was touched.
		   if (touched > 0) {
			   System.out.println("Touched");
			   try {
				   if (listening == true) {
					   disableListening();
				   } else {
					   enableListening();
				   }
			   } catch (Exception e) {
				   e.printStackTrace();
			   }
		   }
	   }
   });
