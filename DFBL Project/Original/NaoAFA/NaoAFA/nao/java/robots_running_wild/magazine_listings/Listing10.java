touchEventId = 
  memory.subscribeToEvent( "MiddleTactilTouched", 
	(touched)->{
       if ((long)touched > 0) {
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
    });
