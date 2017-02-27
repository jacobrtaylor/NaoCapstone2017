public void enableListening() throws Exception {

   // "VoiceCommand" will be the name to identify our subscription
   speech.subscribe("VoiceCommand");
   subscribeToWordRecognizedEvent();
   listening = true;
}
