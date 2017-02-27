private void subscribeToWordRecognizedEvent() throws Exception {
   inputEventId = memory.subscribeToEvent("WordRecognized", new EventCallback<List<Object>>() {
	   @Override
	   public void onEvent(List<Object> input)
			   throws InterruptedException, CallError {
		   if (listening) {
			   // Do something with the recognized word
			   handleOrder((String) input.get(0));
		   }
	   }
   });
}
