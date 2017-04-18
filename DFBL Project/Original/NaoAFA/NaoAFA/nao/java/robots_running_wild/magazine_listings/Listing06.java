inputEventId = memory.subscribeToEvent("WordRecognized", (input)-> {
   if (listening) {
	  // Do something with the recognized word
	  handleOrder((String) ((List<Object>) input).get(0));
  }
});
