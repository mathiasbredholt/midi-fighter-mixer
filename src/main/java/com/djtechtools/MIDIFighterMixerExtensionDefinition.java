package com.djtechtools;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class MIDIFighterMixerExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("c6f6ac9f-2097-476e-851a-46cce4c0b1cf");
   
   public MIDIFighterMixerExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "MIDI Fighter Mixer";
   }
   
   @Override
   public String getAuthor()
   {
      return "mathiasbredholt";
   }

   @Override
   public String getVersion()
   {
      return "0.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "DJ TechTools";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "MIDI Fighter Mixer";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 12;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 1;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 1;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public MIDIFighterMixerExtension createInstance(final ControllerHost host)
   {
      return new MIDIFighterMixerExtension(this, host);
   }
}
