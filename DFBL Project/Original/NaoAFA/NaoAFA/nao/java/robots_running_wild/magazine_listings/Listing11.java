public void disableListening() throws Exception {
   tts.say("I'll stop listening.");
   speech.unsubscribe(“VoiceCommand”);
   memory.unsubscribeToEvent(inputEventId);
   listening = false;
   handleOrder("stop");
}
